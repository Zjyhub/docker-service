package org.course.docker.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @author zjy
 */
@Configuration
public class DockerConfig {
    @Bean(name = "dockerClient")
    public DockerClient connectDocker() {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withApiVersion("1.47")
                .withDockerHost("tcp://localhost:2375")
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .maxConnections(100)
                .build();

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);
        dockerClient.pingCmd().exec();
        System.out.println("dockerClient connect success");
        return dockerClient;
    }
}
