# Commons-logging

This library aims to leverage spring logging modules in its process to package the application logs
in a format that includes relevant metadata meant for monitoring and diagnosis of the application.

## Requirements

- JDK 25
- Maven builds that use this library should also run with Java 25 configured

# Usage

Add library dependency in the `pom.xml`.

  ```
  <dependency>
      <groupId>uk.co.whitbread.shared</groupId>
      <artifactId>commons-logging</artifactId>
      <version>X.X.X</version>
  </dependency>
  ```

Set the application name in the `bootstrap.yml`.

  ```
  spring:
    application:
      name: [YOUR_APP_NAME_HERE] 
  ```

:bangbang: This will setup the console logging pattern to FluentD compatible.

### Logging Patterns

To change the console logging pattern to a more user-friendly version add in the `application.yml`

  ```
  logging:
    configuration:
      logger: custom
  ```

To disable custom logging patterns entirely add in the `application.yml`

  ```
  logging:
    configuration:
      logger: default
  ```

### Masking

For masking sensitive data in the logs add in the `application.yml`

  ```
  logging:
    configuration:
      mask:
        enabled: true
        fields:
          - password
          - phoneNumber
  ```

:bangbang: With default logging pattern masking will not be applied.

### Extra Debugging

To filter out URI from being logged and avoid logs spam add in the `application.yml`. If none specified, the default `.*/actuator/.*` pattern will be uased

  ```
  logging:
    configuration:
      debug:
        exclusions:
          - /actuator/health
          - /actuator/info
  ```

To disable extra debug logs for request body and for response body add in the `application.yml`

  ```
  logging:
    configuration:
      debug:
        request:
          body: false
        response:
          body: false
  ```

:bangbang: URI exclusions will not be applied to request body and response body debug logs.

### Optional

If for any reason you want to disable the `X-Trace-Id` header, then add the following in the
`application.yml`

  ```
  logging:
    configuration:
        trace: false
  ```

# Test ELK locally

Read more about the local setup in this Confluence page: https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/3624206385/Logging+with+local+EFK+stack

