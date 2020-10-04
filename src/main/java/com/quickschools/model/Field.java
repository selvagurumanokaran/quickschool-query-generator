package com.quickschools.model;

import java.util.Objects;

public class Field<E extends Entity> {
    private E entity;
    private String name;

    public Field(E entity, String name) {
        this.entity = entity;
        this.name = name;
    }

    public Object getValue() throws IllegalArgumentException {
        Class<? extends Entity> entityClass = this.entity.getClass();
        try {
            java.lang.reflect.Field field = entityClass.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(this.entity);
        } catch (NoSuchFieldException iae) {
            throw new IllegalArgumentException("Field " + this.name + " does not exist in entity " + this.entity);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid field " + this.name);
        }
    }


    public E getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field<?> field = (Field<?>) o;

        if (!Objects.equals(entity, field.entity)) return false;
        return Objects.equals(name, field.name);
    }

    @Override
    public int hashCode() {
        int result = entity != null ? entity.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return entity + "." + name;
    }
}
