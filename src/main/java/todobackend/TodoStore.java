package todobackend;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.jooby.Err;
import org.jooby.Status;

@Singleton
public class TodoStore {

  private Map<Integer, Todo> todos = new ConcurrentHashMap<>();

  public TodoStore() {
  }

  public List<Todo> list() {
    return todos.values().stream()
        .sorted(Todo.COMPARATOR)
        .collect(Collectors.toList());
  }

  public Todo get(final int id) {
    Todo todo = todos.get(id);
    if (todo == null) {
      throw new Err(Status.NOT_FOUND);
    }
    return todo;
  }

  public Todo create(final Todo todo) {
    todo.setId(todos.size() + 1);
    todos.put(todo.getId(), todo);
    return todo;
  }

  public void delete(final int id) {
    todos.remove(id);
  }

  public void deleteAll() {
    todos.clear();
  }

  public Todo merge(final int id, final Todo todo) {
    Todo existing = get(id);
    Optional.ofNullable(todo.getCompleted()).ifPresent(existing::setCompleted);
    Optional.ofNullable(todo.getOrder()).ifPresent(existing::setOrder);
    Optional.ofNullable(todo.getTitle()).ifPresent(existing::setTitle);
    return existing;
  }

}
