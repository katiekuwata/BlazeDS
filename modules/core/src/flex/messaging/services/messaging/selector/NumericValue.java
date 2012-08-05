/*
 * %W% %E%
 *
 * Copyright 1999-2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 *
 */
package flex.messaging.services.messaging.selector;

import java.lang.reflect.Constructor;

/**
 * Wrapper class for a NumericValue that provides math operations on numbers.
 *
 * This class is a necessary because Java does not support arithmetic operations
 * in java.lang.Number
 *
 * @author Farrukh Najmi
 * @exclude
 */
public class NumericValue {

    Number value = null;
    String image = null;
    int imageType = 0;

    private static final int ByteValue = 0;
    private static final int ShortValue = 1;
    private static final int IntValue = 2;
    private static final int FloatValue = 3;
    static final int LongValue = 4;
    static final int DoubleValue = 5;


    private static String[]  indexToTypeMap = {
        "java.lang.Byte",
        "java.lang.Short",
        "java.lang.Integer",
        "java.lang.Float",
        "java.lang.Long",
        "java.lang.Double"
    };

    private static final int[][] returnTypes =  {
        {ByteValue, ShortValue, IntValue, FloatValue, LongValue, DoubleValue},
        {ShortValue, ShortValue, IntValue, FloatValue, LongValue, DoubleValue},
        {IntValue, IntValue, IntValue, FloatValue, LongValue, DoubleValue},
        {FloatValue, FloatValue, FloatValue, FloatValue, FloatValue, DoubleValue},
        {LongValue, LongValue, LongValue, FloatValue, LongValue, DoubleValue},
        {DoubleValue, DoubleValue, DoubleValue, DoubleValue, DoubleValue, DoubleValue}
    };

    public NumericValue(Object obj) throws ClassCastException {
        //It is OK for a NumericValue to be null. If not null it must be a Number sub-class

        if (value != null && !(obj instanceof java.lang.Number)) {
            throw new ClassCastException();
        }
        if (obj instanceof NumericValue) {
            NumericValue n = (NumericValue) obj;
            this.value = n.value;
            this.image = n.image;
            this.imageType = n.imageType;
        } else {
            this.value = (Number)obj;
        }
    }

    public NumericValue(String image, int imageType) {
        this.image = image;
        this.imageType = imageType;
        this.value = null;
    }

    public Number getValue() {
        if (value == null && image != null) {
            switch (imageType) {
            case DoubleValue:
                value = new Double(image);
                break;
            case LongValue:
                value = Long.decode(image);
                break;
            default:
            }
            image = null;
            imageType = -1;
        }

        return value;
    }

    private int getIndexForType(Number obj) {

        int index = -1;
        String typeName = obj.getClass().getName();
        //System.err.println(typeName);
        if (typeName.equals("java.lang.Byte")) {
            index = ByteValue;
        }
        else if (typeName.equals("java.lang.Short")) {
            index = ShortValue;
        }
        else if (typeName.equals("java.lang.Integer")) {
            index = IntValue;
        }
        else if (typeName.equals("java.lang.Float")) {
            index = FloatValue;
        }
        else if (typeName.equals("java.lang.Long")) {
            index = LongValue;
        }
        else if (typeName.equals("java.lang.Double")) {
            index = DoubleValue;
        }

        return index;
    }

    int getUnifiedTypeIndex(Number val1, Number val2) {
        int index = -1;

        if ((val1 != null) && (val2 != null)) {
            int index1 = getIndexForType(val1);
            int index2 = getIndexForType(val2);

            //System.err.println("index1=" + index1 + "  index2=" + index2);
            index = returnTypes[index1][index2];
        }
        return index;
    }

    private Number convertNumber(Number val, int typeIndex) {
        Number newVal = null;

        String newClassName = indexToTypeMap[typeIndex];
        //System.err.println("retTypeIndex = " + typeIndex + " newClassName=" + newClassName);
        try {
            Class newClass = Class.forName(newClassName);
            Class[] paramTypes = {
                Class.forName("java.lang.String")
            };

            Constructor constr = newClass.getDeclaredConstructor(paramTypes);

            Object[] args = {
                val.toString()
            };
            newVal = (Number)constr.newInstance(args);
        }
        catch (Exception e) {
            // can't happen.  Just do something reasonable.
            throw new RuntimeException(e);
        }
        return newVal;
    }

    public Number add(NumericValue num) {
        Number result = null;

        if (getValue() != null && num != null) {
            Number value2 = num.getValue();
            int typeIndex = getUnifiedTypeIndex(value, value2);
            //System.err.println("retVal is of type" + indexToTypeMap[typeIndex]);

            Number val1 = convertNumber(value, typeIndex);
            Number val2 = convertNumber(value2, typeIndex);

            //System.err.("value = " + value + " val1 = " + val1);
            //System.err.println("value2 = " + value2 + " val2 = " + val2);
            switch (typeIndex) {
                case ByteValue:
                    result = new Byte((byte)(val1.byteValue() + val2.byteValue()));
                    break;
                case ShortValue:
                    result = new Short((short)(val1.shortValue() + val2.shortValue()));
                    break;
                case IntValue:
                    result = new Integer(val1.intValue() + val2.intValue());
                    break;
                case FloatValue:
                    result = new Float(val1.floatValue() + val2.floatValue());
                    break;
                case LongValue:
                    result = new Long(val1.longValue() + val2.longValue());
                    break;
                case DoubleValue:
                    result = new Double(val1.doubleValue() + val2.doubleValue());
                    break;
                default:
                    //Undefined value
                    break;
            }
        }

        return result;
    }

