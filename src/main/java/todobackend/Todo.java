package todobackend;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class Todo {

  private Integer id;

  private String title;

  private Boolean completed;

  private Integer order;

  private String url;

  public static Comparator<Todo> COMPARATOR = (l, r) -> {
    return l.getOrder().compareTo(r.getOrder());
  };

  public int getId() {
    return id;
  }

  public Todo setId(final int id) {
    this.id = id;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public Todo setTitle(final String title) {
    this.title = title;
    return this;
  }

  public Boolean getCompleted() {
    return Optional.ofNullable(completed).orElse(Boolean.FALSE);
  }

  public Todo setCompleted(final Boolean completed) {
    this.completed = completed;
    return this;
  }

  public Integer getOrder() {
    return Optional.ofNullable(order).orElse(0);
  }

  public Todo setOrder(final Integer order) {
    this.order = order;
    return this;
  }

  public String getUrl() {
    return url;
  }

  public Todo setUrl(final String url) {
    this.url = url;
    return this;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof Todo) {
      return Objects.equals(id, ((Todo) obj).id);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Optional.ofNullable(id).orElse(1);
  }

}
