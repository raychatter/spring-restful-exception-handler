# spring-restful-exception-handler

An annotation for the Spring framework to handle HTTP responses for custom exceptions.

## How to use spring-restful-exception-handler

Under PROJECT/resources create a file called *error.template*. Inside this file, place the formatted error template you want to return when a custom exception is thrown. If no custom template is given, the following default template will be used:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<error> %s </error>
```

Annotate the custom exception class with `@ExceptionHandler(*httpStatus*, *contentType*)`. The defaults are `httpStatus = HttpStatus.INTERNAL_SERVER_ERROR` and `contentType = MediaType.APPLICATION_XML_VALUE`. So an example exception class would be:

```java
@ExceptionHandler(httpStatus = HttpStatus.NOT_FOUND, contentType = MediaType.APPLICATION_XML_VALUE)
public class MyCustomException extends Exception {
   public MyCustomException(final String message) {
      super(message);
   }
}
```

The custom message is taken from the custom annotation class itself, so any parameters you'd like to insert need to be handled there.

Make sure to add following to your servlet XML:

```xml
<bean id="exceptionResolver" class="com.raychatter.common.annotation.AnnotationHandler" />
```

And that's it! Just keep in mind that the exception handler will take care of all the exceptions and by default it will return `Internal Server Error` with an `XML` body described above. If you want to override, make sure you have `error.template` in your classpath with `%s` for the message placeholder. An example for a `error.template` file in your classpath for a json response:

```json
{
   "message": "%s"
}
```

And now you need to make sure that your exceptions are returning `json` content type.

```java
@ExceptionHandler(httpStatus = HttpStatus.CONFLICT, contentType = MediaType.APPLICATION_JSON_VALUE)
public class MyCustomException extends Exception {
   public MyCustomException(final String message) {
      super(message);
   }
}
```
