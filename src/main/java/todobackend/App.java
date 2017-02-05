package todobackend;

import java.util.List;
import java.util.function.Consumer;

import org.jooby.Jooby;
import org.jooby.Results;
import org.jooby.Status;
import org.jooby.json.Jackson;

/**
 * Source code for http://www.todobackend.com.
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

    /** Todo API: */
    use("/todos")
        /** List all todos. */
        .get(() -> {
          TodoStore store = require(TodoStore.class);
          return store.list();
        })
        /** Get todo by ID. */
        .get("/:id", req -> {
          TodoStore store = require(TodoStore.class);
          return store.get(req.param("id").intValue());
        })
        /** Create a new todo. */
        .post(req -> {
          TodoStore store = require(TodoStore.class);
          return Results.with(store.create(req.body(Todo.class)), Status.CREATED);
        })
        /** Delete todo by ID. */
        .delete("/:id", req -> {
          TodoStore store = require(TodoStore.class);
          store.delete(req.param("id").intValue());
          return Results.noContent();
        })
        /** Delete all todos. */
        .delete(() -> {
          TodoStore store = require(TodoStore.class);
          store.deleteAll();
          return Results.noContent();
        })
        /** Update an existing todo. */
        .patch("/:id", req -> {
          TodoStore store = require(TodoStore.class);
          return store.merge(req.param("id").intValue(), req.body(Todo.class));
        });
    assets("/**");
  }

  public static void main(final String[] args) {
    run(App::new, args);
  }

}
