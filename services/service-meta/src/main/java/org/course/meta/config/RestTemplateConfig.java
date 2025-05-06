package org.course.meta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Description:
 *
 * @author zjy
 */
@Configuration
public class RestTemplateConfig {
     @Bean
     public RestTemplate restTemplate() {
         return new RestTemplate();
     }
}
