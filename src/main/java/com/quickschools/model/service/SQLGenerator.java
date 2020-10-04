package com.quickschools.model.service;

import com.quickschools.model.Entity;
import com.quickschools.model.Field;
import com.quickschools.model.Join;

import java.util.*;

public class SQLGenerator {


    public SQLGenerator() {
    }

    public String generate(List<Field<? extends Entity>> fields, List<Join<?, ?>> joins) {

        Map<Entity, Set<String>> tableColumnsMap = groupColumnsByEntity(fields);

        Set<Join<?, ?>> joinSet = removeDuplicateJoins(joins);
        if (joins.isEmpty()) return generateSqlWithSingleTable(tableColumnsMap);

        Set<String> tables = new HashSet<>();
        Set<String> columns = new HashSet<>();
        Set<String> conditions = new HashSet<>();

        Set<Entity> visitedEntitySet = new HashSet<>();

        for (Join<?, ?> join : joinSet) {
            Field<?> primaryKeyFirst = join.getPrimaryKeyFirst();
            Field<?> primaryKeySecond = join.getPrimaryKeySecond();

            tables.add(primaryKeyFirst.getEntity().toString());
            tables.add(primaryKeySecond.getEntity().toString());

            columns.addAll(getColumnNames(tableColumnsMap, primaryKeyFirst));
            columns.addAll(getColumnNames(tableColumnsMap, primaryKeySecond));

            conditions.add(join.getCondition());

            visitedEntitySet.add(primaryKeyFirst.getEntity());
            visitedEntitySet.add(primaryKeySecond.getEntity());

        }
        if (visitedEntitySet.size() != tableColumnsMap.size()) {
            throw new IllegalArgumentException("One or more join is missing to form SQL.");
        }
        return generateSqlWithMultipleTable(tables, columns, conditions);
    }

    private Map<Entity, Set<String>> groupColumnsByEntity(List<Field<? extends Entity>> fields) {
        Map<Entity, Set<String>> tableColumnsMap = new HashMap<>();
        if (fields == null || fields.isEmpty())
            throw new IllegalArgumentException("One or more fields required.");
        for (Field<? extends Entity> field : fields) {
            Objects.requireNonNull(field, "Field cannot be null.");
            field.getValue();
            Set<String> names = tableColumnsMap.computeIfAbsent(field.getEntity(), k -> new HashSet<>());
            names.add(field.toString());
        }
        return tableColumnsMap;
    }

    private Set<String> getColumnNames(Map<Entity, Set<String>> tableColumnsMap, Field<? extends Entity> field) {
        Set<String> columnNames = tableColumnsMap.get(field.getEntity());
        if (columnNames == null)
            throw new IllegalArgumentException(String.format("Invalid field %s in join.", field));
        return columnNames;
    }


    private String generateSqlWithSingleTable(Map<Entity, Set<String>> tableColumnsMap) {
        if (tableColumnsMap.size() > 1)
            throw new IllegalArgumentException("One or more join is missing to form SQL.");
        Map.Entry<Entity, Set<String>> entry = tableColumnsMap.entrySet().iterator().next();
        return "SELECT " + String.join(", ", entry.getValue()) + " FROM " + entry.getKey() + ";";
    }

    private Set<Join<?, ?>> removeDuplicateJoins(List<Join<?, ?>> joins) {
        if (joins == null)
            throw new IllegalArgumentException("Parameter joins cannot be null.");
        return new HashSet<>(joins);
    }

    private String generateSqlWithMultipleTable(Set<String> tables, Set<String> columns, Set<String> conditions) {
        return "SELECT " +
                String.join(", ", columns) +
                " FROM " +
                String.join(", ", tables) +
                " WHERE " +
                String.join(" AND ", conditions) +
                ";";
    }
}
