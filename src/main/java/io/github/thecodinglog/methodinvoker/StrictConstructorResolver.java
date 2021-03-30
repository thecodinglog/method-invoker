package io.github.thecodinglog.methodinvoker;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The constructor that the longest parameter length among candidate constructors has will be returned.
 * <p>
 * The detailed constructor candidate selection strategy depends on the implementation of
 * {@link CandidateConstructorsSelector}, {@link MethodArgumentBinder} and {@link PriorityPicker}.
 *
 * @author Jeongjin Kim
 * @since 2021-02-23
 */
final class StrictConstructorResolver implements ConstructorResolver {
    private final CandidateConstructorsSelector candidateResolver;
    private final MethodArgumentBinder methodArgumentBinder;
    private final PriorityPicker priorityPicker;

    public StrictConstructorResolver(
            CandidateConstructorsSelector candidateResolver
            , MethodArgumentBinder methodArgumentBinder
            , PriorityPicker priorityPicker) {
        this.candidateResolver = candidateResolver;
        this.methodArgumentBinder = methodArgumentBinder;
        this.priorityPicker = priorityPicker;
    }

    @Override
    public PrioritizableMethodOrConstructorHolder resolve(Class<?> aClass, Context context) {
        // 생성자 선택
        Constructor<?>[] constructors;
        try {
            constructors = candidateResolver.select(aClass, null);
        } catch (TooManyDefaultException | NoUniqueQualifierException e) {
            throw new ConstructorNotFoundException(e.getMessage(), e);
        } catch (ConstructorNotFoundException e) {
            throw e;
        }

        // 생성자에 파라미터가 많음->적음 순으로 정렬
        Arrays.sort(constructors, (e1, e2) -> Integer.compare(e2.getParameterCount(), e1.getParameterCount()));

        // 이전에 확인했던 생성자의 파라미터 길이
        int beforeConstructorParameterLength = Integer.MAX_VALUE;

        List<PrioritizableMethodOrConstructorHolder> candidatesConstructors = new ArrayList<>(constructors.length);

        for (Constructor<?> constructor : constructors) {
            // 파라미터 길이가 다른데 후보 생성자 개수가 0보다 크면 후보 생성자에서 다시 선택해야함.
            // 더 짧은 파라미터를 가진 생성자는 확인할 필요 없음
            if (beforeConstructorParameterLength > constructor.getParameters().length
                    && candidatesConstructors.size() > 0)
                break;
            PrioritizableMethodOrConstructorHolder holder;
            try {
                holder = methodArgumentBinder.bind(new MethodOrConstructor(constructor), context);
            } catch (MethodBindingException e) {
                throw new ConstructorNotFoundException(e.getMessage(), e);
            }
            if (holder != null)
                candidatesConstructors.add(holder);

            beforeConstructorParameterLength = constructor.getParameters().length;
        }
        if (candidatesConstructors.size() == 0)
            throw new ConstructorNotFoundException("No candidate constructors.");

        Prioritizable pick;
        try {
            pick = priorityPicker.pick(candidatesConstructors);
        } catch (PriorityPickingException e) {
            throw new ConstructorNotFoundException(e.getMessage(), e);
        }
        return (PrioritizableMethodOrConstructorHolder) pick;
    }
}
