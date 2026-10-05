# commons-entity-exceptions

Whitbread common error handling library for the Entity and System layers

# Hierarchy

```
ExceptionInterface
 └--generic
    |---AbstractBadRequestException 
    |---AbstractInternalException     
    |---AbstractNotFoundException     
    |---AbstractBusinessViolationException 
    .
    .
    .
    └---future implementations
```

# Technologies

* Java 25
* Maven 3+

# Usage

For Maven projects:

```xml

<dependency>
  <groupId>uk.co.whitbread.shared</groupId>
  <artifactId>commons-entity-exceptions</artifactId>
  <version>{latest-version}</version>
</dependency>
```

The error codes need to be part of every exception that occurs in Digital. The error code range is:
-->PMS : 900-999;
-->AEM : 800-899;
-->Databases : 700-799;
-->Payments : 600-699;
-->BAU & Other external systems : 500-599;
-->Validation errors : 400-499;
-->Digital: 00-299

Exceptions should be propagated and keep their error code across microservices in order to provide
relevant tracing and proper error debugging.
External exceptions from outside services need to be wrapped in a Digital exception, containing an
error message, but keeping the original message from the external system.

1. if the existing abstract classes do not match your needs then create a new generic exception,
   which is supposed to implement Property Management System marker interface (ExceptionInterface)
   and create custom mechanism to handle message, debug message and error code.

2. In the digital microservices, the user extends the Abstract{service/layer/etc.}Exception which
   already contains the mechanism.

All exceptions derived from the abstract classes are handled in their BusinessExceptionHandler advice.
GlobalExceptionHandler catches any uncaught exceptions using ValidationError enum for the error
codes and the custom messages.


Example exception - this would be implemented in other microservices:

```java
public class ActualException extends AbstractBadRequestException {

  public ActualException(String message, String debugMessage, int errorCode) {
    super(message, debugMessage, errorCode);
  }

  public ActualException(String message, String debugMessage, Throwable cause, int errorCode) {
    super(message, debugMessage, cause, errorCode);
  }
}
```

Response example:

1. Validation exception. Create a class that extends AbstractBadRequestException:

```json
{
  "errCode": 100,
  "debugMessage": "Validation failed for argument [0] in public org.springframework.http.ResponseEntity<java.lang.Void> uk.co.whitbread.payments.infrastructure.rest.controller.payment.PaymentMethodsController.validateSelectedPaymentMethod(uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.in.SelectedPaymentMethodsDto) with 2 errors: [Field error in object 'selectedPaymentMethodsDto' on field 'size': rejected value [3]; codes [Min.selectedPaymentMethodsDto.size,Min.size,Min.java.lang.Integer,Min]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [selectedPaymentMethodsDto.size,size]; arguments []; default message [size],18]; default message [Age should not be less than 18]] [Field error in object 'selectedPaymentMethodsDto' on field 'selectedPaymentOption': rejected value [null]; codes [NotEmpty.selectedPaymentMethodsDto.selectedPaymentOption,NotEmpty.selectedPaymentOption,NotEmpty.java.lang.String,NotEmpty]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [selectedPaymentMethodsDto.selectedPaymentOption,selectedPaymentOption]; arguments []; default message [selectedPaymentOption]]; default message [must not be empty]] ",
  "globalErrTextTemplate": "valdation_error_form",
  "details": [
    {
      "elementId": "size",
      "errTextTemplate": "size.validation.error"
    },
    {
      "elementId": "selectedPaymentOption",
      "errTextTemplate": "selectedPaymentOption.validation.error"
    }
  ]
}
```

2. Business validation exception. Create a class that extends AbstractBusinessValidationException

```json
{
  "errCode": 600,
  "debugMessage": "A fraud check was triggered and the payment transaction was declined for paymentId 123456789",
  "globalErrTextTemplate": "fraud.check.failed"
}
```

3. Internal server error. Create a class that extends AbstractInternalException

```json
{
  "errCode": 101,
  "debugMessage": "Connection refused executing GET http://localhost:8087/reservations/basket/TKINPT0059720",
  "globalErrTextTemplate": "internal.server.exception"
}
```

4. AbstractNotFoundException. Create a class that extends AbstractNotFoundException

```json
{
  "errCode": 102,
  "debugMessage": "Basket by basket reference TKP12345 was not found!",
  "globalErrTextTemplate": "basket.not.found"
}
```

# GraphQL Error Handling

The sample resolver will utilise the AppSync Error model to inform the consumer of any exception propagated by the
downstream services. The solution, essentially leverages the `$util.error(message, errorType?, data?, errorInfo?)` to
fill in the out-of-the-box model with details.

Appsync overrides the errorCode with the html status code:

```apache
## Raise a GraphQL field error in case of a downstream invocation error
#if($ctx.error)
  #set( $errorMsg = "appsync.downstream.error" )
  #set( $errorInfo = { "errCode": "$ctx.result.statusCode" ,"debugMessage": "$ctx.error.message", "globalErrTextTemplate": "$errorMsg"}) 
  $util.error($errorMsg, "$ctx.result.statusCode", {}, $errorInfo)
#end
## If the response is not 200 then return an error. Else return the body **
#if($ctx.result.statusCode == 200)
  $ctx.result.body
#else
  #set( $parsed_body = $util.parseJson($ctx.result.body) )
  #set( $errorMsg = $util.defaultIfNullOrBlank($parsed_body.globalErrTextTemplate,"appsync.global.error") )
  $util.error($errorMsg, "$ctx.result.statusCode", {}, $parsed_body)
#end
```

For this to make more sense, an entity response like below:

```json
{
  "errCode": 600,
  "debugMessage": "A fraud check was triggered and the payment transaction was declined for paymentId 123456789",
  "globalErrTextTemplate": "fraud check failed"
}
```

After Appsync processing, it will end up like below:

```json
{
  "data": {
    "something": null
  },
  "errors": [
    {
      "path": [
        "something"
      ],
      "data": {
        "responseData": null
      },
      "errorType": "409",
      "errorInfo": {
        "errCode": 409,
        "debugMessage": "A fraud check was triggered and the payment transaction was declined for paymentId 123456789",
        "globalErrTextTemplate": "fraud.check.failed"
      },
      "locations": [
        {
          "line": 2,
          "column": 3,
          "sourceName": null
        }
      ],
      "message": "fraud.check.failed"
    }
  ]
}
```

**_NOTE:_** API consumers should seek the `errorInfo` object when handling erroneous behavior. The reason is that for any
AppSync internal exception like templating or syntax problems, the response model won't follow the rules described
above as they are out of our control and fall back to default.

Example:

```json 
{
    "data": null,
    "errors": [
        {
            "path": null,
            "locations": [
                {
                    "line": 4,
                    "column": 7,
                    "sourceName": null
                }
            ],
            "message": "Validation error of type FieldUndefined: Field 'hotelNam' in type 'Availability' is undefined @ 'something/responseData/hotelNam'"
        }
    ]
}
```
