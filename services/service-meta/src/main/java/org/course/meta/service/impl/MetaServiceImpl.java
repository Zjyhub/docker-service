package org.course.meta.service.impl;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import org.course.container.ContainerVo;
import org.course.meta.model.ContainerStatVO;
import org.course.meta.service.MetaService;
import org.course.meta.util.DockerStatsConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zjy
 */
@Service
public class MetaServiceImpl implements MetaService {

    private final DiscoveryClient discoveryClient;

    private final RestTemplate restTemplate;

    @Autowired
    public MetaServiceImpl(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ContainerVo> getContainerList() {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        List<ContainerVo> containerVoList = new LinkedList<>();
        for (ServiceInstance instance : instances) {
            String url = instance.getUri().toString() + "/docker/containers";

            ContainerVo[] obj = restTemplate.getForObject(url, ContainerVo[].class);
            if (obj != null) {
                for (ContainerVo container : obj) {
                    container.setHost(instance.getHost());
                }
                containerVoList.addAll(Arrays.asList(obj));
            }
        }
        return containerVoList;
    }

    @Override
    public Container getContainerInfo(String id, String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/info?id=" + id;
                Container obj = restTemplate.getForObject(url, Container.class);
                return obj;
            }
        }
        return null;
    }

    @Override
    public Boolean startContainer(String id, String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/start?id=" + id;
                Boolean obj = restTemplate.postForObject(url, null, Boolean.class);
                return obj;
            }
        }
        return false;
    }

    @Override
    public Boolean stopContainer(String id, String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/stop?id=" + id;
                Boolean obj = restTemplate.postForObject(url, null, Boolean.class);
                return obj;
            }
        }
        return false;
    }

    @Override
    public Boolean removeContainer(String id, String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/remove?id=" + id;
                Boolean obj = restTemplate.postForObject(url, null, Boolean.class);
                return obj;
            }
        }
        return false;
    }

    @Override
    public Boolean restartContainer(String id, String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/restart?id=" + id;
                Boolean obj = restTemplate.postForObject(url, null, Boolean.class);
                return obj;
            }
        }
        return false;
    }
    
    @Override
    public Statistics getContainerStats(String id, String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/stats/" + id;
                Statistics stats = restTemplate.getForObject(url, Statistics.class);
                return stats;
            }
        }
        return null;
    }
    
    @Override
    public Map<String, Statistics> getAllContainerStats(String host) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        for (ServiceInstance instance : instances) {
            if (host.equals(instance.getHost())) {
                String url = instance.getUri().toString() + "/docker/stats/all";
                
                ResponseEntity<Map<String, Statistics>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Statistics>>() {}
                );
                
                return response.getBody();
            }
        }
        return Collections.emptyMap();
    }
    
    @Override
    public Map<String, Map<String, Statistics>> getAllNodesContainerStats() {
        Map<String, Map<String, Statistics>> result = new HashMap<>();
        List<ServiceInstance> instances = discoveryClient.getInstances("service-docker");
        
        for (ServiceInstance instance : instances) {
            String host = instance.getHost();
            Map<String, Statistics> hostStats = getAllContainerStats(host);
            if (!hostStats.isEmpty()) {
                result.put(host, hostStats);
            }
        }
        
        return result;
    }
    
    @Override
    public Map<String, ContainerStatVO> getAllNodesContainerStatsVO() {
        Map<String, ContainerStatVO> result = new HashMap<>();
        Map<String, Map<String, Statistics>> allNodesStats = getAllNodesContainerStats();
        
        for (Map.Entry<String, Map<String, Statistics>> nodeEntry : allNodesStats.entrySet()) {
            String host = nodeEntry.getKey();
            Map<String, Statistics> containerStats = nodeEntry.getValue();
            
            for (Map.Entry<String, Statistics> containerEntry : containerStats.entrySet()) {
                String containerId = containerEntry.getKey();
                Statistics stats = containerEntry.getValue();
                
                // 获取容器详细信息
                Container container = getContainerInfo(containerId, host);
                if (container != null) {
                    ContainerStatVO statVO = DockerStatsConverter.convertToContainerStatVO(stats, container, host);
                    if (statVO != null) {
                        result.put(containerId, statVO);
                    }
                }
            }
        }
        
        return result;
    }
    
    @Override
    public List<ContainerStatVO> checkContainersStatus(double cpuThreshold, double memoryThreshold) {
        Map<String, ContainerStatVO> allStats = getAllNodesContainerStatsVO();
        List<ContainerStatVO> warnings = new ArrayList<>();
        for (ContainerStatVO stat : allStats.values()) {
            boolean hasWarning = false;
            StringBuilder warningMessage = new StringBuilder();
            
            // 检查CPU使用率
            if (stat.getCpuUsagePercentage() > cpuThreshold) {
                hasWarning = true;
                warningMessage.append("CPU使用率过高: ").append(stat.getCpuUsagePercentage()).append("% > ")
                        .append(cpuThreshold).append("%");
            }
            
            // 检查内存使用率
            if (stat.getMemoryUsagePercentage() > memoryThreshold) {
                if (hasWarning) {
                    warningMessage.append("; ");
                }
                hasWarning = true;
                warningMessage.append("内存使用率过高: ").append(stat.getMemoryUsagePercentage()).append("% > ")
                        .append(memoryThreshold).append("%");
            }
            
            if (hasWarning) {
                stat.setStatus("WARNING");
                stat.setWarningMessage(warningMessage.toString());
                warnings.add(stat);
            }
        }
        
        return warnings;
    }
}
