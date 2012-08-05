package features.validators.deserialization;

import flex.messaging.config.ConfigMap;
import flex.messaging.validators.DeserializationValidator;

public class TestDeserializationValidator implements DeserializationValidator
{
    /**
     * Simply prints the assignment and always returns true.
     */
    public boolean validateAssignment(Object instance, int index, Object value)
    {
        System.out.println("validateAssign1: [" + (instance == null? "null" : instance.getClass().getName()) + "," + index + "," + value + "]");
        return true;
    }

    /**
     * Simply prints the assignment and always returns true.
     */
    public boolean validateAssignment(Object instance, String propertyName, Object value)
    {
        System.out.println("validateAssign2: [" + (instance == null? "null" : instance.getClass().getName()) + "," + propertyName + "," + value + "]");
        return true;
    }

    /**
     * Simply prints the creation and always returns true.
     */
    public boolean validateCreation(Class<?> c)
    {
        System.out.println("validateCreate: " + (c == null? "null" : c.getName()));
        return true;
    }

    /* (non-Javadoc)
     * @see flex.messaging.FlexConfigurable#initialize(java.lang.String, flex.messaging.config.ConfigMap)
     */
    public void initialize(String id, ConfigMap configMap)
    {
        // No-op.
    }
}