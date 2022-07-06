package io.github.thecodinglog.methodinvoker;

import io.github.thecodinglog.methodinvoker.exceptions.NoUniqueElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;

/**
 * Finds and binds an object in the context by the method parameter type.
 * <p>
 * If it cannot be retrieved by the parameter type in the context,
 * {@link ParameterBindingResult} in which the object is {@code null} is returned to check the next parameter.
 * When it cannot be found, the processing depends on the implementation using that strategy.
 *
 * @author Jeongjin Kim
 * @since 2021-03-26
 */
public class ParameterTypeMethodArgumentBindingStrategy implements MethodArgumentBindingStrategy {
    private static final Logger log = LoggerFactory.getLogger(ParameterTypeMethodArgumentBindingStrategy.class);

    @Override
    public ParameterBindingResult tryBind(ParameterAndArgumentHolder parameterAndArgumentHolder, Context context) {
        // check by type
        if (context.hasType(parameterAndArgumentHolder.getParameterType())) {
            TypeDescribableObject argCandidate;
            try {
                argCandidate = context.getOneValueByType(parameterAndArgumentHolder.getParameterType());
            } catch (NoUniqueElementException | NoSuchElementException e) {
                // If there are two parameters, one of them is bound as a Key and the others are bound by a type,
                // but the next constructor is tried if there are more or no more than two parameters.
                log.debug("[{}] is skipped. No unique parameter set.", parameterAndArgumentHolder.getParameterType());
                return new ParameterBindingResult(null, false);
            }
            parameterAndArgumentHolder.accept(argCandidate);
            log.debug("Parameter [{}] has bound by type [{}].",
                    parameterAndArgumentHolder.getParameterName(),
                    parameterAndArgumentHolder.getParameterType());
            return new ParameterBindingResult(parameterAndArgumentHolder, true);
        } else {
            log.debug("No parameter type [{}] in the context.", parameterAndArgumentHolder.getParameterType());
            return new ParameterBindingResult(null, true);
        }
    }
}
