package io.github.thecodinglog.methodinvoker;

import io.github.thecodinglog.methodinvoker.annotations.ParameterQualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Finds and binds an object in the context by the parameter qualifier.
 * <p>
 * If the object taken by the parameter qualifier from the context is not
 * compatible with the parameter type, it is considered a failure.
 * Handling in case of failure depends on the implementation using that strategy.
 * <p>
 * If it cannot be retrieved by the method qualifier in the context, it is considered a failure.
 * the next processing is depends on the implementation using that strategy.
 * <p>
 * If {@link ParameterQualifier} does not exist in the parameter,
 * {@link ParameterBindingResult} in which the object is {@code null} is
 * returned so that the next parameter can be checked.
 *
 * @author Jeongjin Kim
 * @since 2021-03-26
 */
public class ParameterQualifierMethodArgumentBindingStrategy implements MethodArgumentBindingStrategy {
    private static final Logger log = LoggerFactory.getLogger(ParameterQualifierMethodArgumentBindingStrategy.class);

    @Override
    public ParameterBindingResult tryBind(ParameterAndArgumentHolder parameterAndArgumentHolder, Context context) {
        String contextKey;
        ParameterQualifier annotation = parameterAndArgumentHolder.getParameterAnnotation(ParameterQualifier.class);
        if (annotation != null) {
            contextKey = annotation.value();
            if (context.hasKey(contextKey)) {
                TypeDescribableObject argCandidate = context.getValueByKey(contextKey);

                if (parameterAndArgumentHolder.canAccept(argCandidate.getType()))
                    parameterAndArgumentHolder.accept(argCandidate);
                else
                    throw new ClassCastException(
                            String.format("Annotated parameter [%s] is not a instance of [%s]"
                                    , contextKey, parameterAndArgumentHolder.getParameterType()));
                log.debug("Annotation binding of " + contextKey);
                return new ParameterBindingResult(parameterAndArgumentHolder, false);
            } else {
                return new ParameterBindingResult(null, false);
            }
        } else {
            return new ParameterBindingResult(null, true);
        }
    }
}
