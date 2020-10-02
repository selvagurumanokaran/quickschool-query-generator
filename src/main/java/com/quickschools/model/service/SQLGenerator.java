package com.quickschools.model.service;

import com.quickschools.model.Entity;
import com.quickschools.model.Field;
import com.quickschools.model.Join;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SQLGenerator {
    private static Set<String> expandField(Field<? extends Entity> field) {
        Set<String> set = new HashSet<>();
        set.add(field.getEntity() + "." + field.getName());
        return set;
    }

    private static Set<String> mergeSet(Set<String> list1, Set<String> list2) {
        return Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toSet());
    }

    public String generate(List<Field<? extends Entity>> fields, List<Join<?, ?>> joins) {
        Objects.requireNonNull(fields);
        if (fields.isEmpty())
            throw new IllegalArgumentException("The fields can not be empty.");
        Objects.requireNonNull(joins);
        Set<Join<?, ?>> joinSet = new HashSet<>(joins);
        List<String> fieldNames = new ArrayList<>();
        List<String> tables = new ArrayList<>();
        List<String> conditions = new ArrayList<>();
        Map<Entity, Set<String>> result = new HashMap<>();
        for (Field<? extends Entity> field : fields) {
            Objects.requireNonNull(field, "Field cannot be null.");
            field.getValue();
            result.merge(field.getEntity(), expandField(field), SQLGenerator::mergeSet);
        }
        if (joins.isEmpty()) {
            if (result.size() > 1)
                throw new IllegalArgumentException("One or more join is missing to form SQL.");
            Map.Entry<Entity, Set<String>> entry = result.entrySet().iterator().next();
            return "SELECT " + String.join(", ", entry.getValue()) + " FROM " + entry.getKey() + ";";
        }
        for (Join<?, ?> join : joinSet) {
            Field<? extends Entity> primaryKeyFirst = Objects.requireNonNull(
                    join.getPrimaryKeyFirst(), "One of the field is null in Join.");
            Field<? extends Entity> primaryKeySecond = Objects.requireNonNull(
                    join.getPrimaryKeySecond(), "One of the field is null in Join.");

            Entity primaryKeyFirstEntity = primaryKeyFirst.getEntity();
            Set<String> firstTableFieldNames = result.remove(primaryKeyFirstEntity);
            String primaryKeyFirstName = primaryKeyFirst.getName();
            if (firstTableFieldNames == null) {
                throw new IllegalArgumentException(
                        String.format("The key %s in Join %s doesn't represent any entity.", primaryKeyFirstName, join)
                );
            }
            Entity primaryKeySecondEntity = primaryKeySecond.getEntity();
            Set<String> secondTableFieldNames = result.remove(primaryKeySecondEntity);
            String primaryKeySecondName = primaryKeySecond.getName();
            if (secondTableFieldNames == null) {
                throw new IllegalArgumentException(
                        String.format("The key %s in Join %s doesn't represent any entity.", primaryKeySecondName, join)
                );
            }
            conditions.add(primaryKeyFirstEntity.toString() +
                    "." + primaryKeyFirstName + " = " + primaryKeySecondEntity.toString() +
                    "." + primaryKeySecondName);
            fieldNames.addAll(firstTableFieldNames);
            fieldNames.addAll(secondTableFieldNames);
            tables.add(primaryKeyFirstEntity.toString());
            tables.add(primaryKeySecondEntity.toString());
        }
        if (result.size() > 0) {
            throw new IllegalArgumentException("One or more join is missing to form SQL.");
        }
        return "SELECT " +
                String.join(", ", fieldNames) +
                " FROM " +
                String.join(", ", tables) +
                " WHERE " +
                String.join(" AND ", conditions) +
                ";";
    }
}
