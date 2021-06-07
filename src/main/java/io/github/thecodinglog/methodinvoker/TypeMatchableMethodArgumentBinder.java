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

            // ParameterQualifier 에 명시된 키 값으로 확인
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
                    if (!parameterBindingResult.isOptional())
                        break params;
                }
            }
            // 여기까지 오면 파라미터 하나를 바인드 못 한 것이기 떄문에 다음 파라미터를 시도할 필요가 없음
            break;
        }

        // 매핑이 확인된 인자수와 파라미터 수가 같아야 해당 컨스트럭터를 인정함
        if (parameterAndArgumentHolders.size() == methodOrConstructor.parameterCount()) {
            return new PrioritizableMethodOrConstructorHolder(methodOrConstructor, parameterAndArgumentHolders);
        } else {
            return null;
        }
    }
}
