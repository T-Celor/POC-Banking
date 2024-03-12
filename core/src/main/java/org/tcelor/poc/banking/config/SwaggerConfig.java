package org.tcelor.poc.banking.config;


import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

import jakarta.ws.rs.core.Application;


@OpenAPIDefinition(
    info = @Info(
        title="POC Banking - Core Services",
        version = "1.0.0",
        contact = @Contact(
            name = "Tristan CELOR"),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0.html"))
)
public class SwaggerConfig extends Application {}