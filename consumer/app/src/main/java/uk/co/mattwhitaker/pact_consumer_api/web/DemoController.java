package uk.co.mattwhitaker.pact_consumer_api.web;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.co.mattwhitaker.pact_consumer_api.client.HttpBinClient;

@RestController
@RequestMapping("/demo")
public class DemoController {

  private final HttpBinClient httpBinClient;

  public DemoController(HttpBinClient httpBinClient) {
    this.httpBinClient = httpBinClient;
  }

  @GetMapping
  public Mono<Map<String, String>> demo() {
    return httpBinClient.hostName().flatMap(b -> Mono.just(Map.of("foo", "bar", "remoteHost", b.getHostname())));
  }

}
