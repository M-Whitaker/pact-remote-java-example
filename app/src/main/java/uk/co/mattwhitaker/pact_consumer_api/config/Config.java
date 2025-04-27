package uk.co.mattwhitaker.pact_consumer_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

}
