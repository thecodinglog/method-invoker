package io.github.thecodinglog.methodinvoker;

import io.github.thecodinglog.methodinvoker.annotations.DefaultMethod;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jeongjin Kim
 * @since 2021-03-25
 */
class StrictMethodInvokerTest {
    MethodInvoker methodInvoker = new StrictMethodInvoker();

    @Test
    void givenNoArgsMethodThenShouldInvokeMethod() {
        SimpleMethod simpleMethod = new SimpleMethod();

        TypeDescribableObject myName = methodInvoker.invoke(simpleMethod, "myName", null);

        assertThat(myName.getObject()).isEqualTo("myName");
        assertThat(myName.getType()).isEqualTo(String.class);
    }

    @Test
    void givenNoMethodNameUseDefaultMethod() {
        DefaultMethodUse defaultMethodUse = new DefaultMethodUse();
        TypeDescribableObject invoke = methodInvoker.invoke(defaultMethodUse, null, null);
        assertThat(invoke.getObject()).isEqualTo("two");
        assertThat(invoke.getType()).isEqualTo(String.class);
    }

    @Test
    void givenVoidMethodThenObjectIsNullAndTypeIsVoid() {
        VoidMethodClass voidMethodClass = new VoidMethodClass();
        TypeDescribableObject invoke = methodInvoker.invoke(voidMethodClass, "process", null);
        assertThat(invoke.getObject()).isEqualTo(null);
        assertThat(invoke.getType()).isEqualTo(void.class);
    }

    @Test
    void givenParameterizedTypeReturnMethodThenTypeDescribableObjectTypeIsParameterizedType() {
        ParameterizedTypeClass parameterizedTypeClass = new ParameterizedTypeClass();
        TypeDescribableObject parameterizedTypeReturn =
                methodInvoker.invoke(parameterizedTypeClass, "parameterizedTypeReturn", null);
        assertThat(parameterizedTypeReturn.getType()).
                isEqualTo((new TypeReference<List<Map<String, Object>>>() {
                }).getType());
        assertThat(parameterizedTypeReturn.getType()).isInstanceOf(ParameterizedType.class);

    }

    @Test
    void givenPlainTypeReturnMethodThenTypeDescribableObjectTypeIsPlainType() {
        ParameterizedTypeClass parameterizedTypeClass = new ParameterizedTypeClass();
        TypeDescribableObject parameterizedTypeReturn =
                methodInvoker.invoke(parameterizedTypeClass, "plainObject", null);
        assertThat(parameterizedTypeReturn.getType()).isEqualTo(VoidMethodClass.class);
        assertThat(parameterizedTypeReturn.getType()).isInstanceOf(Class.class);
    }

    static class ParameterizedTypeClass {
        public List<Map<String, Object>> parameterizedTypeReturn() {
            return new ArrayList<>();
        }

        public VoidMethodClass plainObject() {
            return new VoidMethodClass();
        }
    }

    static class VoidMethodClass {
        public void process() {

        }
    }

    static class DefaultMethodUse {
        public String one() {
            return "one";
        }

        @DefaultMethod
        public String two() {
            return "two";
        }
    }

    static class SimpleMethod {
        public String myName() {
            return "myName";
        }
    }
}