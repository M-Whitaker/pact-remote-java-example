package uk.co.mattwhitaker.test.ext;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

public class PactExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

  private final RestClient restClient = RestClient.builder()
      .baseUrl("http://localhost:8081")
      .defaultHeader("X-Pact-Mock-Service", "true")
      .defaultStatusHandler(res -> true)
      .build();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    ResponseEntity<Void> deleteRes = restClient.delete().uri("/interactions")
        .retrieve()
        .toBodilessEntity();
    assertEquals(HttpStatus.OK, deleteRes.getStatusCode());

    InputStream inputStream = PactExtension.class.getClassLoader().getResourceAsStream("interactions/consumer-interaction.json");
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readValue(inputStream, JsonNode.class);
    ResponseEntity<String> postRes = restClient.post().uri("/interactions")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(mapper.writeValueAsString(jsonNode))
        .retrieve()
        .toEntity(String.class);

    assertEquals(HttpStatus.OK, postRes.getStatusCode());
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    ResponseEntity<String> res = restClient.get().uri("/interactions/verification")
        .retrieve()
        .toEntity(String.class);

    assertAll(
        () -> assertEquals(HttpStatus.OK, res.getStatusCode()),
        () -> assertEquals("Interactions matched\n", res.getBody())
    );
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    ResponseEntity<String> res = restClient.get().uri("/").retrieve().toEntity(String.class);
    assertAll(
        () -> assertEquals(HttpStatus.OK, res.getStatusCode(), "Mock Server did not return correct status code"),
        () -> assertEquals(MediaType.TEXT_PLAIN, res.getHeaders().getContentType(), "Mock Server did not return correct content type"),
        () -> assertEquals("Mock service running\n", res.getBody(), "Mock Server is not running!")
    );
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    ResponseEntity<Void> res = restClient.post().uri("/pact").retrieve()
        .toBodilessEntity();

    assertEquals(HttpStatus.OK, res.getStatusCode());
  }
}
