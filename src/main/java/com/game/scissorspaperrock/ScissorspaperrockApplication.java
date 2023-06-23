package com.game.scissorspaperrock;

import com.game.scissorspaperrock.secrets.ApplicationConfigProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationConfigProperties.class)
@OpenAPIDefinition(info = @Info(title = "Scissors Paper Rock Game"))
public class ScissorspaperrockApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScissorspaperrockApplication.class, args);
    }
}
