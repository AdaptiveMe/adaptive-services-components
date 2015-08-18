# adaptive-services-components
Several components that can be used in distinct services for adaptive grouped in a single repo

## Notification ##

To enable notification in the application add the notification modules that your application will support.

For instance, if the application supports `EMAIL` notifications and wants the `Velocity` template engine the following modules should be added.

```
 		<dependency>
            <groupId>me.adaptive.services.components</groupId>
            <artifactId>adaptive-notification-email</artifactId>
            <version>${adaptive.services.compoments.version}</version>
        </dependency>
        <dependency>
            <groupId>me.adaptive.services.components</groupId>
            <artifactId>adaptive-notification-template-velocity</artifactId>
            <version>${adaptive.services.compoments.version}</version>
        </dependency>
```
You can then AutoWire the NotificationSender and send Notifications as follows

```
@Service
public class MyService{
    @Autowired
    NotificationSender notificationSender;
    
    void doSomething(){
     //Something happened in your code and a notification needs to be sent
      NotificationEntity notificationEntity = new NotificationEntity();
      notificationEntity.setStatus(NotificationStatus.CREATED);
      notificationEntity.setUserNotified(userEntity);
      notificationEntity.setEvent(NotificationEvent.USER_REGISTERED);
      notificationEntity.setChannel(NotificationChannel.EMAIL);
      notificationEntity.setDestination("someemail@somedomain.com");
      notificationSender.releaseNotification(notificationEntity);
      //the releaseNotification can be ommited if you're persisting the notification
      //a scheduled job sends pending notifications every minute
    
    }

}
```

## TODO ##

Create the modules for other channels and create templates for every support event in every supported channel
