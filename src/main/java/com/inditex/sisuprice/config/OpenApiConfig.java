package com.inditex.sisuprice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "wsc-sisupricingengine API",
                version = "v1",
                description = "Interactive API documentation for the SISU Pricing Engine service.",
                contact = @Contact(name = "Inditex", url = "https://www.inditex.com/"),
                license = @License(name = "Apache-2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        )
)
public class OpenApiConfig {}
