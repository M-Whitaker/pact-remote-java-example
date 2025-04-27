package uk.co.mattwhitaker.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.mattwhitaker.test.ext.PactExtension;

@ExtendWith(PactExtension.class)
class ExampleTest {

  @Test
  void example() {
    given()
        .get("http://localhost:8080/demo")
        .then()
        .statusCode(200)
        .body("foo", equalTo("bar"))
        .body("remoteHost", equalTo("go-httpbin"));
  }

}
