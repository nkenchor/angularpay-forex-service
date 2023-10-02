package io.angularpay.forex.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("angularpay")
@Data
public class AngularPayConfiguration {

    private int codecSizeInMB;
    private Redis redis;
    private Fixer fixer;

    @Data
    public static class Redis {
        private String host;
        private int port;
        private int timeout;
    }

    @Data
    public static class Fixer {
        private String baseUrl;
        private String apiKey;
    }
}
