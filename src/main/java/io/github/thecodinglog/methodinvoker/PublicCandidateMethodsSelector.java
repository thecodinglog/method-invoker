package io.github.thecodinglog.methodinvoker;

import io.github.thecodinglog.methodinvoker.annotations.DefaultMethod;
import io.github.thecodinglog.methodinvoker.annotations.MethodQualifier;
import io.github.thecodinglog.methodinvoker.exceptions.NoUniqueQualifierException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jeongjin Kim
 * @since 2021-03-15
 */
final class PublicCandidateMethodsSelector implements CandidateMethodsSelector {
    @Override
    public Method[] select(Class<?> clazz, String qualifier) {
        Method[] methods = clazz.getMethods();
        if (methods.length == 0)
            throw new MethodNotFoundException("No public method exists.");

        Method defaultMethodIfExists = getDefaultMethodIfExists(methods);

        if (qualifier == null && defaultMethodIfExists == null)
            throw new MethodNotFoundException("No default method exists.");

        if (qualifier == null)
            return new Method[]{defaultMethodIfExists};

        Method qualifiedMethodIfExists = getQualifiedMethodIfExists(methods, qualifier);

        if (qualifiedMethodIfExists != null)
            return new Method[]{qualifiedMethodIfExists};

        Method[] methodsMatchingQualifierAndMethodName = getMethodsMatchingQualifierAndMethodName(methods, qualifier);

        if (methodsMatchingQualifierAndMethodName.length == 0)
            throw new MethodNotFoundException("No public method called " + qualifier + ".");

        return methodsMatchingQualifierAndMethodName;
    }

    private Method[] getMethodsMatchingQualifierAndMethodName(Method[] methods, String qualifier) {
        List<Method> qualifiedMethods = new ArrayList<>(methods.length);
        for (Method method : methods) {
            if (method.getName().equals(qualifier))
                qualifiedMethods.add(method);

        }
        return qualifiedMethods.toArray(new Method[0]);
    }

    private Method getQualifiedMethodIfExists(Method[] methods, String qualifier) {
        Map<String, Method> qualifiedMethods = new HashMap<>(methods.length);
        for (Method method : methods) {
            MethodQualifier annotation = method.getAnnotation(MethodQualifier.class);
            if (annotation != null) {
                if (qualifiedMethods.containsKey(annotation.value()))
                    throw new NoUniqueQualifierException(annotation.value() + " is not unique qualifier.");
                qualifiedMethods.put(annotation.value(), method);
            }
        }
        return qualifiedMethods.get(qualifier);
    }

    private Method getDefaultMethodIfExists(Method[] methods) {
        List<Method> defaultMethods = new ArrayList<>(methods.length);

        for (Method method : methods) {
            if (method.getAnnotation(DefaultMethod.class) != null)
                defaultMethods.add(method);
        }
        if (defaultMethods.size() > 1)
            throw new TooManyDefaultException("More then one DefaultMethod exists.");
        else if (defaultMethods.size() == 1)
            return defaultMethods.get(0);
        else
            return null;
    }
}
