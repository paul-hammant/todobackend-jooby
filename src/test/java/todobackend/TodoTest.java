package todobackend;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.jooby.test.JoobyRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class TodoTest {

  /**
   * One app/server for all the test of this class. If you want to start/stop a new server per test,
   * remove the static modifier and replace the {@link ClassRule} annotation with {@link Rule}.
   */
  @ClassRule
  public static JoobyRule app = new JoobyRule(new App());

  @BeforeClass
  public static void saveOne() {
    given()
        .contentType("application/json")
        .body("{\"title\": \"Jooby rocks!\"}")
        .post("/todos")
        .then()
        .assertThat()
        .body(equalTo(
            "{\"id\":1,\"title\":\"Jooby rocks!\",\"completed\":false,\"order\":0,\"url\":\"http://localhost:8080/todos/1\"}"))
        .statusCode(201)
        .contentType("application/json;charset=UTF-8");
  }

  @Test
  public void list() {
    get("/todos")
        .then()
        .assertThat()
        .body(equalTo(
            "[{\"id\":1,\"title\":\"Jooby rocks!\",\"completed\":false,\"order\":0,\"url\":\"http://localhost:8080/todos/1\"}]"))
        .header("Access-Control-Allow-Origin", equalTo("*"))
        .header("Access-Control-Allow-Methods", equalTo("POST, GET, OPTIONS, DELETE, PATCH"))
        .header("Access-Control-Max-Age", equalTo("3600"))
        .header("Access-Control-Allow-Headers", "accept")
        .statusCode(200)
        .contentType("application/json;charset=UTF-8");
  }

  @Test
  public void getById() {
    get("/todos/1")
        .then()
        .assertThat()
        .body(equalTo(
            "{\"id\":1,\"title\":\"Jooby rocks!\",\"completed\":false,\"order\":0,\"url\":\"http://localhost:8080/todos/1\"}"))
        .statusCode(200)
        .contentType("application/json;charset=UTF-8");
  }

  @Test
  public void merge() {
    given()
        .contentType("application/json")
        .body("{\"completed\": true}")
        .patch("/todos/1")
        .then()
        .assertThat()
        .body(equalTo(
            "{\"id\":1,\"title\":\"Jooby rocks!\",\"completed\":true,\"order\":0,\"url\":\"http://localhost:8080/todos/1\"}"))
        .statusCode(200)
        .contentType("application/json;charset=UTF-8");

    deleteById();
    deleteAll();
  }

  public void deleteById() {
    delete("/todos/1")
        .then()
        .assertThat()
        .statusCode(204);
  }

  public void deleteAll() {
    delete("/todos")
        .then()
        .assertThat()
        .statusCode(204);
  }

}
