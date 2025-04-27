package uk.co.mattwhitaker.pact_consumer_api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class HttpBinClient {

  private final WebClient webClient;

  public HttpBinClient(WebClient.Builder webClientBuilder, @Value("${httpbin.url}") String baseUrl) {
    this.webClient = webClientBuilder.baseUrl(baseUrl).build();
  }

  public Mono<HostNameModel> hostName() {
      return webClient.get().uri("/hostname").retrieve().bodyToMono(HostNameModel.class);
  }

}
