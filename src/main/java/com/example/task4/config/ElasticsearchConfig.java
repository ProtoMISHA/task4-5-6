package com.example.task4.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("elasticsearch.port")
    private int port;
    @Value("elasticsearch.host")
    private String host;

    @Override
    @Bean
    public ClientConfiguration clientConfiguration() {
        String hostAndPort = String.format("%s:%d", host, port);
        return ClientConfiguration.builder().connectedTo(hostAndPort).build();

    }
}
