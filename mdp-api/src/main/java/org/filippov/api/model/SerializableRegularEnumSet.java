package org.filippov.api.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.EnumSet;

public class SerializableRegularEnumSet<E extends Enum<E>> {
    @Getter
    private final EnumSet<E> enumSet;
    static private final Class<?> regularEnumSet;
    static private final Field elementsField;

    static {
        try {
            regularEnumSet = Class.forName("java.util.RegularEnumSet");
            elementsField = regularEnumSet.getDeclaredField("elements");
            elementsField.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonValue
    @Override
    public String toString() {
        try {
            long elements = elementsField.getLong(enumSet);
            return Long.toBinaryString(elements);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SerializableRegularEnumSet(String elements, Class<E> elementType) {
        try {
            if (elements.length() > 64) {
                throw new UnsupportedOperationException();
            }
            EnumSet<E> enumSet = EnumSet.noneOf(elementType);
            elementsField.setLong(enumSet, Long.parseLong(elements, 2));
            this.enumSet = enumSet;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public static abstract class Deserializer<T extends Enum<T>> extends StdDeserializer<SerializableRegularEnumSet<T>> {
        public Deserializer() {
            this(null);
        }

        protected Deserializer(Class<?> vc) {
            super(vc);
        }

        abstract public Class<T> getElementType();

        @Override
        public SerializableRegularEnumSet<T> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            String elements = jp.getText();
            return new SerializableRegularEnumSet<>(elements, getElementType());
        }
    }
}
