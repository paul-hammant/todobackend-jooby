package todobackend;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class TodoUnitTest {

    @Test
    public void testTodoStoreCanHoldThings() {
        TodoStore ts = new TodoStore();
        assertThat(ts.list().size(), equalTo(0));
        ts.create(new Todo().setId(1).setTitle("abc"));
        assertThat(ts.list().size(), equalTo(1));
        assertThat(ts.get(1).getTitle(), equalTo("abc"));
    }
}
