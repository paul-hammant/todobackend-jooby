package todobackend;

import org.jooby.Request;
import org.jooby.Results;
import org.jooby.Status;
import org.junit.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.seleniumhq.selenium.fluent.FluentWebDriver;
import org.seleniumhq.selenium.fluent.FluentWebElement;
import org.seleniumhq.selenium.fluent.TestableString;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;
import static org.seleniumhq.selenium.fluent.Period.secs;

public class TodoWebDriverTest {

  public static class TestApp extends App {

    private boolean appStarted;

    public TestApp() {
      onStarted(() -> appStarted = true);
    }

    @Override
    protected Object patchTodo(Request req) throws Exception {
      throw new UnsupportedOperationException("not expected");
    }

    @Override
    protected Object deleteAllTodos() {
      throw new UnsupportedOperationException("not expected");
    }

    @Override
    protected Object deleteTodoById(Request req) {
      throw new UnsupportedOperationException("not expected");
    }

    @Override
    protected Object createNewTodo(Request req) throws Exception {
      throw new UnsupportedOperationException("not expected");
    }

    @Override
    protected Object getTodoById(Request req) {
      throw new UnsupportedOperationException("not expected");
    }

    @Override
    protected Object getAllTodos() {
      throw new UnsupportedOperationException("not expected");
    }

  }

  private static ChromeDriver DRIVER;
  private static FluentWebDriver FWD;
  private static int testNum;

  private TestApp app;
  private Todo todo;
  private int id;

  @BeforeClass
  public static void sharedForAllTests() {
    // Keep the WebDriver browser window open between tests
    DRIVER = new ChromeDriver();
    FWD = new FluentWebDriver(DRIVER);
  }

  @AfterClass
  public static void tearDown() {
    DRIVER.close();
    DRIVER.quit();
  }

  private String domain;

  @Before
  public void perTest() {
    // anySubDomainOf.devd.io maps to 127.0.0.1
    // I sure hope those people don't let the domain go, or remap it
    // it is a decent way to ensure nothing is shared between tests (mostly)
    domain = "http://t" + testNum++ + ".devd.io:8080";
    todo = null;
    id = -1;
  }

  @After
  public void stopServer() {
    app.stop();
  }

  @Test
  public void initialListShouldBeASingleSetupEntry() {
    app = new TestApp() {
      @Override
      public Object getAllTodos() {
        return new ArrayList<Todo>() {{
          add(new Todo().setTitle("Win Lottery").setId(1));
          add(new Todo().setTitle("Climb Everest").setId(2));
        }};
      }
    };
    startApp();
    openTodoPage();
    listInPageShouldBe("Win Lottery|Climb Everest");
  }

  @Test
  public void addItemToList() throws InterruptedException {
    app = new TestApp() {
      @Override
      public Object getAllTodos() {
        return new ArrayList<Todo>() {{
          add(new Todo().setTitle("One").setId(1));
          add(new Todo().setTitle("Two").setId(2));
        }};
      }
      @Override
      public Object createNewTodo(Request req) throws Exception {
        todo = req.body(Todo.class);
        return Results.with(new Todo().setTitle("unimportant").setId(1), Status.CREATED);
      }
    };

    startApp();
    openTodoPage();
    FWD.input(id("new-todo")).sendKeys("Buy eggs - check they're not cracked" + Keys.RETURN);
    listInPageShouldBe("One|Two|Buy eggs - check they're not cracked");
    waitForTodoToBeUpdatedAsync();
    assertThat(todo.getTitle(), equalTo("Buy eggs - check they're not cracked"));
  }

  private void waitForTodoToBeUpdatedAsync() throws InterruptedException {
    long time = 0;
    while (todo == null && time < 1000) {
      Thread.sleep(10);
      time += 10;
    }
  }

  private void openTodoPage() {
    DRIVER.get(domain + "/index.html?" + domain + "/todos");
  }

  @Test
  public void deleteAnItem() throws InterruptedException {

    app = new TestApp() {
      @Override
      public Object getAllTodos() {
        return new ArrayList<Todo>() {{
          add(new Todo().setTitle("Sleep").setId(1));
        }};
      }

      @Override
      public Object deleteTodoById(Request req) {
        id = req.param("id").intValue();
        return Results.noContent();
      }
    };

    startApp();
    openTodoPage();

    listInPageShouldBe("Sleep");
    FluentWebElement sleepRow = FWD.ul(id("todo-list"));
    clickOnRowToActivateDeleteButton(sleepRow);
    sleepRow.button(className("destroy")).click();
    listInPageShouldBe("");
    assertThat(id, equalTo(1));
  }

  private void startApp() {
    app.start("server.join=false");
    while (!app.appStarted) {
      try {
        Thread.sleep(15);
      } catch (InterruptedException e) {
      }
    }
  }

  @Test
  @Ignore // Too flakey
  public void runPetesTestSuite() throws InterruptedException {
    DRIVER.get("http://todobackend.com/specs/index.html?http://localhost:8080/todos");
    FWD.li(className("passes")).getText().within(secs(12)).shouldContain("passes: 16");
  }

  private static void listInPageShouldBe(String shouldBe) {
    FWD.ul(id("todo-list")).getText((TestableString.StringChanger) s -> s.replace("\n", "|"))
            .within(secs(1)).shouldBe(shouldBe);
  }

  private static void clickOnRowToActivateDeleteButton(FluentWebElement row) {
    new Actions(DRIVER).moveToElement(row.getWebElement()).click().perform();
  }

}
