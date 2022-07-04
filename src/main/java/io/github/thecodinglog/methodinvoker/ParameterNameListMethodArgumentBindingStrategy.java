package io.github.thecodinglog.methodinvoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Finds and binds an object in the context by the method parameter name.
 * <p>
 * It is checked once more at the element level whether the objects of the collection are bindable.
 * <p>
 * If the object taken by the parameter name from the context is not
 * compatible with the parameter type, it is considered a failure.
 * Handling in case of failure depends on the implementation using that strategy.
 * <p>
 * If it cannot be retrieved by the method name in the context,
 * {@link ParameterBindingResult} in which the object is {@code null} is
 * returned to check the next parameter.
 *
 * @author Jeongjin Kim
 * @since 2022-07-04
 */
public class ParameterNameListMethodArgumentBindingStrategy implements MethodArgumentBindingStrategy {
    private static final Logger log = LoggerFactory.getLogger(ParameterNameListMethodArgumentBindingStrategy.class);

    @Override
    public ParameterBindingResult tryBind(ParameterAndArgumentHolder parameterAndArgumentHolder, Context context) {
        // check by parameter name
        if (context.hasKey(parameterAndArgumentHolder.getParameterName())) {
            TypeDescribableObject argCandidate =
                    context.getValueByKey(parameterAndArgumentHolder.getParameterName());
            if (parameterAndArgumentHolder.canAccept(argCandidate.getObject())) {
                parameterAndArgumentHolder.accept(argCandidate);
                log.debug("Parameter name binding of {}", parameterAndArgumentHolder.getParameterName());
                return new ParameterBindingResult(parameterAndArgumentHolder, false);
            }
        }
        return new ParameterBindingResult(null, true);
    }
}
