# spring-restful-exception-handler
### An annotation for the Spring framework to handle HTTP responses for custom exceptions.

## How to use spring-restful-exception-handler
Under PROJECT/resources create a file called *error.template*. Inside this file, place the formatted error template you want to return when a custom exception is thrown. If no custom template is given, the following default template will be used:

```<?xml version="1.0" encoding="UTF-8"?> <error> %s </error>```

Annotate the custom exception class with `@Exception(*httpStatus*, *contentType*)`.
The defaults are `httpStatus = HttpStatus.INTERNAL_SERVER_ERROR` and `contentType = MediaType.APPLICATION_XML_VALUE`.

The custom message is taken from the custom annotation class itself, so any parameters you'd like to insert need to be handled there.

Make sure to add `<bean id="exceptionResolver" class="com.github.raychatter.AnnotationHandler" />` to your XML.

And that's it!
