package io.github.thecodinglog.methodinvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable class with constructor and arguments.
 * <p>
 * It has the priority of the method or constructor.
 * The lowest priority of among parameters is the priority of the method or constructor.
 *
 * @author Jeongjin Kim
 * @since 2021-02-23
 */
final class PrioritizableMethodOrConstructorHolder implements Prioritizable {
    private final MethodOrConstructor methodOrConstructor;
    private final List<? extends ParameterAndArgumentHolder> parameterAndArgumentHolders;
    private final int priority;

    public PrioritizableMethodOrConstructorHolder(MethodOrConstructor methodOrConstructor,
                                                  List<? extends ParameterAndArgumentHolder> holders) {
        if (methodOrConstructor == null)
            throw new IllegalArgumentException("The methodOrConstructor can not be null.");

        this.methodOrConstructor = methodOrConstructor;
        this.parameterAndArgumentHolders = holders == null ?
                Collections.unmodifiableList(new ArrayList<>()) :
                Collections.unmodifiableList(holders);

        int highestPriority = 0;
        for (Prioritizable parameterAndArgumentHolder : this.parameterAndArgumentHolders) {
            if (highestPriority == ParameterAndArgumentHolder.PRIORITY_LOWEST)
                break;
            if (parameterAndArgumentHolder.priority() > highestPriority)
                highestPriority = parameterAndArgumentHolder.priority();
        }
        this.priority = highestPriority;
    }

    /**
     * @return Constructor. It can be {@code null}
     */
    public Constructor<?> constructor() {
        return methodOrConstructor.getConstructor();
    }

    /**
     * @return method. It can be {@code null}
     */
    public Method method() {
        return methodOrConstructor.getMethod();
    }

    /**
     * Return actual arguments. if no argument exists, return an array with length 0;
     *
     * @return actual arguments.
     */
    public Object[] args() {
        return parameterAndArgumentHolders.stream()
                .map(ParameterAndArgumentHolder::getActualArgument)
                .toArray();
    }

    @Override
    public int priority() {
        return priority;
    }
}
