package learning;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author Jeongjin Kim
 * @since 2021-03-17
 */
class FunctionalInterfaceClassTest {
    @Test
    void getFunctionalInterfaceMethod() throws NoSuchMethodException {
        Method[] methods = MyClass.class.getMethods();
        for (Method method : methods) {
            System.out.print(method.getName() + ":");
            for (Annotation annotation : method.getAnnotations()) {
                System.out.print(annotation.annotationType() + ",");
            }
            System.out.println();
        }
//        MyClass.class.getInterfaces()[0].getAnnotation(FunctionalInterface.class)
        Method method = MyClass.class.getInterfaces()[0].getMethods()[0];
        Method method1 = MyClass.class.getMethod(method.getName(), method.getParameterTypes());
        System.out.println(method1.getName());

    }

    @FunctionalInterface
    public interface MyInterface {
        void myHey();
    }

    @FunctionalInterface
    public interface YouInterface {
        void youHey();
    }

    static class MyClass implements MyInterface, YouInterface {

        @Override
        public void myHey() {
            System.out.println("hi");
        }

        public void na() {
        }

        @Override
        public void youHey() {
        }
    }

    static class YouClass extends MyClass {

    }
}
