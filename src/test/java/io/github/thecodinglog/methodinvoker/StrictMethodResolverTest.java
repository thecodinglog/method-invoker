package io.github.thecodinglog.methodinvoker;

import io.github.thecodinglog.methodinvoker.annotations.DefaultMethod;
import io.github.thecodinglog.methodinvoker.annotations.MethodQualifier;
import io.github.thecodinglog.methodinvoker.exceptions.NoUniqueQualifierException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author Jeongjin Kim
 * @since 2021-03-11
 */
class StrictMethodResolverTest {
    MethodResolver resolver = new StrictMethodResolver(
            new PublicCandidateMethodsSelector(),
            new TypeMatchableMethodArgumentBinder(),
            new HighestPriorityPicker()
    );

    @Test
    void givenOrdinaryClassWithoutContextThenReturnMethodWithNoArgs() throws NoSuchMethodException {
        Context mockContext = mock(Context.class);
        given(mockContext.hasKey("name")).willReturn(false);

        PrioritizableMethodOrConstructorHolder sayHello = resolver.resolve(OrdinaryClass.class, "sayHello", mockContext);

        assertThat(sayHello.method()).isEqualTo(OrdinaryClass.class.getMethod("sayHello", (Class<?>[]) null));
        assertThat(sayHello.args()).hasSize(0);
    }

    @Test
    void givenOrdinaryClassWithContextThenReturnMethodWithArgs() throws NoSuchMethodException {
        Context mockContext = mock(Context.class);
        given(mockContext.hasKey("name")).willReturn(true);
        given(mockContext.getValueByKey("name")).willReturn(new TypeDescribableObject("ff"));

        PrioritizableMethodOrConstructorHolder sayHello = resolver.resolve(OrdinaryClass.class, "sayHello", mockContext);

        assertThat(sayHello.method()).isEqualTo(OrdinaryClass.class.getMethod("sayHello", String.class));
        assertThat(sayHello.args()).hasSize(1);
    }

    @Test
    void givenOrdinaryClassAndAskWrongNameOfMethodThenThrowsException() {
        Context mockContext = mock(Context.class);
        assertThatExceptionOfType(MethodNotFoundException.class)
                .isThrownBy(() -> resolver.resolve(OrdinaryClass.class, "hi", mockContext))
                .withMessage("No public method called hi.");
    }

    @Test
    void givenOrdinaryClassAndAskWithoutMethodNameThenThrowsException() {
        Context mockContext = mock(Context.class);
        assertThatExceptionOfType(MethodNotFoundException.class)
                .isThrownBy(() -> resolver.resolve(OrdinaryClass.class, mockContext))
                .withMessage("No default method exists.");
    }

    @Test
    void givenPrivateMethodAndAskPrivateMethodThenThrowsException() {
        Context mockContext = mock(Context.class);
        given(mockContext.hasKey("name")).willReturn(true);
        given(mockContext.getValueByKey("name")).willReturn(new TypeDescribableObject("ff"));
        assertThatExceptionOfType(MethodNotFoundException.class)
                .isThrownBy(() -> resolver.resolve(PrivateMethod.class, "sayHello", mockContext))
                .withMessage("No public method called sayHello.");
    }

    @Test
    void givenDefaultMethodThenReturnTheMethod() throws NoSuchMethodException {
        Context mockContext = mock(Context.class);
        given(mockContext.hasKey("name")).willReturn(true);
        given(mockContext.getValueByKey("name")).willReturn(new TypeDescribableObject("ff"));
        PrioritizableMethodOrConstructorHolder sayHello = resolver.resolve(DefaultMethodClass.class, mockContext);

        assertThat(sayHello.method()).isEqualTo(DefaultMethodClass.class.getMethod("method1", String.class));
        assertThat(sayHello.args()).hasSize(1);
    }

    @Test
    void givenDefaultMethodAndMethodNameThenReturnTheNamedMethod() throws NoSuchMethodException {
        Context mockContext = mock(Context.class);
        given(mockContext.hasKey("name")).willReturn(true);
        given(mockContext.getValueByKey("name")).willReturn(new TypeDescribableObject("ff"));
        PrioritizableMethodOrConstructorHolder sayHello = resolver.resolve(DefaultMethodClass.class, "method2", mockContext);

        assertThat(sayHello.method()).isEqualTo(DefaultMethodClass.class.getMethod("method2", String.class));
        assertThat(sayHello.args()).hasSize(1);
    }

