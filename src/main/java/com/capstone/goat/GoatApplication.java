package com.capstone.goat;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		servers = {
				@Server(url = "http://15.165.113.9:8080/", description = "Server URL"),
				@Server(url = "http://localhost:8080",description = "Local URL")
		},
		security = {
				@SecurityRequirement(name = "Auth"),
				@SecurityRequirement(name = "refresh"),
				@SecurityRequirement(name = "code")
		}
)
@SecuritySchemes({
		@SecurityScheme(name = "Auth",
				type = SecuritySchemeType.APIKEY,
				description = "JWT token",
				in = SecuritySchemeIn.HEADER,
				paramName = "Auth"),
		@SecurityScheme(name = "refresh",
				type = SecuritySchemeType.APIKEY,
				description = "JWT refresh token",
				in = SecuritySchemeIn.HEADER,
				paramName = "refresh"),
		@SecurityScheme(name = "code",
				type = SecuritySchemeType.APIKEY,
				description = "code",
				in = SecuritySchemeIn.HEADER,
				paramName = "code")
})
public class GoatApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoatApplication.class, args);
	}

}
