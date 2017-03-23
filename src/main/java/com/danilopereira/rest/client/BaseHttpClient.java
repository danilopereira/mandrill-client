package com.danilopereira.rest.client;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * Created by danilopereira on 22/03/17.
 */
public class BaseHttpClient {

    protected RestTemplate restTemplate;

    public BaseHttpClient(Integer connectionTimeout, Integer readTimeout, Integer maxConnections, String username, String password){
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        if (!StringUtils.isEmpty(username) || !StringUtils.isEmpty(password)){
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }

        HttpClient httpClient = httpClientBuilder
                .setMaxConnPerRoute(maxConnections)
                .setMaxConnTotal(maxConnections)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        factory.setConnectTimeout(connectionTimeout);
        factory.setConnectionRequestTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);

        HttpClientCustomLoggingInterceptor httpClientCustomLoggingInterceptor = new HttpClientCustomLoggingInterceptor();
        this.restTemplate.setInterceptors(Arrays.asList(new ClientHttpRequestInterceptor[] {httpClientCustomLoggingInterceptor}));
        //this.restTemplate.setRequestFactory(factory);
        this.restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(factory));
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
