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
import me.adaptive.services.notification.error.NotificationException;

import java.util.Map;

/**
 * Created by panthro on 17/08/15.
 */
public interface NotificationService {

    /**
     * A unique service id that represents this service
     * eg: SMSService, PushNotificationAndroidService
     *
     * @return
     */
    String getServiceId();


    /**
     * Get this notification channel
     *
     * @return
     */
    NotificationChannel getChannel();

    /**
     * Notify based on the given {@code NotificationEntity}
     *
     * @param notification the {@code NotificationEntity}
     * @param model        the model to be merged, can be null
     * @throws NotificationException a fulfilled {@code NotificationException} in case an error has occurred.
     */
    void notify(NotificationEntity notification, Map<String, Object> model) throws NotificationException;


}
