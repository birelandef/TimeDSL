package com.birelendef;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import static org.junit.Assert.assertEquals;

public class PostExecutionChecker {
    /**
     *
     * @param properties - list with all properties {@link TimeSequence}
     * Example:
     *   props.put(String.class.getMethod("getLength"), ONE_HOUR.multipliedBy(5L) );
     *   props.put(String.class.getMethod("getPiquetCount"), 5);
     *   props.put(String.class.getMethod("isEmpty"), false);
     *   props.put(String.class.getMethod("isContinuous"), true);
     *
     * So, such properties are described {@link TimeSequence}
     *                  - length = 5 hours
     *                  - piquets = 5
     *                  - not empty
     *                  - continuous
     * @param method - invoking method {@link TimeSequence}
     * @param initTimeLine - initial object
     * @param inputArgs - args for {@link method}
     */
    public static void execute(Properties properties, Method method, TimeSequence initTimeLine, Object... inputArgs){

        TimeSequence beforeExecition = initTimeLine.copy();
        try {
            Object result = method.invoke(initTimeLine, inputArgs);
            assertEquals("Initial object didn't change", beforeExecition, initTimeLine);
            Method checkingProperty = null;
            for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
                checkingProperty = (Method)objectObjectEntry.getKey();
                assertEquals("Property " + checkingProperty.getName() + " is correct",
                        checkingProperty.invoke(result),objectObjectEntry.getValue() );
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.err.println("Error  while reflection invoke");
        }
    }
}
