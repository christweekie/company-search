package org.chrisfaulkner.config;

import org.chrisfaulkner.web.CompanyOfficerClient;
import org.chrisfaulkner.web.CompanyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfiguration {



    @Bean
    public WebClient webClient(@Value("${base.search-url}") String baseUrl,
                                @Value("${secret-api-key:some-value}") String apiKey,
                                @Value("${web.client.response-timeout-ms:30000}") long responseTimeoutMs) {
        final var httpClient = HttpClient.create()
            .responseTimeout(Duration.ofMillis(responseTimeoutMs));

        return WebClient.builder()
            .defaultHeaders(header -> header.setContentType(MediaType.APPLICATION_JSON))
            .defaultHeaders(header -> header.set("x-api-key", apiKey))
            .defaultStatusHandler(HttpStatusCode::isError, resp ->
                Mono.just(new CompanyException("Error creating WebClient")))
            .baseUrl(baseUrl)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }

    @Bean
    public CompanyOfficerClient riskNarrativeClient(WebClient webClient) {
        final var webClientAdapter = WebClientAdapter.create(webClient);

        return HttpServiceProxyFactory
                .builderFor(webClientAdapter)
                .build()
                .createClient(CompanyOfficerClient.class);
    }
}
