package io.github.thecodinglog.methodinvoker;

import io.github.thecodinglog.methodinvoker.exceptions.NoUniqueElementException;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Context that maintains only a single level context.
 *
 * @author Jeongjin Kim
 * @see org.springframework.core.ResolvableType
 * @since 2021-03-25
 */
public class SingleLevelContext implements Context {
    private final Map<String, TypeDescribableObject> store = new HashMap<>();

    @Override
    public TypeDescribableObject getValueByKey(String key) {
        return store.get(key);
    }

    @Override
    public TypeDescribableObject getOneValueByType(Type type) {
        List<String> keys = extractKeysByType(type);
        if (keys.size() > 1)
            throw new NoUniqueElementException("No unique.");
        else if (keys.size() == 0)
            throw new NoSuchElementException("No element.");
        else
            return store.get(keys.get(0));
    }

    private List<String> extractKeysByType(Type type) {
        ResolvableType resolvableType = ResolvableType.forType(type);
        List<String> keys = new ArrayList<>(store.entrySet().size());
        for (Map.Entry<String, TypeDescribableObject> entry : store.entrySet()) {
            if (resolvableType.isAssignableFrom(ResolvableType.forType(entry.getValue().getType())))
                keys.add(entry.getKey());
        }
        return keys;
    }

    @Override
    public boolean hasKey(String key) {
        return store.containsKey(key);
    }

    @Override
    public boolean hasType(Type type) {
        return extractKeysByType(type).size() > 0;
    }

    @Override
    public void add(String key, TypeDescribableObject typeDescribableObject) {
        store.put(key, typeDescribableObject);
    }
}
