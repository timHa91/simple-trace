package de.tim.tracec;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TraceExportConfig {

    @Bean
    @Qualifier("traceExport")
    public RestTemplate traceExport() {
        return new RestTemplate();
    }
}
