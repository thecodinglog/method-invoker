package io.github.thecodinglog.methodinvoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeongjin Kim
 * @since 2021-03-26
 */
public class ParameterNameMethodArgumentBindingStrategy implements MethodArgumentBindingStrategy {
    private static final Logger log = LoggerFactory.getLogger(ParameterNameMethodArgumentBindingStrategy.class);

    @Override
    public ParameterBindingResult tryBind(ParameterAndArgumentHolder parameterAndArgumentHolder, Context context) {
        // 파라미터 이름으로 확인
        if (context.hasKey(parameterAndArgumentHolder.getParameterName())) {
            TypeDescribableObject argCandidate =
                    context.getValueByKey(parameterAndArgumentHolder.getParameterName());
            if (parameterAndArgumentHolder.canAccept(argCandidate.getType())) {
                parameterAndArgumentHolder.accept(argCandidate);
                log.debug("Parameter name binding of " + parameterAndArgumentHolder.getParameterName());
                return new ParameterBindingResult(parameterAndArgumentHolder, false);
            }
        }
        return new ParameterBindingResult(null, true);
    }
}
