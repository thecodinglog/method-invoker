package sample.context;

import io.github.thecodinglog.methodinvoker.Context;
import io.github.thecodinglog.methodinvoker.exceptions.NoUniqueElementException;
import io.github.thecodinglog.methodinvoker.TypeDescribableObject;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Jeongjin Kim
 * @since 2021-03-25
 */
public class FakeContext implements Context {
    private final Map<String, TypeDescribableObject> store = new HashMap<>();

    @Override
    public TypeDescribableObject getValueByKey(String key) {
        return store.get(key);
    }

    @Override
    public TypeDescribableObject getOneValueByType(Type type) {
        ResolvableType resolvableType = ResolvableType.forType(type);
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, TypeDescribableObject> entry : store.entrySet()) {
            if (resolvableType.isAssignableFrom(ResolvableType.forType(entry.getValue().getType())))
                keys.add(entry.getKey());
        }
        if (keys.size() > 1)
            throw new NoUniqueElementException("No unique.");
        else if (keys.size() == 0)
            throw new NoSuchElementException("No element.");
        else
            return store.get(keys.get(0));
    }

    @Override
    public boolean hasKey(String key) {
        return store.containsKey(key);
    }

    @Override
    public boolean hasType(Type type) {
        ResolvableType resolvableType = ResolvableType.forType(type);
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, TypeDescribableObject> entry : store.entrySet()) {
            if (resolvableType.isAssignableFrom(ResolvableType.forType(entry.getValue().getType())))
                keys.add(entry.getKey());
        }

        return keys.size() > 0;
    }

    @Override
    public void add(String key, TypeDescribableObject typeDescribableObject) {
        store.put(key, typeDescribableObject);
    }
}
