package uk.co.whitbread.hotel.card.utils;

import com.github.javafaker.Faker;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class FakeDataGenerator {

    private static final Faker faker = new Faker();

    public static <T> T createFakeData(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Class<?> currentClass = clazz;
            while (currentClass != null) {
                for (Field field : currentClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.getType().equals(String.class)) {
                        field.set(instance, faker.lorem().word());
                    } else if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                        field.set(instance, faker.number().randomDigit());
                    } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                        field.set(instance, faker.number().randomNumber());
                    } else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                        field.set(instance, faker.bool().bool());
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        ParameterizedType listType = (ParameterizedType) field.getGenericType();
                        Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                        field.set(instance, List.of(createFakeData(listClass)));
                    } else {
                        field.set(instance, createFakeData(field.getType()));
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create fake data", e);
        }
    }
}