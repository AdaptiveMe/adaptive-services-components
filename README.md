# adaptive-services-components
Several components that can be used in distinct services for adaptive grouped in a single repo

## Notification ##

To enable notification in the application the notification modules that your application will support.

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


## TODO ##

Create the modules for other channels and create templates for every support event in every supported channel