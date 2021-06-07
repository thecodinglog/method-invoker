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
        // 타입으로 확인
        if (context.hasType(parameterAndArgumentHolder.getParameterType())) {
            TypeDescribableObject argCandidate;
            try {
                argCandidate = context.getOneValueByType(parameterAndArgumentHolder.getParameterType());
            } catch (NoUniqueElementException | NoSuchElementException e) {
                // 파라미터가 2개 중 하나는 Key 로 바인드되고, 나머지는 타입으로 바인드 시도했는데 2개 이상이거나 없으면 다음 생성자를 시도함
                log.debug("{} is skipped.", parameterAndArgumentHolder.getParameterType());
                return new ParameterBindingResult(null, false);
            }
            parameterAndArgumentHolder.accept(argCandidate);
            log.debug("Type binding of {}", parameterAndArgumentHolder.getParameterType());
            return new ParameterBindingResult(parameterAndArgumentHolder, true);
        } else {
            return new ParameterBindingResult(null, true);
        }
    }
}
