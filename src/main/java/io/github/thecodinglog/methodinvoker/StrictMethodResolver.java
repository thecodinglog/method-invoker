package io.github.thecodinglog.methodinvoker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * When all values of parameter exist in the context, the corresponding method is selected.
 *
 * @author Jeongjin Kim
 * @since 2021-03-10
 */
final class StrictMethodResolver implements MethodResolver {
    private final CandidateMethodsSelector candidateMethodsSelector;
    private final MethodArgumentBinder methodArgumentBinder;
    private final PriorityPicker priorityPicker;

    public StrictMethodResolver(CandidateMethodsSelector candidateMethodsSelector
            , MethodArgumentBinder methodArgumentBinder
            , PriorityPicker priorityPicker) {
        this.candidateMethodsSelector = candidateMethodsSelector;
        this.methodArgumentBinder = methodArgumentBinder;
        this.priorityPicker = priorityPicker;
    }

    @Override
    public PrioritizableMethodOrConstructorHolder resolve(Class<?> aClass, String methodName, Context context) {
        Method[] methods = candidateMethodsSelector.select(aClass, methodName);

        // 생성자에 파라미터가 많음->적음 순으로 정렬
        Arrays.sort(methods, (e1, e2) -> Integer.compare(e2.getParameterCount(), e1.getParameterCount()));

        // 이전에 확인했던 생성자의 파라미터 길이
        int beforeConstructorParameterLength = Integer.MAX_VALUE;

        List<PrioritizableMethodOrConstructorHolder> candidatesMethods = new ArrayList<>(methods.length);

        for (Method method : methods) {
            // 파라미터 길이가 다른데 후보 생성자 개수가 0보다 크면 후보 생성자에서 다시 선택해야함.
            // 더 짧은 파라미터를 가진 생성자는 확인할 필요 없음
            if (beforeConstructorParameterLength > method.getParameters().length && candidatesMethods.size() > 0)
                break;
            PrioritizableMethodOrConstructorHolder holder =
                    methodArgumentBinder.bind(new MethodOrConstructor(method), context);
            if (holder != null)
                candidatesMethods.add(holder);

            beforeConstructorParameterLength = method.getParameters().length;
        }
        if (candidatesMethods.size() == 0)
            throw new MethodNotFoundException(
                    String.format("No suitable method. : Class[%s], Method[%s]", aClass.getName(), methodName));

        Prioritizable pick;
        try {
            pick = priorityPicker.pick(candidatesMethods);
        } catch (PriorityPickingException e) {
            throw new MethodNotFoundException(
                    String.format("PriorityPicker exception : %s", aClass.getName()), e);
        }
        return (PrioritizableMethodOrConstructorHolder) pick;
    }

    @Override
    public PrioritizableMethodOrConstructorHolder resolve(Class<?> aClass, Context context) {
        return this.resolve(aClass, null, context);
    }
}
