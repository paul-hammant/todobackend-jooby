package todobackend;

import java.util.List;
import java.util.function.Consumer;

import org.jooby.Jooby;
import org.jooby.Request;
import org.jooby.Results;
import org.jooby.Status;
import org.jooby.json.Jackson;

/**
 * Source code for implementation of http://todobackend.com.
 */
@SuppressWarnings({"unchecked", "rawtypes" })
public class App extends Jooby {

  {
    /** JSON: */
    use(new Jackson());

    /** CORS: */
    use("*", (req, rsp) -> {
      rsp.header("Access-Control-Allow-Origin", "*");
      rsp.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PATCH");
      rsp.header("Access-Control-Max-Age", "3600");
      rsp.header("Access-Control-Allow-Headers", "x-requested-with", "origin", "content-type",
          "accept");
      if (req.method().equalsIgnoreCase("options")) {
        rsp.end();
      }
    });

    /** Compute absolute Todo URL: */
    after("/todos/**", (req, rsp, result) -> {
      Consumer<Todo> computeUrl = todo -> todo
          .setUrl("http://" + req.header("host").value("") + "/todos/" + todo.getId());
      Object value = result.get();
      if (value instanceof Todo) {
        computeUrl.accept((Todo) value);
      } else if (value instanceof List) {
        ((List) value).forEach(computeUrl);
      }
      return result;
    });

    /* Todo API: */
    use("/todos")
        /* List all todos. */
        .get(this::getAllTodos)
        /* Get todo by ID. */
        .get("/:id", this::getTodoById)
        /* Create a new todo. */
        .post(this::createNewTodo)
        /* Delete todo by ID. */
        .delete("/:id", this::deleteTodoById)
        /* Delete all todos. */
        .delete(this::deleteAllTodos)
        /* Update an existing todo. */
        .patch("/:id", this::patchTodo);

    assets("/**");
  }

  protected Object patchTodo(Request req) throws Exception {
    TodoStore store = require(TodoStore.class);
    return store.merge(req.param("id").intValue(), req.body(Todo.class));
  }

  protected Object deleteAllTodos() {
    TodoStore store = require(TodoStore.class);
    store.deleteAll();
    return Results.noContent();
  }

  protected Object deleteTodoById(Request req) {
    TodoStore store = require(TodoStore.class);
    store.delete(req.param("id").intValue());
    return Results.noContent();
  }

  protected Object createNewTodo(Request req) throws Exception {
    TodoStore store = require(TodoStore.class);
    return Results.with(store.create(req.body(Todo.class)), Status.CREATED);
  }

  protected Object getTodoById(Request req) {
    TodoStore store = require(TodoStore.class);
    return store.get(req.param("id").intValue());
  }

  protected Object getAllTodos() {
    TodoStore store = require(TodoStore.class);
    return store.list();
  }

  public static void main(final String[] args) {
    run(App::new, args);
  }

}
