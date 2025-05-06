package org.course.meta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Description:
 *
 * @author zjy
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class MetaMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetaMainApplication.class, args);
    }
}
