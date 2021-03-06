/*
 * Copyright 2014-2015. Adaptive.me.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package me.adaptive.services.notification;

import me.adaptive.core.data.domain.NotificationEntity;
import me.adaptive.core.data.domain.types.NotificationChannel;
import me.adaptive.core.data.domain.types.NotificationStatus;
import me.adaptive.core.data.repo.NotificationErrorRepository;
import me.adaptive.core.data.repo.NotificationRepository;
import me.adaptive.core.data.util.SystemSettingHolder;
import me.adaptive.services.notification.error.NotificationException;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by panthro on 17/08/15.
 */
@Service
public class NotificationSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationSender.class);

    private static final int NOTIFICATION_BATCH = 100;
    private static final long NOTIFICATION_MAX_EXEC_TIME_MIN = 10;


    Map<NotificationChannel, Collection<NotificationService>> notificationServices = new HashMap<>();

    @Autowired(required = false)
    Collection<NotificationService> services;

    ExecutorService executorService;

    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    NotificationErrorRepository notificationErrorRepository;

    @PostConstruct
    void init() {
        LOGGER.info("Initializing");
        if (!CollectionUtils.isEmpty(services)) {
            executorService = Executors.newFixedThreadPool(services.size(), new CustomizableThreadFactory("NOTIFICATION-"));
            for (NotificationService service : services) {
                if (!notificationServices.containsKey(service.getChannel())) {
                    notificationServices.put(service.getChannel(), new HashSet<>());
                }
                notificationServices.get(service.getChannel()).add(service);
            }
        }
        LOGGER.info("NotificationSender initialized with {} NotificationServices with the Channels {}", services.size(), Arrays.toString(notificationServices.keySet().stream().map(Enum::toString).toArray()));
    }

    @PreDestroy
    void destroy() {
        LOGGER.info("Shut down executor service");
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.info("Could not shut down executor service, tasks are still being executed, forcing the shutdown");
            executorService.shutdownNow();
        }
    }


    @Scheduled(fixedDelay = 60000)
        //sends notifications every minute
    void sendNotifications() {
        LOGGER.info("Sending pending notifications");
        for (NotificationChannel channel : notificationServices.keySet()) {
            executorService.submit(new NotificationChannelTask(channel));

            try {
                executorService.awaitTermination(NOTIFICATION_MAX_EXEC_TIME_MIN, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                LOGGER.warn("Could not send notifications in {} minutes, this most likely will cause overlapping of notifications", NOTIFICATION_MAX_EXEC_TIME_MIN);
            }
        }
    }

    @SuppressWarnings("unused")
    @Async
    public void releaseNotification(NotificationEntity notification) {
        doReleaseNotification(notification, new HashMap<>());
    }

    @SuppressWarnings("unused")
    @Async
    /**
     * Releases the given notification with the given model
     */
    public void releaseNotification(NotificationEntity notification, Map model) {
        doReleaseNotification(notification, model);
    }

    private void doReleaseNotification(NotificationEntity notification, Map<String, Object> model) {
        model = addCommonSettings(model);
        for (NotificationService service : notificationServices.get(notification.getChannel())) {
            try {
                notification.setStatus(NotificationStatus.QUEUED);
                notification = notificationRepository.save(notification);
                service.notify(notification, model);
                notification.setSentDate(DateTime.now().toDate());
                notification.setStatus(NotificationStatus.SENT);
                LOGGER.info("Notification {} sent to the channel {} trough the service {} ", notification.getId(), notification.getChannel(), service.getServiceId());
            } catch (NotificationException e) {
                LOGGER.info("Error sending Notification {} sent to the channel {} trough the service {} ", notification.getId(), notification.getChannel(), service.getServiceId());
                if (!NotificationStatus.SENT.equals(notification.getStatus())) { //Only set as an error if the notification did not succeed for any other channel
                    notification.setStatus(NotificationStatus.ERROR);
                }
                if (e.getNotificationError().isPresent()) { //The error still should be persisted in any case
                    notificationErrorRepository.save(e.getNotificationError().get());
                }
            }
        }
        notificationRepository.save(notification);
    }

    private Map<String, Object> addCommonSettings(Map<String, Object> model) {
        if (model == null) {
            model = new HashMap<>(SystemSettingHolder.getAll());
        } else {
            model.putAll(SystemSettingHolder.getAll());
        }
        return model;
    }

    private class NotificationChannelTask implements Runnable {

        NotificationChannel channel;

        public NotificationChannelTask(NotificationChannel channel) {
            this.channel = channel;
        }

        @Override
        public void run() {
            LOGGER.info("Sending notifications for channel {}", channel.toString());
            PageRequest pageRequest = new PageRequest(0, NOTIFICATION_BATCH);
            Page<NotificationEntity> page = notificationRepository.findByChannelAndStatus(channel, NotificationStatus.CREATED, pageRequest);
            //TODO maybe we need to queue these notifications now if we want multiple notifications servers in the future
            LOGGER.info("A total of {} notifications will be sent in this batch for channel {}", page.getNumberOfElements(), channel.toString());
            for (NotificationEntity notification : page) {
                doReleaseNotification(notification, new HashMap<>(SystemSettingHolder.getAll()));
            }
        }
    }

}