    @Test
    void givenMethodQualifierAndQualifierValueThenReturnQualifiedMethod() throws NoSuchMethodException {
        Context mockContext = mock(Context.class);
        given(mockContext.hasKey("name")).willReturn(true);
        given(mockContext.getValueByKey("name")).willReturn(new TypeDescribableObject("ff"));
        PrioritizableMethodOrConstructorHolder sayHello = resolver.resolve(QualifiedMethodClass.class, "MyMethod", mockContext);

        assertThat(sayHello.method()).isEqualTo(QualifiedMethodClass.class.getMethod("method1", String.class));
        assertThat(sayHello.args()).hasSize(1);
    }

    @Test
    void givenSameMethodQualifierThenThrowsException() {
        Context mockContext = mock(Context.class);
        assertThatExceptionOfType(NoUniqueQualifierException.class)
                .isThrownBy(() -> resolver.resolve(SameQualifiedMethodClass.class, "MyMethod", mockContext))
                .withMessage("MyMethod is not unique qualifier.");
    }

    @Test
    void givenDuplicateDefaultMethodThenThrowsException() {
        Context mockContext = mock(Context.class);
        assertThatExceptionOfType(TooManyDefaultException.class)
                .isThrownBy(() -> resolver.resolve(DuplicateDefaultMethodClass.class, "MyMethod", mockContext))
                .withMessage("More then one DefaultMethod exists.");
    }

    @Test
    void givenComplicateObjectParameterExistsAndTheObjectExistsInContextThenReturnTheMethodHavingComplicateClassParameter() throws NoSuchMethodException {
        Context mockContext = mock(Context.class);
        given(mockContext.hasKey("name")).willReturn(true);
        given(mockContext.getValueByKey("name")).willReturn(new TypeDescribableObject(new OrdinaryClass()));
        PrioritizableMethodOrConstructorHolder sayHello = resolver.resolve(ComplicateObjectParameter.class, "method1", mockContext);

        assertThat(sayHello.method()).isEqualTo(ComplicateObjectParameter.class.getMethod("method1", OrdinaryClass.class));
        assertThat(sayHello.args()).hasSize(1);
    }

    @Test
    void giveListWildcardMethodThenBindWithAnyList() throws NoSuchMethodException {
        Context mockContext = mock(Context.class);
        given(mockContext.hasKey("list")).willReturn(true);
        ArrayList<String> arrayList = new ArrayList<>();
        given(mockContext.getValueByKey("list")).willReturn(new TypeDescribableObject(arrayList, new TypeReference<ArrayList<String>>() {
        }));
        PrioritizableMethodOrConstructorHolder sayHello = resolver.resolve(WildcardMethod.class, "method1", mockContext);

        assertThat(sayHello.method()).isEqualTo(WildcardMethod.class.getMethod("method1", List.class));
        assertThat(sayHello.args()).hasSize(1);
    }

    @Test
    void givenTypeMatchingContextThenBindUsingType() throws NoSuchMethodException {
        Context mockContext = mock(Context.class);
        given(mockContext.hasKey("list")).willReturn(false);
        ArrayList<String> arrayList = new ArrayList<>();
        Type methodParamType = WildcardMethod.class.getMethod("method1", List.class).getParameters()[0].getParameterizedType();
        given(mockContext.hasType(methodParamType)).willReturn(true);
        given(mockContext.getOneValueByType(methodParamType))
                .willReturn(new TypeDescribableObject(arrayList, new TypeReference<ArrayList<String>>() {
                }));
        PrioritizableMethodOrConstructorHolder sayHello = resolver.resolve(WildcardMethod.class, "method1", mockContext);
        assertThat(sayHello.method()).isEqualTo(WildcardMethod.class.getMethod("method1", List.class));
        assertThat(sayHello.args()).hasSize(1);
    }

    static class WildcardMethod {
        public void method1(List<?> list) {

        }

        public void method1(String name) {
        }
    }

    static class ComplicateObjectParameter {
        public void method1(OrdinaryClass name) {
        }

        public void method1(String name) {
        }
    }

    static class SameQualifiedMethodClass {
        @MethodQualifier("MyMethod")
        public void method2(String name) {

        }

        @MethodQualifier("MyMethod")
        public void method1(String name) {

        }

    }

    static class QualifiedMethodClass {
        public void method2(String name) {

        }

        @MethodQualifier("MyMethod")
        public void method1(String name) {

        }

    }

    static class DuplicateDefaultMethodClass {
        @DefaultMethod
        public void method1(String name) {

        }

        @DefaultMethod
        public void method2(String name) {

        }
    }

    static class DefaultMethodClass {
        @DefaultMethod
        public void method1(String name) {

        }

        public void method2(String name) {

        }
    }

    static class PrivateMethod {
        private void sayHello() {

        }

        private void sayHello(String name) {

        }
    }

    static class OrdinaryClass {
        public void sayHello() {

        }

        public void sayHello(String name) {

        }
    }
}