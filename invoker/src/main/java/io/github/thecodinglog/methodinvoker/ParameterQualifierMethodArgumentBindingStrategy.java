package io.github.thecodinglog.methodinvoker;

import io.github.thecodinglog.methodinvoker.annotations.ParameterQualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
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
