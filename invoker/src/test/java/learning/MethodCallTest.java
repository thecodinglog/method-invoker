package learning;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Jeongjin Kim
 * @since 2021-03-09
 */
class MethodCallTest {
    @Test
    void callMethod() throws InvocationTargetException, IllegalAccessException {
        OrdinaryClass ordinaryClass = new OrdinaryClass();
        Method[] methods = OrdinaryClass.class.getMethods();
        Object invoke = methods[0].invoke(ordinaryClass, (Object[]) null);
    }
}