    public Number subtract(NumericValue num) {
        Number result = null;

        if (getValue() != null && num != null) {
            Number value2 = num.getValue();
            int typeIndex = getUnifiedTypeIndex(value, value2);
            //System.err.println("retVal is of type" + indexToTypeMap[typeIndex]);

            Number val1 = convertNumber(value, typeIndex);
            Number val2 = convertNumber(value2, typeIndex);

            //System.err.println("value = " + value + " val1 = " + val1);
            //System.err.println("value2 = " + value2 + " val2 = " + val2);
            switch (typeIndex) {
                case ByteValue:
                    result = new Byte((byte)(val1.byteValue() - val2.byteValue()));
                    break;
                case ShortValue:
                    result = new Short((short)(val1.shortValue() - val2.shortValue()));
                    break;
                case IntValue:
                    result = new Integer(val1.intValue() - val2.intValue());
                    break;
                case FloatValue:
                    result = new Float(val1.floatValue() - val2.floatValue());
                    break;
                case LongValue:
                    result = new Long(val1.longValue() - val2.longValue());
                    break;
                case DoubleValue:
                    result = new Double(val1.doubleValue() - val2.doubleValue());
                    break;
                default:
                    //Undefined value
                    break;
            }
        }
        return result;
    }

    public Number multiply(NumericValue num) {
        Number result = null;

        if (getValue() != null && num != null) {
            Number value2 = num.getValue();
            int typeIndex = getUnifiedTypeIndex(value, value2);
            //System.err.println("retVal is of type" + indexToTypeMap[typeIndex]);

            Number val1 = convertNumber(value, typeIndex);
            Number val2 = convertNumber(value2, typeIndex);

            //System.err.println("value = " + value + " val1 = " + val1);
            //System.err.println("value2 = " + value2 + " val2 = " + val2);
            switch (typeIndex) {
                case ByteValue:
                    result = new Byte((byte)(val1.byteValue() * val2.byteValue()));
                    break;
                case ShortValue:
                    result = new Short((short)(val1.shortValue() * val2.shortValue()));
                    break;
                case IntValue:
                    result = new Integer(val1.intValue() * val2.intValue());
                    break;
                case FloatValue:
                    result = new Float(val1.floatValue() * val2.floatValue());
                    break;
                case LongValue:
                    result = new Long(val1.longValue() * val2.longValue());
                    break;
                case DoubleValue:
                    result = new Double(val1.doubleValue() * val2.doubleValue());
                    break;
                default:
                    //Undefined value
                    break;
            }
        }

        return result;
    }

    public Number divide(NumericValue num) {
        Number result = null;

        if (getValue() != null && num != null) {
            Number value2 = num.getValue();
            int typeIndex = getUnifiedTypeIndex(value, value2);
            //System.err.println("retVal is of type" + indexToTypeMap[typeIndex]);

            Number val1 = convertNumber(value, typeIndex);
            Number val2 = convertNumber(value2, typeIndex);

            //System.err.println("value = " + value + " val1 = " + val1);
            //System.err.println("value2 = " + value2 + " val2 = " + val2);
            switch (typeIndex) {
                case ByteValue:
                    result = new Byte((byte)(val1.byteValue() / val2.byteValue()));
                    break;
                case ShortValue:
                    result = new Short((short)(val1.shortValue() / val2.shortValue()));
                    break;
                case IntValue:
                    result = new Integer(val1.intValue() / val2.intValue());
                    break;
                case FloatValue:
                    result = new Float(val1.floatValue() / val2.floatValue());
                    break;
                case LongValue:
                    result = new Long(val1.longValue() / val2.longValue());
                    break;
                case DoubleValue:
                    result = new Double(val1.doubleValue() / val2.doubleValue());
                    break;
                default:
                    //Undefined value
                    break;
            }
        }
        return result;
    }

    public Number negate() {
        Number result = null;

        if (image != null) {
            image = "-" + image;
            return getValue();
        }

        if (getValue() != null) {
            int typeIndex = getIndexForType(value);
            //System.err.println("retVal is of type" + indexToTypeMap[typeIndex]);

            //System.err.println("value = " + value);

            switch (typeIndex) {
                case ByteValue:
                    result = new Byte((byte)(-value.byteValue()));
                    break;
                case ShortValue:
                    result = new Short((short)(-value.shortValue()));
                    break;
                case IntValue:
                    result = new Integer(-value.intValue());
                    break;
                case FloatValue:
                    result = new Float(-value.floatValue());
                    break;
                case LongValue:
                    result = new Long(-value.longValue());
                    break;
                case DoubleValue:
                    result = new Double(-value.doubleValue());
                    break;
                default:
                    //Undefined value
                    break;
            }
        }
        return result;
    }

    public String toString() {
        String str = "null";

        if (getValue() != null) {
            str = value.toString();
        }
        return str;
    }

    public static void main(String[] args) {
        NumericValue num1 = new NumericValue(new Float(3.65));
        NumericValue num2 = new NumericValue(new Long(100L));
        System.err.println(num1 + "+" + num2 + " = " + num1.add(num2));
        System.err.println(num2 + "+" + num1 + " = " + num2.add(num1));

        System.err.println(num1 + "-" + num2 + " = " + num1.subtract(num2));
        System.err.println(num2 + "-" + num1 + " = " + num2.subtract(num1));

        System.err.println(num1 + "*" + num2 + " = " + num1.multiply(num2));
        System.err.println(num2 + "*" + num1 + " = " + num2.multiply(num1));

        System.err.println(num1 + "/" + num2 + " = " + num1.divide(num2));
        System.err.println(num2 + "/" + num1 + " = " + num2.divide(num1));

        System.err.println("-" + num1 + " = " + num1.negate());
        System.err.println("-" + num2 + " = " + num2.negate());


        System.exit(0);
    }


}
