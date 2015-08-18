
## Creating new Templates ##
The Engine expects the templates to be in the following structure

```
+-- src
|   +-- main
|       +-- resources
|           +-- <channel>
|               +-- <event>.vm
|               +-- <event>_title.vm
```

for instance a file located at

`./src/main/resources/email/user_registered.vm`

Will be used or `EMAIL` notifications for the event `USER_REGISTERED`
