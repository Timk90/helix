package com.helixtesttask.helixdemo.configuration;

import com.helixtesttask.helixdemo.dto.Credentials;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "authentication")
public class CredentialsProperties {
    private Map<String, Credentials> credentials;
}
