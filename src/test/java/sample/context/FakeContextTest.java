package sample.context;

import io.github.thecodinglog.methodinvoker.TypeDescribableObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

/**
 * @author Jeongjin Kim
 * @since 2021-03-25
 */
class FakeContextTest {
    @Test
    void typeCheck() {
        FakeContext fakeContext = new FakeContext();
        fakeContext.add("a", new TypeDescribableObject(1, Integer.class));
        TypeDescribableObject oneValueOfType = fakeContext.getOneValueByType(Number.class);
        Integer object = oneValueOfType.getObject(Integer.class);
        Assertions.assertThat(object).isEqualTo(1);
    }

    @Test
    void reversedTypeCheck() {
        FakeContext fakeContext = new FakeContext();
        fakeContext.add("a", new TypeDescribableObject(1, Number.class));
        Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> fakeContext.getOneValueByType(Integer.class));
    }
}