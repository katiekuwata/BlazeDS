/*
 * @(#)PropertyValueComparator.java 1.4 00/11/16
 *
 * Copyright 1999-2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of Sun Microsystems, Inc.
 * Use is subject to license terms.
 *
 */
package flex.messaging.services.messaging.selector;

import java.util.Comparator;

/**
 * Compares 2 objects; determines relative order.
 *
 * Used by JMSSelector class.
 *
 * This class implements the Singleton design pattern
 * Related classes include <tt>com.sun.jms.service.selector.JMSSelector</tt>,
 * <tt>com.sun.jms.MessageImpl</tt>, and <tt>javax.jms.Message</tt>.
 *
 * @exclude
 */
public class PropertyValueComparator implements Comparator {
    static PropertyValueComparator  instance = null;
    public static final int UNKNOWN = -100;

    public static PropertyValueComparator getInstance() {
        if (instance == null) {
            instance = new PropertyValueComparator();
        }
        return instance;
    }

    public int compare(Object o1, Object o2) throws ClassCastException {
        int result = 0;

        if (o1 instanceof NumericValue) {
            o1 = ((NumericValue)o1).getValue();
        }
        if (o2 instanceof NumericValue) {
            o2 = ((NumericValue)o2).getValue();
        }
        try {
            if ((o1 == null) || (o2 == null)) {
                result = UNKNOWN;
            }
            else if (o1 instanceof Boolean) {
                if (o2 instanceof Boolean) {
                    if (!o1.equals(o2)) {
                        result = -1;
                    } else {
                        result = 0;
                    }
                } else {
                    throw new ClassCastException();
                }
            } else if (o1 instanceof Byte) {
                if (o2 instanceof Byte) {
                    result = ((Byte) o1).compareTo((Byte) o2);
                } else if (o2 instanceof Short) {
                    result = (new Short(((Byte) o1).shortValue())).compareTo((Short) o2);
                } else if (o2 instanceof Integer) {
                    result = (new Integer(((Byte) o1).intValue())).compareTo((Integer) o2);
                } else if (o2 instanceof Long) {
                    result = (new Long(((Byte) o1).longValue())).compareTo((Long) o2);
                } else if (o2 instanceof Float) {
                    result = (new Float(((Byte) o1).floatValue())).compareTo((Float) o2);
                } else if (o2 instanceof Double) {
                    result = (new Double(((Byte) o1).doubleValue())).compareTo((Double) o2);
                } else {
                    throw new ClassCastException();
                }
            } else if (o1 instanceof Short) {
                if (o2 instanceof Byte) {
                    result = ((Short) o1).compareTo(new Short(((Byte) o2).shortValue()));
                } else if (o2 instanceof Short) {
                    result = ((Short) o1).compareTo((Short) o2);
                } else if (o2 instanceof Integer) {
                    result = (new Integer(((Short) o1).intValue())).compareTo((Integer) o2);
                } else if (o2 instanceof Long) {
                    result = (new Long(((Short) o1).longValue())).compareTo((Long) o2);
                } else if (o2 instanceof Float) {
                    result = (new Float(((Short) o1).floatValue())).compareTo((Float) o2);
                } else if (o2 instanceof Double) {
                    result = (new Double(((Short) o1).doubleValue())).compareTo((Double) o2);
                } else {
                    throw new ClassCastException();
                }
            } else if (o1 instanceof Integer) {
                if (o2 instanceof Byte) {
                    result = ((Integer) o1).compareTo(new Integer(((Byte) o2).intValue()));
                } else if (o2 instanceof Short) {
                    result = ((Integer) o1).compareTo(new Integer(((Short) o2).intValue()));
                } else if (o2 instanceof Integer) {
                    result = ((Integer) o1).compareTo((Integer) o2);
                } else if (o2 instanceof Long) {
                    result = (new Long(((Integer) o1).longValue())).compareTo((Long) o2);
                } else if (o2 instanceof Float) {
                    result = (new Float(((Integer) o1).floatValue())).compareTo((Float) o2);
                } else if (o2 instanceof Double) {
                    result = (new Double(((Integer) o1).doubleValue())).compareTo((Double) o2);
                } else {
                    throw new ClassCastException();
                }
            } else if (o1 instanceof Long) {
                if (o2 instanceof Byte) {
                    result = ((Long) o1).compareTo(new Long(((Byte) o2).longValue()));
                } else if (o2 instanceof Short) {
                    result = ((Long) o1).compareTo(new Long(((Short) o2).longValue()));
                } else if (o2 instanceof Integer) {
                    result = ((Long) o1).compareTo(new Long(((Integer) o2).longValue()));
                } else if (o2 instanceof Long) {
                    result = ((Long) o1).compareTo((Long) o2);
                } else if (o2 instanceof Float) {
                    result = (new Float(((Long) o1).floatValue())).compareTo((Float) o2);
                } else if (o2 instanceof Double) {
                    result = (new Double(((Long) o1).doubleValue())).compareTo((Double) o2);
                } else {
                    throw new ClassCastException();
                }
            } else if (o1 instanceof Float) {
                if (o2 instanceof Byte) {
                    result = ((Float) o1).compareTo(new Float(((Byte) o2).floatValue()));
                } else if (o2 instanceof Short) {
                    result = ((Float) o1).compareTo(new Float(((Short) o2).floatValue()));
                } else if (o2 instanceof Integer) {
                    result = ((Float) o1).compareTo(new Float(((Integer) o2).floatValue()));
                } else if (o2 instanceof Long) {
                    result = ((Float) o1).compareTo(new Float(((Long) o2).floatValue()));
                } else if (o2 instanceof Float) {
                    result = ((Float) o1).compareTo((Float) o2);
                } else if (o2 instanceof Double) {
                    result = (new Double(((Float) o1).doubleValue())).compareTo((Double) o2);
                } else {
                    throw new ClassCastException();
                }
            } else if (o1 instanceof Double) {
                if (o2 instanceof Byte) {
                    result = ((Double) o1).compareTo(new Double(((Byte) o2).doubleValue()));
                } else if (o2 instanceof Short) {
                    result = ((Double) o1).compareTo(new Double(((Short) o2).doubleValue()));
                } else if (o2 instanceof Integer) {
                    result = ((Double) o1).compareTo(new Double(((Integer) o2).doubleValue()));
                } else if (o2 instanceof Long) {
                    result = ((Double) o1).compareTo(new Double(((Long) o2).doubleValue()));
                } else if (o2 instanceof Float) {
                    result = ((Double) o1).compareTo(new Double(((Float) o2).doubleValue()));
                } else if (o2 instanceof Double) {
                    result = ((Double) o1).compareTo((Double) o2);
                } else {
                    throw new ClassCastException();
                }
            } else if (o1 instanceof String) {
                result = ((String) o1).compareTo(o2.toString());
            } else {
                throw new ClassCastException();
            }
        } catch ( /* NumberFormat */Exception e) {
            throw new ClassCastException();
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (obj instanceof PropertyValueComparator) {
            return true;
        } else {
            return false;
        }
    }

}
