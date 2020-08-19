package services;

import com.fasterxml.jackson.databind.JsonNode;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class AvroAutoCoder {
    // dataSupplier usually hold the properties of the event (eg. name, date, etc.)
    public static Object createAndSet(Class clazz, Function<String, String> dataSupplier) throws Exception {

        Object builderInstance = findMethod(clazz, "newBuilder")
                .invoke(null);

        // Create new BuilderClass
        // For each setter add the Setter from the Avro Class?
        Class<?> builderClass = builderInstance.getClass();
        getSetters2(clazz).forEach(setter -> {
            try {
                String fieldName = setter.getName().substring(3).toLowerCase();
                findMethod(builderClass, setter.getName())
                        .invoke(builderInstance, dataSupplier.apply(fieldName));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });

        return findMethod(builderClass, "build")
                .invoke(builderInstance);
    }

    public static Object createAndSet2(Class clazz, Function<String, JsonNode> dataSupplier) throws Exception {

        Object builderInstance = findMethod(clazz, "newBuilder")
                .invoke(null);

        // Create new BuilderClass
        // For each setter add the Setter from the Avro Class?
        Class<?> builderClass = builderInstance.getClass();
        getSetters2(clazz).forEach(setter -> {
            try {
                String fieldName = setter.getName().substring(3).toLowerCase();
                findMethod(builderClass, setter.getName())
                        .invoke(builderInstance, dataSupplier.apply(fieldName));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });

        return findMethod(builderClass, "build")
                .invoke(builderInstance);

    }

    // Gets methods of class which match string
    // In this case "newBuilder"
    private static Method findMethod(Class clazz, String methodName) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.getName().equals(methodName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

    }

    // Find all setter of class
    private static Stream<Method> getSetters2(Class clazz) {
        Optional<Class> first = Arrays.stream(clazz.getDeclaredClasses())
                .findFirst();

        return Arrays.stream(first.get().getDeclaredMethods())
                .filter(method -> method.getName().startsWith("set"));
    }
}
