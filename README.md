# Method-Invoker

[![CI](https://github.com/thecodinglog/method-invoker/actions/workflows/ci.yml/badge.svg)](https://github.com/thecodinglog/method-invoker/actions/workflows/ci.yml)
[![Publish](https://github.com/thecodinglog/method-invoker/actions/workflows/publish.yml/badge.svg?event=workflow_dispatch)](https://github.com/thecodinglog/method-invoker/actions/workflows/publish.yml)

_Method-Invoker_ is **a tool for invoking methods at runtime** using class names and method modifiers.

The key feature of Method-Invoker is to create an object by selecting the most appropriate class constructor and invoke
the best matching method in the current context.

```java
class OrderManagerTest {
    private MethodInvoker methodInvoker = new StrictMethodInvoker();
    private Context context = new FakeContext();

    void cancelOrder() {
        String fqcn = "sample.order.OrderManager";
        context.add("orderId", new TypeDescribableObject("newId"));

        TypeDescribableObject methodResult = methodInvoker.invoke(fqcn, "cancelOrder", context);
    }
}
```

The example test code above shows how to invoke the `cancelOrder` method of the `OrderManager` class.
The `methodInvoker#invoke()` method creates an `OrderManager` object and invokes the requested `CancelOrder()` method
from the object.

The `OrderManager` class is a plain Java class and has no dependency on _Method-Invoker_. Marker annotations can also be
used to specify specific constructors and specific methods.

Which constructor and which method to use depends on the data in the context and the binding strategy.

## Requirements

- JDK 1.8 or higher

## Install

### Maven

```xml
<dependency>
  <groupId>io.github.thecodinglog</groupId>
  <artifactId>method-invoker</artifactId>
  <version>0.1.1</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.thecodinglog:method-invoker:0.1.1'
```

## Selecting constructor strategy

### Specify default constructor

When a class has multiple constructors, you can specify a default constructor with the `DefaultConstructor` annotation.

```java
class DefaultConstructorAnnotation {
    public DefaultConstructorAnnotation() {
    }

    @DefaultConstructor
    public DefaultConstructorAnnotation(String a) {
    }
}
```

Even if you specify a default constructor, object creation fails if an appropriate argument corresponding to the
constructor parameter does not exist in the context.

### Longest parameter first

The constructor with the longest constructor parameter matching the context is chosen first. In other words, even if the
constructor parameter is the longest if any of the parameters do not match the context, the constructor is not used.

Let's say you have a value with `name` and `age` as keys in the context.

```java
class MyClass {
    public MyClass(String name, String address, int age) {
    }

    public MyClass(String name, int age) {
    }

    public MyClass(String name) {
    }
}
```

When the constructor is defined as above, the final selected constructor is `MyClass(String name, int age)`.

### Ambiguous constructor

When it can't select only one constructor then an exception will be thrown, as shown below. Be careful.

```java
class MyClass {
    public MyClass(int age) {
    }

    public MyClass(String name) {
    }
}
```

## Selecting method strategy

### Specify default method

`MethodInvoker` is the core interface of _Method-Invoker_ provides that a method that takes a _class name_, a _method
name_, and a _context_ as arguments.

```java
public interface MethodInvoker {
    TypeDescribableObject invoke(String fullQualifiedClassName, String methodName, Context context);
}
```

When the method name is omitted here, i.e. null, it finds the **default method**. If the name is omitted and there is no
default method specified, an exception is thrown.

The default method can be specified with the `DefaultMethod` annotation as shown below.

```java
class MyClass {
    @DefaultMethod
    public void myMethod(int age) {
    }

    public void myMethod(String name) {
    }
}
```

Like the constructor, it cannot be used if the parameters of selected method and context do not match.

### Specify method qualifier

You can specify a qualifier on a method with the `MethodQualifier` annotation.
_Method-Invoker_ finds a method that matches the method name received as an argument and the qualifier value and checks
if it can be used. It can also be used in constructors.

```java
class MyClass {
    @MethodQualifier("yourMethod")
    public void myMethod(int age) {
    }

    public void myMethod(String name) {
    }
}
```

The annotation value specified by `MethodQualifier` must be unique within the class.

### Longest parameter first

If a method is not found with `DefaultMethod` and `MethodQualifier`, a method matching the method name received as an
argument is selected as a candidate. Among the selected candidate methods, the method with the **longest parameter is
selected** as the final method to be executed.

## Parameter and argument matching strategy

### Specify parameter qualifier

You can explicitly specify the context key to match with the `ParameterQualifier`
annotation in the method parameter.

```java
class MyClass {
    public void myMethod(int age) {
    }

    public void myMethod(@ParameterQualifier("nickname") String name) {
    }
}
```

If the value corresponding to the key specified by `ParameterQualifier`
does not exist in the context, the parameter matching fails. Depending on the detailed strategy, if any of the
parameters fail to match, the method or constructor is not selected as a candidate.

### Matching parameter name and context key

For parameters where `ParameterQualifier` is not specified, the **parameter name** is used to find a matching value in
the context. If the parameter type and the type of the object in the context are not compatible, the matching fails.

### Matching parameter type and context type

If the matching between the `ParameterQualifier` and the parameter name fails, a value with a type compatible with the
parameter type is searched in the context. Select the value only when the compatible value is unique.

### Primitive and Wrapped Type are regarded as the same type

If the method parameter type is `int` and the type of the value corresponding to the context is `Integer`, it is treated
as the **same type**.

### Select the object of the type closest to the parameter type

The closer the type of the value retrieved from the context and the type of the parameter have priority.

For example, when the type that exists in the context is `String` and the method parameter has `String`
and `CharSequence` types, select the `String` type.

The code below selects `void myMethod(String name)`.

```java
class MyClass {
    public void myMethod(String name) {
    }

    public void myMethod(CharSequence name) {
    }
}
```

## Context

The central interface provides objects for an application. The context can be changed in running to get and run
dynamically methods. The context may have a hierarchy.

```java
public interface Context {
    TypeDescribableObject getValueByKey(String key);

    TypeDescribableObject getOneValueByType(Type type);

    boolean hasKey(String key);

    boolean hasType(Type type);

    void add(String key, TypeDescribableObject typeDescribableObject);
}
```

When a client requests an object, it looks for the requested object from the lowest layer to the higher layer. If the
requested object is not found in the current layer, it is looked up in the upper layer. If an object is found in one
hierarchy, additional objects are searched for in the same level. Particularly when only one object is to be returned
using a type, only the first layer that is the object founded is the boundary for searching.

## TypeDescribableObject

It is a class that explicitly stores an object and its type. Ordinary classes can get the type of the class, but generic
classes don't have exact type information due to type erasure. So explicitly put the
type. `io.github.thecodinglog.methodinvoker.TypeReference`
is helpful to get type information from Generic class.
