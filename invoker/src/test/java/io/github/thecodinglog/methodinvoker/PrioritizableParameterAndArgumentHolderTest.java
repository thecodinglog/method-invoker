package io.github.thecodinglog.methodinvoker;

import org.junit.jupiter.api.Test;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author Jeongjin Kim
 * @since 2021-03-03
 */
class PrioritizableParameterAndArgumentHolderTest {
    @Test
    void getGenericInfo() throws NoSuchFieldException, NoSuchMethodException {
        List<Integer> integers = new ArrayList<>();
        Field bytes = TestClass.class.getDeclaredField("bytes");
        Type genericType = bytes.getGenericType();
        System.out.println(genericType);
//        System.out.println(TestClass.class.getField("bytes").getGenericType() instanceof ParameterizedType);
        System.out.println(integers.getClass());
        System.out.println(integers instanceof ParameterizedType);

        Type genericSuperclass = ((Object) integers).getClass().getGenericSuperclass();
        System.out.println("ffff -- " + genericSuperclass);
        System.out.println(genericSuperclass instanceof ParameterizedType);
        System.out.println(TestClass.class.getGenericSuperclass() instanceof ParameterizedType);
        System.out.println(((ParameterizedType) genericSuperclass).getActualTypeArguments());

        Constructor<?> constructor = TestClass.class.getConstructor(List.class);

        Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (Class<?> parameterType : parameterTypes) {
            System.out.println(parameterType);
        }
        ParameterizedTypeReference<List<Integer>> parameterizedTypeReference = new ParameterizedTypeReference<List<Integer>>() {
        };

        ResolvableType resolvableType = ResolvableType.forInstance(parameterizedTypeReference);
        System.out.println(resolvableType.getSuperType().getGeneric(0));
        ResolvableType resolvableType1 = ResolvableType.forConstructorParameter(TestClass.class.getConstructor(List.class), 0);
        System.out.println(resolvableType1);
    }

    @Test
    void objectExtract() {
        List<Integer> integers = new ArrayList<>();
        Context context = new Context();
        context.put("aaa", integers);
    }

    @Test
    void getWhatIsT() throws NoSuchMethodException {
        Constructor<GenericMethod> constructor = GenericMethod.class.getConstructor(List.class);
        ResolvableType resolvableType = ResolvableType.forConstructorParameter(constructor, 0);
        Type genericSuperclass = GenericMethod.class.getGenericSuperclass();
        Type genericSuperclass1 = MyGenericMethod.class.getGenericSuperclass();
    }

    @Test
    void resolvableTypeOfParameter() throws NoSuchMethodException {
        Constructor<GenericMethod> constructor = GenericMethod.class.getConstructor(Object.class);

        Parameter parameters = constructor.getParameters()[0];

        ResolvableType resolvableType = ResolvableType.forType(parameters.getType());
        ResolvableType resolvableType1 = ResolvableType.forConstructorParameter(constructor, 0);
        List<Integer> a = new ArrayList<>();
        System.out.println(resolvableType.isInstance(a));
        System.out.println(resolvableType1.isInstance(a));
    }

    @Test
    void resolvableTypeOfParameterArgument() throws NoSuchMethodException {
        Constructor<GenericConstructorClass> constructor = GenericConstructorClass.class.getConstructor(List.class);

        Parameter parameters = constructor.getParameters()[0];

        ResolvableType resolvableType = ResolvableType.forType(parameters.getType());
        ResolvableType resolvableType1 = ResolvableType.forConstructorParameter(constructor, 0);
//        ResolvableType.forMethodParameter()

        List<Integer> a = new ArrayList<>();
        System.out.println(resolvableType.isInstance(a));
        System.out.println(resolvableType1.isInstance(a));
    }

    @Test
    void nestedResolvableTypeOfParameterArgument() throws NoSuchMethodException {
        Constructor<GenericConstructorNestClass> constructor = GenericConstructorNestClass.class.getConstructor(List.class);

        Parameter parameters = constructor.getParameters()[0];

        ResolvableType resolvableType = ResolvableType.forType(parameters.getType());
        ResolvableType resolvableType1 = ResolvableType.forConstructorParameter(constructor, 0);
//        ResolvableType.forMethodParameter()

        List<Map<String, Object>> a = new ArrayList<>();
        System.out.println(resolvableType.isInstance(a));
        System.out.println(resolvableType1.isInstance(a));
        boolean assignableFrom = resolvableType1.isAssignableFrom(ResolvableType.forInstance(a));
        System.out.println(assignableFrom);
        ParameterizedTypeReference<List<Map<String, Object>>> typeRef = new ParameterizedTypeReference<List<Map<String, Object>>>() {
        };
        boolean assignableFrom1 = resolvableType1.isAssignableFrom(ResolvableType.forType(typeRef.getType()));
        System.out.println(assignableFrom1);

        ParameterizedTypeReference<List<Map<String, Object>>> typeRef2 = new ParameterizedTypeReference<List<Map<String, Object>>>() {
        };
        boolean assignableFrom2 = resolvableType1.isAssignableFrom(ResolvableType.forType(typeRef2.getType()));
        System.out.println(assignableFrom2);
    }

