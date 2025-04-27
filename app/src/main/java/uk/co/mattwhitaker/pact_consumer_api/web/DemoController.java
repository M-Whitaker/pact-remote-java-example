package uk.co.mattwhitaker.pact_consumer_api.web;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.co.mattwhitaker.pact_consumer_api.client.HttpBinClient;

@RestController
@RequestMapping("/demo")
public class DemoController {

  private static final Logger log = LoggerFactory.getLogger(DemoController.class);
  private final HttpBinClient httpBinClient;

  public DemoController(HttpBinClient httpBinClient) {
    this.httpBinClient = httpBinClient;
  }

  @GetMapping
  public Mono<Map<String, String>> demo() {
    return httpBinClient.anything().flatMap(b -> {
      log.info(b);
      return Mono.just(Map.of("foo", "bar"));
    });
  }

}
