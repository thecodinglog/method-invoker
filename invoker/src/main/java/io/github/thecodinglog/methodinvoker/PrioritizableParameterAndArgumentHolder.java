package io.github.thecodinglog.methodinvoker;

import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Holder class to save the parameter and argument.
 * <p>
 * The argument can save using {@link PrioritizableParameterAndArgumentHolder#accept} method.
 * If the argument is not acceptable, {@link IllegalArgumentException} will be thrown.
 * <p>
 * The priority is determined by the parameter class and the argument class. The priority is used for resolving the
 * constructor. If more than one constructor is listed as a candidate, this priority is used to make the final decision.
 * <p>
 * See, {@link ConstructorResolver}
 *
 * @author Jeongjin Kim
 * @since 2021-02-23
 */
final class PrioritizableParameterAndArgumentHolder implements ParameterAndArgumentHolder {
    public final static int PRIORITY_STEP = 255;

    private final MethodOrConstructorParameter methodOrConstructorParameter;
    private Object actualArgument;
    private boolean isResolved;
    private int priority;
    private ResolvableType resolvableType;

    public PrioritizableParameterAndArgumentHolder(MethodOrConstructorParameter methodParameter) {
        this.methodOrConstructorParameter = methodParameter;
    }

    /**
     * @return Parameter name
     */
    @Override
    public String getParameterName() {
        return methodOrConstructorParameter.getParameterName();
    }

    /**
     * @return Actual argument
     */
    @Override
    public Object getActualArgument() {
        return actualArgument;
    }

    /**
     * @param actualArgument to bind with the parameter
     * @throws IllegalStateException    if already resolved
     * @throws IllegalArgumentException if can not accept the argument
     */
    @Override
    public void accept(TypeDescribableObject actualArgument) {
        if (isResolved)
            throw new IllegalStateException("Actual argument already set");

        if (canAccept(actualArgument.getType())) {
            if (this.methodOrConstructorParameter.isPrimitive() && actualArgument.getObject() == null)
                this.actualArgument = defaultPrimitiveValue(this.methodOrConstructorParameter.getParameterType());
            else
                this.actualArgument = actualArgument.getObject();
            this.isResolved = true;

            priority = evaluatePriority(this.methodOrConstructorParameter.getGenericParameterType()
                    , actualArgument.getType());

        } else
            throw new IllegalArgumentException(
                    String.format("actualArgument is not type of the parameter type [%s]"
                            , methodOrConstructorParameter.getGenericParameterType()));
    }

    private Object defaultPrimitiveValue(Class<?> parameterType) {
        if (parameterType == int.class)
            return 0;
        else if (parameterType == short.class)
            return 0;
        else if (parameterType == long.class)
            return 0L;
        else if (parameterType == byte.class)
            return 0;
        else if (parameterType == float.class)
            return 0.0f;
        else if (parameterType == double.class)
            return 0.0d;
        else if (parameterType == char.class)
            return '\u0000';
        else if (parameterType == boolean.class)
            return false;
        return null;
    }

    private int evaluatePriority(Type paramType, Type argType) {
        int priority = PRIORITY_HIGHEST;

        if (paramType == argType)
            return priority;

        if (ClassUtils.isPrimitiveOrWrapper(methodOrConstructorParameter.getParameterType())) {
            return priority;
        }

        ResolvableType type = ResolvableType.forType(argType);
        while (!(type.resolve() == null || type.resolve() == resolvableType.resolve())) {
            priority += PRIORITY_STEP;
            type = type.getSuperType();
        }
        return priority;
    }

    /**
     * @param tClass to get
     * @param <T>    Annotation type
     * @return specified annotation
     */
    @Override
    public <T extends Annotation> T getParameterAnnotation(Class<T> tClass) {
        return methodOrConstructorParameter.getParameterAnnotation(tClass);
    }

    /**
     * Return true if the parameter can accept the argument.
     * <p>
     * If object is {@code null}, then return {@code true}.
     *
     * @param type to test
     * @return true if the argument is type of the parameter type
     */
    //todo 의존성 제거
    @Override
    public boolean canAccept(Type type) {
        if (this.resolvableType == null)
            this.resolvableType = ResolvableType.forMethodParameter(methodOrConstructorParameter.getMethodParameter());

        //todo 숫자인 경우 int -> long, float or double 변환 등 가능하면 변환 시도 기능 추가, NumberUtils
        return TypeUtils.isAssignable(resolvableType, type);
    }

    /**
     * @return type of the parameter
     */
    @Override
    public Type getParameterType() {
        return this.methodOrConstructorParameter.getGenericParameterType();
    }

    /**
     * @return priority
     */
    @Override
    public int priority() {
        return priority;
    }
}