    @Test
    void subClassResolvableTypeOfParameterArgument() throws NoSuchMethodException {
        Constructor<GenericConstructorNestClassClass> constructor =
                GenericConstructorNestClassClass.class.getConstructor(List.class);
        ResolvableType resolvableType = ResolvableType.forConstructorParameter(constructor, 0);

        System.out.println(resolvableType
                .isAssignableFrom(ResolvableType.forType(
                        new ParameterizedTypeReference<List<Map<String, Object>>>() {
                        }.getType())));

        System.out.println(resolvableType
                .isAssignableFrom(ResolvableType.forType(
                        new ParameterizedTypeReference<List<Map<Integer, Object>>>() {
                        }.getType())));

    }

    @Test
    void getGenericMethodTypeFrom() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        GenericReturnType g = new GenericReturnType();
        Method method = g.getClass().getMethod("method", (Class<?>[]) null);
        Object invoke = method.invoke(g, (Object[]) null);

        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
        TypeDescribableObject typeDescribableObject = new TypeDescribableObject(invoke, resolvableType.getType());

    }

    @Test
    void callVoidMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        VoidReturnType voidReturnType = new VoidReturnType();
        Method method = voidReturnType.getClass().getMethod("call", (Class<?>[]) null);
        Object invoke = method.invoke(voidReturnType, (Object[]) null);

        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
        TypeDescribableObject typeDescribableObject = new TypeDescribableObject(invoke, resolvableType.getType());

    }

    @Test
    void callStringMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        NonGenericType voidReturnType = new NonGenericType();
        Method method = voidReturnType.getClass().getMethod("hey", (Class<?>[]) null);
        Object invoke = method.invoke(voidReturnType, (Object[]) null);

        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
        TypeDescribableObject typeDescribableObject = new TypeDescribableObject(invoke, resolvableType.resolve());
        TypeDescribableObject typeDescribableObject2 = new TypeDescribableObject(invoke, method.getGenericReturnType());
    }

    @Test
    void withoutResolvable() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        GenericReturnType g = new GenericReturnType();
        Method method = g.getClass().getMethod("method", (Class<?>[]) null);
        Object invoke = method.invoke(g, (Object[]) null);

        TypeDescribableObject typeDescribableObject = new TypeDescribableObject(invoke, method.getGenericReturnType());
    }

    @Test
    void genericTypeReturn() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        GenericReturnType g = new GenericReturnType();
        Method method = g.getClass().getMethod("hey", String.class);
        Object invoke = method.invoke(g, "12312");

        ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
        TypeDescribableObject typeDescribableObject = new TypeDescribableObject(invoke, method.getGenericReturnType());
        TypeDescribableObject typeDescribableObject2 = new TypeDescribableObject(invoke, resolvableType.getType());

    }

    @Test
    void methodParameter() throws NoSuchMethodException {
        GenericReturnType g = new GenericReturnType();
        Method method = g.getClass().getMethod("hey", String.class);

        MethodParameter methodParameter = MethodParameter.forMethodOrConstructor(method, 0);
        methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
        System.out.println(methodParameter.getParameterName());
    }

    @Test
    void parameterizedTypeReferenceTest() {
        ParameterizedTypeReference<String> type = new ParameterizedTypeReference<String>() {
        };
        System.out.println(type.getType());
    }

    @Test
    void boundedConstructor() {
        ResolvableType resolvableType1 = ResolvableType.forConstructorParameter(BoundedGeneric.class.getConstructors()[0], 0);
        ResolvableType resolvableType2 = ResolvableType.forConstructorParameter(NoBoundedGeneric.class.getConstructors()[0], 0);
        ResolvableType resolvableType3 = ResolvableType.forConstructorParameter(BoundedGenericList.class.getConstructors()[0], 0);
    }

    static class BoundedGeneric<T extends Number> {
        public BoundedGeneric(T t) {
        }
    }

    static class BoundedGenericList<T extends Number> {
        public BoundedGenericList(List<T> t) {
        }
    }

    static class NoBoundedGeneric<T> {
        public NoBoundedGeneric(T t) {
        }
    }

    static class NonGenericType {
        public String hey() {
            return "hey";
        }
    }

    static class VoidReturnType {
        public void call() {

        }
    }

    static class GenericReturnType {
        public List<String> method() {
            LinkedList<String> objects = new LinkedList<>();
            objects.add("ff");
            return objects;
        }

        public <T extends String> T hey(T f) {
            return f;
        }
    }

    static class GenericConstructorNestClassClass {
        public GenericConstructorNestClassClass(List<Map<? extends Number, Object>> lists) {
        }
    }

    static class GenericConstructorNestClass {
        public GenericConstructorNestClass(List<Map<String, Object>> lists) {
        }
    }

    static class GenericConstructorClass {
        public GenericConstructorClass(List<String> lists) {
        }
    }

    static class GenericMethod<T> {
        public GenericMethod(T t) {
        }

        public GenericMethod(List<T> list) {

        }
    }

    static class MyGenericMethod<U> extends GenericMethod<U> {

        public MyGenericMethod(U u) {
            super(u);
        }
    }

    static class Context {
        Map<String, Object> data = new HashMap<>();

        public void put(String key, Object data) {
            ResolvableType resolvableType = ResolvableType.forInstance(data);
            System.out.println(resolvableType.getSuperType().getGeneric(0));
            System.out.println(resolvableType.getGeneric(0));
        }
    }

    static class TestClass {
        private List<Byte> bytes;

        public TestClass(List<String> strings) {
        }
    }
}