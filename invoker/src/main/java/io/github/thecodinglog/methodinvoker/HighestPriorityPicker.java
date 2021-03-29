package io.github.thecodinglog.methodinvoker;

import java.util.List;

/**
 * Returns the object with the highest priority.
 * <p>
 * If there is more than one of the highest priority, {@link PriorityPickingException} is thrown.
 * There must be at least one object to select. Otherwise, an {@link IllegalArgumentException} is thrown.
 *
 * @author Jeongjin Kim
 * @since 2021-03-12
 */
final class HighestPriorityPicker implements PriorityPicker {
    @Override
    public Prioritizable pick(List<? extends Prioritizable> object) {
        if (object == null)
            throw new PriorityPickingException("Should not be null.");

        if (object.size() == 0)
            throw new PriorityPickingException("No objects to pick.");

        if (object.size() == 1)
            return object.get(0);

        // 후보들 중에서 최선을 선택해야 함
        // 우선순위가 가장 높은 것(0)에서 낮은 순(Integer.MAX_VALUE)으로 정렬
        object.sort((o1, o2) -> {
            if (o1.priority() < o1.priority())
                return 1;
            else if (o1.priority() == o2.priority())
                return 0;
            else
                return -1;
        });

        if (object.get(0).priority() == object.get(1).priority())
            throw new PriorityPickingException("Ambiguous priority objects");

        return object.get(0);
    }
}
