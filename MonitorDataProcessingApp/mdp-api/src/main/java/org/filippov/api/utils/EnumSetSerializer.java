package org.filippov.api.utils;

import java.lang.reflect.Field;
import java.util.EnumSet;

public class EnumSetSerializer {
//    static private final Class<?> regularEnumSet;
//    static private final Field elementsField;
//
//    static {
//        try {
//            regularEnumSet = Class.forName("java.util.RegularEnumSet");
//            elementsField = regularEnumSet.getDeclaredField("elements");
//            elementsField.setAccessible(true);
//        } catch (ClassNotFoundException | NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static <E extends Enum<E>> String toString(EnumSet<E> enumSet) {
//        try {
//            if (regularEnumSet.isInstance(enumSet)) {
//                throw new UnsupportedOperationException();
//            }
//            long elements = elementsField.getLong(enumSet);
//            return Long.toBinaryString(elements);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static <E extends Enum<E>> EnumSet<E> fromString(String elements, Class<E> elementType) {
//        try {
//            if (elements.length() > 64) {
//                throw new UnsupportedOperationException();
//            }
//            EnumSet<E> enumSet = EnumSet.noneOf(elementType);
//            elementsField.setLong(enumSet, Long.parseLong(elements, 2));
//            return enumSet;
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
