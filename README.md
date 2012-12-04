# spring-restful-exception-handler
### An annotation for the Spring framework to handle HTTP responses for custom exceptions.

## How to use spring-restful-exception-handler
Under PROJECT/resources create a file called *error.template*. Inside this file, place the formatted error template you want to return when a custom exception is thrown. If no custom template is given, the following default template will be used:

`<?xml version="1.0" encoding="UTF-8"?>`
`<error>`
`%s`
`</error>`

Annotate the custom exception class with `@Exception(*httpStatus*, *contentType*)`.
The defaults are
`httpStatus = HttpStatus.INTERNAL_SERVER_ERROR`
`contentType = MediaType.APPLICATION_XML_VALUE`
Make sure to add `com.raychatter.common.annotation.AnnotationHandler` as a bean in your XML.
And that's it!


