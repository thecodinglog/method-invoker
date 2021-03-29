package io.github.thecodinglog.methodinvoker;

/**
 * @author Jeongjin Kim
 * @since 2021-03-26
 */
interface MethodArgumentBindingStrategy {
    ParameterBindingResult tryBind(ParameterAndArgumentHolder parameterAndArgumentHolder, Context context);
}
