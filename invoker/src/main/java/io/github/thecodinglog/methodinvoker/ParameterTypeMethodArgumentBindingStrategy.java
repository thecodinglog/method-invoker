package io.github.thecodinglog.methodinvoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;

/**
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
                log.debug(String.format("[%s] is skipped.", parameterAndArgumentHolder.getParameterType()));
                return new ParameterBindingResult(null, false);
            }
            parameterAndArgumentHolder.accept(argCandidate);
            log.debug("Type binding of " + parameterAndArgumentHolder.getParameterType());
            return new ParameterBindingResult(parameterAndArgumentHolder, true);
        }else{
            return new ParameterBindingResult(null, true);
        }
    }
}
