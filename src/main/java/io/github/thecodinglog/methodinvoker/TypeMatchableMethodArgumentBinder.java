package io.github.thecodinglog.methodinvoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeongjin Kim
 * @since 2021-03-12
 */
final class TypeMatchableMethodArgumentBinder implements MethodArgumentBinder {
    private static final Logger log = LoggerFactory.getLogger(TypeMatchableMethodArgumentBinder.class);

    @Override
    public PrioritizableMethodOrConstructorHolder bind(MethodOrConstructor methodOrConstructor, Context context) {
        List<ParameterAndArgumentHolder> parameterAndArgumentHolders =
                new ArrayList<>(methodOrConstructor.parameterCount());

        if (methodOrConstructor.parameterCount() > 0 && context == null)
            throw new MethodBindingException("Args exist but no context exists.");

        params:
        for (int i = 0; i < methodOrConstructor.parameterCount(); i++) {
            MethodOrConstructorParameter methodParameter =
                    new SpringMethodOrConstructorParameter(methodOrConstructor, i);
            ParameterAndArgumentHolder parameterAndArgumentHolder =
                    new PrioritizableParameterAndArgumentHolder(methodParameter);

            List<MethodArgumentBindingStrategy> parameterQualifierBindingStrategies =
                    new ArrayList<>();
            parameterQualifierBindingStrategies.add(
                    new ParameterQualifierMethodArgumentBindingStrategy());
            parameterQualifierBindingStrategies.add(
                    new ParameterNameMethodArgumentBindingStrategy());
            parameterQualifierBindingStrategies.add(
                    new ParameterTypeMethodArgumentBindingStrategy());
            parameterQualifierBindingStrategies.add(
                    new ParameterNameAndJsonMethodArgumentBindingStrategy());

            for (MethodArgumentBindingStrategy strategy : parameterQualifierBindingStrategies) {
                ParameterBindingResult parameterBindingResult
                        = strategy.tryBind(parameterAndArgumentHolder, context);

                if (parameterBindingResult.getParameterAndArgumentHolder() != null) {
                    parameterAndArgumentHolders.add(parameterBindingResult.getParameterAndArgumentHolder());
                    continue params;
                } else {
                    if (!parameterBindingResult.isOptional()) {
                        break params;
                    }
                }
            }
            log.debug("Can't bind parameter [{}][{}]",
                    methodParameter.getParameterName(),
                    methodParameter.getParameterType());
            // If getting here, you do not need to try the next parameter because one parameter could not be bound.
            break;
        }

        // When the number of arguments for which mapping was confirmed and the number of parameters ara same
        // then the constructor Approved.
        if (parameterAndArgumentHolders.size() == methodOrConstructor.parameterCount()) {
            return new PrioritizableMethodOrConstructorHolder(methodOrConstructor, parameterAndArgumentHolders);
        } else {
            return null;
        }
    }
}
