package learning;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.ClassUtils;

/**
 * @author Jeongjin Kim
 * @since 2021-03-02
 */
class ClassUtilTest {
    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive();
    }

    @Test
    void UseUtil() {
        Assertions.assertThat(ClassUtils.isAssignable(int.class, Integer.class)).isTrue();
        Assertions.assertThat(ClassUtils.isAssignable(Integer.class, int.class)).isTrue();
        Assertions.assertThat(ClassUtils.isAssignable(int.class, int.class)).isTrue();
        Assertions.assertThat(ClassUtils.isAssignable(float.class, int.class)).isFalse();
        Assertions.assertThat(ClassUtils.isAssignable(int.class, float.class)).isFalse();
        Assertions.assertThat(ClassUtils.isAssignable(double.class, int.class)).isFalse();
        Assertions.assertThat(ClassUtils.isAssignable(long.class, int.class)).isFalse();
        Assertions.assertThat(ClassUtils.isAssignable(Number.class, int.class)).isTrue();
        Assertions.assertThat(ClassUtils.isAssignable(Number.class, double.class)).isTrue();
        Assertions.assertThat(ClassUtils.isAssignable(Number.class, Integer.class)).isTrue();
        Assertions.assertThat(ClassUtils.isAssignable(Integer.class, Number.class)).isFalse();
        Assertions.assertThat(ClassUtils.isAssignable(CharSequence.class, String.class)).isTrue();
    }

    @Test
    void ObjectCast() {
        Object a = 1;
        Integer b = 1;
        int c = b;

        System.out.println(ClassUtils.isPrimitiveOrWrapper(a.getClass()));
        System.out.println(ClassUtils.isPrimitiveOrWrapper(b.getClass()));
        System.out.println(a.getClass() == b.getClass());

    }
}
