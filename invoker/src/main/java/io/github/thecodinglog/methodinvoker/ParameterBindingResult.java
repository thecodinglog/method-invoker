package io.github.thecodinglog.methodinvoker;

/**
 * @author Jeongjin Kim
 * @since 2021-03-26
 */
class ParameterBindingResult {
    private final ParameterAndArgumentHolder parameterAndArgumentHolder;
    private final boolean isOptional;

    ParameterBindingResult(ParameterAndArgumentHolder parameterAndArgumentHolder, boolean isOptional) {
        this.parameterAndArgumentHolder = parameterAndArgumentHolder;
        this.isOptional = isOptional;
    }

    public ParameterAndArgumentHolder getParameterAndArgumentHolder() {
        return parameterAndArgumentHolder;
    }

    public boolean isOptional() {
        return isOptional;
    }
}
