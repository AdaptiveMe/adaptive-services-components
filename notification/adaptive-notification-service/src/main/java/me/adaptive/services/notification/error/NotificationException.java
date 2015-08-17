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

package me.adaptive.services.notification.error;

import me.adaptive.core.data.domain.NotificationErrorEntity;

import java.util.Optional;

/**
 * Created by panthro on 17/08/15.
 */
public class NotificationException extends Exception {

    private final Optional<NotificationErrorEntity> notificationError;

    public Optional<NotificationErrorEntity> getNotificationError() {
        return notificationError;
    }

    public NotificationException(NotificationErrorEntity notificationError) {
        this.notificationError = Optional.ofNullable(notificationError);
        ;
    }

    public NotificationException(String message, NotificationErrorEntity notificationError) {
        super(message);
        this.notificationError = Optional.ofNullable(notificationError);
        ;
    }

    public NotificationException(String message, Throwable cause, NotificationErrorEntity notificationError) {
        super(message, cause);
        this.notificationError = Optional.ofNullable(notificationError);
        ;
    }
}
