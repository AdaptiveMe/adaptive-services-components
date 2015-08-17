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
import me.adaptive.services.notification.error.NotificationException;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by panthro on 17/08/15.
 */
@Service
public class NotificationSender {

    private static final int NOTIFICATION_BATCH = 100;


    Map<NotificationChannel, Collection<NotificationService>> notificationServices = new HashMap<>();


    ExecutorService executorService;

    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    NotificationErrorRepository notificationErrorRepository;

    @PostConstruct
    @Autowired(required = false)
    void init(Collection<NotificationService> services) {
        if (!CollectionUtils.isEmpty(services)) {
            executorService = Executors.newFixedThreadPool(services.size(), new CustomizableThreadFactory("NOTIFICATION-"));
            for (NotificationService service : services) {
                if (!notificationServices.containsKey(service.getChannel())) {
                    notificationServices.put(service.getChannel(), new HashSet<>());
                }
                notificationServices.get(service.getChannel()).add(service);
            }
        }
    }

    @PreDestroy
    void destroy() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }


    @Scheduled(fixedDelay = 2 * 60 * 1000)
        // x * 60 * 1000 where x = minutes
    void sendNotifications() {
        for (NotificationChannel channel : notificationServices.keySet()) {
            executorService.submit(new NotificationTask(channel));
        }
    }


    private class NotificationTask implements Runnable {

        NotificationChannel channel;

        public NotificationTask(NotificationChannel channel) {
            this.channel = channel;
        }

        @Transactional
        void notifyNow() {
            PageRequest pageRequest = new PageRequest(0, NOTIFICATION_BATCH);
            Page<NotificationEntity> page = notificationRepository.findByChannelAndStatus(channel, NotificationStatus.CREATED, pageRequest);
            //TODO maybe we need to queue these notifications now if we want multiple notifications servers in the future
            for (NotificationEntity notification : page) {
                for (NotificationService service : notificationServices.get(channel)) {
                    try {
                        service.notify(notification, new HashMap<>()); //TODO add stuff to the model depending on the notification event & channel
                        notification.setStatus(NotificationStatus.QUEUED);
                        notification = notificationRepository.save(notification);
                        notification.setSentDate(DateTime.now().toDate());
                        notification.setStatus(NotificationStatus.SENT);
                    } catch (NotificationException e) {
                        notification.setStatus(NotificationStatus.ERROR);
                        notificationRepository.save(notification);
                        if (e.getNotificationError().isPresent()) {
                            notificationErrorRepository.save(e.getNotificationError().get());
                        }
                    } finally {
                        notificationRepository.save(notification);
                    }

                }
            }
        }

        @Override
        public void run() {
            notifyNow();
        }
    }


}
