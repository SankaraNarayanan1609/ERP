package com.Vcidex.StoryboardSystems;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;

public class RestAssuredBootstrap {
    static {
        RestAssured.config = RestAssuredConfig.config()
                .objectMapperConfig(new ObjectMapperConfig()
                        .jackson2ObjectMapperFactory((cls, charset) ->
                                new ObjectMapper()
                                        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                        )
                );
    }
}
