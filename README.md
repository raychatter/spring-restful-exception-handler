# spring-restful-exception-handler

An annotation for the Spring framework to handle HTTP responses for custom exceptions. The way it works is you have to change the default exception resolver for Spring to our custom exception resolver by modifying your servlet xml file for Spring:

```xml
<bean id="exceptionResolver" class="com.github.raychatter.AnnotationHandler" />
```

After overriding the exception resolving mechanism, just annotate the custom exception classes with `@ExceptionHandler(*httpStatus*, *contentType*)`. The defaults are `httpStatus = HttpStatus.INTERNAL_SERVER_ERROR` and `contentType = MediaType.APPLICATION_XML_VALUE`. So an example exception class would be:

```java
@ExceptionHandler(httpStatus = HttpStatus.NOT_FOUND, contentType = MediaType.APPLICATION_XML_VALUE)
public class MyCustomException extends Exception {
   public MyCustomException(final String message) {
      super(message);
   }
}
```

The custom message is taken from the custom annotation class itself, so any parameters you'd like to insert need to be handled there.

And that's it! Just keep in mind that the exception handler will take care of all the exceptions and by default it will return `Internal Server Error` with an `XML` body described above. If you don't specify any custom template for your error responses, following error template will be used by default:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<error>
   %s
</error>
```

If you want to override the default template, make sure you have `error.template` in your classpath with `%s` for the message placeholder. An example for a `error.template` file in your classpath for a json response:

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

 ### Note:
 If an unannotated exception is thrown, the spring-restful-exception-handler will call `getCause()` until the first annotated exception is found. Provided there is no annotated exception, the default httpStatus and contentType will be used.
