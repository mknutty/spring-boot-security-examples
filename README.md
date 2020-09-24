# Spring Boot Security Example

Demonstrates using JWT to manage user identity and granted authorities.

Look at ExampleController for usage and more info.

By default the application will use Springs Default JWT code. This uses 
'user_name' and 'authorities' fields in the JWT. To use the custom UserAuthenticationConverter, set the active profile to 'custom'. This will use 'name' and 'admin' fields in the JWT.

To get a JWT, call the /jwt endpoint to get a JWT to pass as the bearer token.