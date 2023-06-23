package com.game.scissorspaperrock.secrets;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application.jwt")
public record ApplicationConfigProperties(String secret) {
}
