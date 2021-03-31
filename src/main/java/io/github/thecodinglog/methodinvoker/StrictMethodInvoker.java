package io.github.thecodinglog.methodinvoker;

import io.github.thecodinglog.methodinvoker.exceptions.MethodInvokeException;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Jeongjin Kim
 * @since 2021-03-25
 */
public class StrictMethodInvoker implements MethodInvoker {
    private final MethodArgumentBinder binder = new TypeMatchableMethodArgumentBinder();
    private final PriorityPicker picker = new HighestPriorityPicker();

    private final MethodResolver methodResolver = new StrictMethodResolver(
            new PublicCandidateMethodsSelector(),
            binder,
            picker
    );
    private final ObjectFactory objectFactory = new DefaultObjectFactory(
            new StrictConstructorResolver(
                    new PublicOnlyCandidateConstructorsSelector(),
                    binder,
                    picker
            )
    );

    @Override
    public TypeDescribableObject invoke(Object object, String methodName, Context context) {
        PrioritizableMethodOrConstructorHolder resolve;
        try {
            if (methodName != null) {
                resolve = methodResolver.resolve(object.getClass(), methodName, context);

            } else {
                resolve = methodResolver.resolve(object.getClass(), context);
            }
        } catch (MethodNotFoundException e) {
            throw new MethodInvokeException(e.getMessage(), e);
        }

        Object invoke;
        try {
            invoke = resolve.method().invoke(object, resolve.args());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MethodInvokeException(e.getMessage(), e);
        }

        return new TypeDescribableObject(invoke, resolve.method().getReturnType());
    }

    @Override
    public TypeDescribableObject invoke(Class<?> clazz, String methodName, Context context) {
        Object object = objectFactory.createObject(clazz, context);

        return invoke(object, methodName, context);
    }

    @Override
    public TypeDescribableObject invoke(String fullQualifiedClassName, String methodName, Context context) {
        Object object = objectFactory.createObject(fullQualifiedClassName, context);

        return invoke(object, methodName, context);
    }
}
