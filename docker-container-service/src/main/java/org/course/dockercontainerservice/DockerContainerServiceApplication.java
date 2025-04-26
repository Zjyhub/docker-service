package org.course.dockercontainerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Description:
 *
 * @author zjy
 */
@SpringBootApplication
@EnableDiscoveryClient
public class DockerContainerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DockerContainerServiceApplication.class, args);
    }
}
