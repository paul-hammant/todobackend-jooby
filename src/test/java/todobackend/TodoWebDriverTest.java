package todobackend;

import org.jooby.test.JoobyRule;
import org.junit.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.seleniumhq.selenium.fluent.FluentWebDriver;
import org.seleniumhq.selenium.fluent.FluentWebElement;
import org.seleniumhq.selenium.fluent.TestableString;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.id;
import static org.seleniumhq.selenium.fluent.Period.millis;

public class TodoWebDriverTest {

  public static class TestApp extends App {
    /* Only for testing */
    public TodoStore getTodoStore() {
      return require(TodoStore.class);
    }
  }

  public static TestApp TEST_APP = new TestApp();

  /**
   * One app/server for all the test of this class. If you want to start/stop a new server per test,
   * remove the static modifier and replace the {@link ClassRule} annotation with {@link Rule}.
   */
  @ClassRule
  public static JoobyRule app = new JoobyRule(TEST_APP);

  private static ChromeDriver DRIVER;
  private static FluentWebDriver FWD;
  private static int testNum;

  @BeforeClass
  public static void saveOneAndSetupWebDriver() {
    // Keep the WebDriver browser window open between tests
    DRIVER = new ChromeDriver();
    FWD = new FluentWebDriver(DRIVER);
  }

  @AfterClass
  public static void tearDown() {
    DRIVER.close();
    DRIVER.quit();
  }

  @Before
  public void perTest() {
    TodoStore todoStore = TEST_APP.getTodoStore();
    todoStore.deleteAll(); // wipe out server-side todos hanging o  ver from the last test.
    todoStore.create(new Todo().setId(1).setTitle("Wash bathroom"));
    // anySubDomainOf.devd.io maps to 127.0.0.1
    // I sure hope those people don't let the domain go, or remap it
    // it is a decent way to ensure nothing is shared between tests (mostly)
    String domain = "http://t" + testNum++ + ".devd.io:8080";
    DRIVER.get(domain + "/index.html?" + domain + "/todos");
  }

  @Test
  public void initialListShouldBeASingleSetupEntry() {
    listInPageShouldBe("Wash bathroom");
  }

  @Test
  public void addItemToList() {
    FWD.input(id("new-todo")).sendKeys("Buy eggs - check they're not cracked" + Keys.RETURN);
    listInPageShouldBe("Wash bathroom|Buy eggs - check they're not cracked");
  }

  @Test
  public void deleteAnItem() {
    listInPageShouldBe("Wash bathroom");
    FluentWebElement washBathroomRow = FWD.ul(id("todo-list"));
    clickOnRowToActivateDeleteButton(washBathroomRow);
    washBathroomRow.button(className("destroy")).click();
    listInPageShouldBe("");
    assertThat(TEST_APP.getTodoStore().list().size(), equalTo(0));
  }

  private static void listInPageShouldBe(String shouldBe) {
    FWD.ul(id("todo-list")).labels().getText(barDelimit()).within(millis(700)).shouldBe(shouldBe);
  }

  private static TestableString.DelimitWithChars barDelimit() {
    return new TestableString.DelimitWithChars("|");
  }

  private static void clickOnRowToActivateDeleteButton(FluentWebElement row) {
    new Actions(DRIVER).moveToElement(row.getWebElement()).click().perform();
  }

}
