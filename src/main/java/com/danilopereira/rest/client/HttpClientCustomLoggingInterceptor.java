package com.danilopereira.rest.client;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Created by danilopereira on 22/03/17.
 */
public class HttpClientCustomLoggingInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientCustomLoggingInterceptor.class);

    private static final String LOG_REQUEST_RESPONSE_TEMPLATE = "\n>>>>>>>>>\n%s %s\n%s\n%s\n<<<<<<<<<\n%s %s\n%s\n%s\n";

    private static final String LOG_HEADER_REQUEST_RESPONSE_TEMPLATE = "%s: %s\n";


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        ClientHttpResponse response = execution.execute(request, body);

        doLog(request,body,response);

        return response;
    }

    private void doLog(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        LOG.info(generateLogString(request, body, response));
    }

    private String generateLogString(HttpRequest request, byte[] body, ClientHttpResponse response){
        StringBuffer logString = new StringBuffer();

        try {
            logString.append(String.format(LOG_REQUEST_RESPONSE_TEMPLATE,
                    request.getMethod(),
                    request.getURI(),
                    generateHeadersLogString(request.getHeaders()),
                    new String(body),
                    response.getStatusCode(),
                    response.getStatusText(),
                    generateHeadersLogString(response.getHeaders()),
                    IOUtils.toString(response.getBody(), StandardCharsets.UTF_8)
            ));
        } catch (IOException e) {
            LOG.error("Error generating log for request/response", e);
        }

        return logString.toString();
    }

    private String generateHeadersLogString(HttpHeaders headers){
        StringBuffer logHeaderString = new StringBuffer();
        Set<String> keys = headers.keySet();
        for (String key : keys) {
            logHeaderString.append(String.format(LOG_HEADER_REQUEST_RESPONSE_TEMPLATE, key, headers.get(key).get(0)));
        }
        return logHeaderString.toString();
    }
}
