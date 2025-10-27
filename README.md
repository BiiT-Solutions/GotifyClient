Library to send Push notifications through an existing Gotify Server.

# HowTo use this library on your project

Add the dependency on the maven pom.xml

```
        <dependency>
            <groupId>com.biit-solutions</groupId>
            <artifactId>gotify-client</artifactId>
            <version>${gotify-client.version}</version>
        </dependency>
```

And include the packet `com.biit.gotify.client` on the `@ComponentScan` annotation

```
@ComponentScan({"com.biit.project", "com.biit.gotify.client"})
```

And finally, remember to include `com.biit.gotify.logger.GotifyLogger` to the logback configuration file.

## Configuration

For configuring, please set the next properties in the application.properties from your project:

```
gotify.server.url=
gotify.application.token=
gotify.application.id=
gotify.user=
gotify.password=
```

Where, the `application token` is obtained from the Gotify UX when registering a new application. The `user`
and `password` is the user used when logged in on the Gotify UX and the application id is obtained from the result of:

```
curl -X GET "https://gotify.server.com/application" -H  "accept: application/json" -H  "authorization: Basic dGVzdDphc2QxMjM="
```

The best way of generating the CURL and its `authorization` header is accessing to the swagger
page `https://gotify.server.com/docs`.

### Final Remarks

Gotify user can define multiple applications. But the Gotify mobile app receives all messages from all applications from
the logged-in user. For multiple push notifications systems, or you create multiple applications for the same user (he
will receive all notifications) or create multiples user to only receive a subset of notifications, but only one user
will receive it.