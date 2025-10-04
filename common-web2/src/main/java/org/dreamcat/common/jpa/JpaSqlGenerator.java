package org.dreamcat.common.jpa;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.dreamcat.common.util.ReflectUtil;
import org.dreamcat.common.util.StringUtil;

/**
 * @author Jerry Will
 * @version 2021-10-12
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JpaSqlGenerator {

    private boolean useCamel; // default use snake
    private boolean nullable; // default not null
    private boolean storeEnumInInt; // default store enum in string
    @Builder.Default
    private String enumIntType = "smallint"; // only effect if storeEnumInInt is true
    @Builder.Default
    private int enumStringLength = 64; // only effect if storeEnumInInt is false
    private boolean useBit; // default boolean -> tinyint
    private boolean useDatetime; // default date -> timestamp
    @Builder.Default
    private int length = 255; // varchar255()
    @Builder.Default
    private int precision = 10; // decimal(10, 2)
    @Builder.Default
    private int scale = 2;
    @Builder.Default
    private String tableParam = "engine = innodb default charset = utf8mb4;"; // prefer mysql innodb

    public String generate(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            throw new IllegalArgumentException("@javax.persistence.Table is missing on " + clazz);
        }
        String tableName = table.name();
        if (tableName.isEmpty()) {
            tableName = StringUtil.toSnakeCase(clazz.getSimpleName());
        }

        List<Field> fields = ReflectUtil.retrieveBeanFields(clazz);
        StringBuilder s = new StringBuilder();
        s.append("create table `").append(tableName).append("` (\n");
        String primaryKey = null;
        List<String> embeddedFields = new ArrayList<>();
        List<String> uniqueIndexedColumns = new ArrayList<>();
        for (Field field : fields) {
            if (field.getAnnotation(EmbeddedId.class) != null) {
                fillEmbeddableClass(field, s, embeddedFields);
                continue;
            }
            String columnName = getColumnName(field);

            Column column = field.getAnnotation(Column.class);
            if (column != null && column.unique()) {
                uniqueIndexedColumns.add(columnName);
            }
            String columnType = getColumnType(field);

            s.append("    `").append(columnName).append("` ").append(columnType);

            if ((column != null && !column.nullable()) || !nullable) {
                String notNullDefault = getDefault(columnType);
                if (notNullDefault != null) {
                    s.append(" not null default ").append(notNullDefault);
                }
            }

            GeneratedValue generatedValue;
            if ((generatedValue = field.getAnnotation(GeneratedValue.class)) != null &&
                    generatedValue.strategy() == GenerationType.IDENTITY) {
                s.append(" auto_increment");
            }
            if (field.getAnnotation(Id.class) != null) {
                primaryKey = columnName;
            }
            s.append(",\n");
        }

        if (!embeddedFields.isEmpty()) {
            s.append("    primary key(`")
                    .append(String.join("`, `", embeddedFields))
                    .append("`)");
        } else if (primaryKey != null) {
            s.append("    primary key(`").append(primaryKey).append("`)");
        } else {
            throw new IllegalArgumentException("primary key is missing");
        }
        if (!uniqueIndexedColumns.isEmpty()) {
            s.append(",\n    ");
            fillIndexes("", true, uniqueIndexedColumns, s);
        }
        UniqueConstraint[] uniqueConstraints = table.uniqueConstraints();
        if (uniqueConstraints.length > 0) {
            for (UniqueConstraint uniqueConstraint : uniqueConstraints) {
                s.append(",\n    ");
                fillIndexes(uniqueConstraint.name(), true,
                        Arrays.asList(uniqueConstraint.columnNames()), s);
            }
        }
        Index[] indexes = table.indexes();
        if (indexes.length > 0) {
            for (Index index : indexes) {
                s.append(",\n    ");
                fillIndexes(index.name(), index.unique(),
                        Arrays.asList(index.columnList().split(",")), s);
            }
        }
        s.append("\n) ").append(tableParam);
        return s.toString();
    }

    private void fillIndexes(String name, boolean unique, List<String> columnNames, StringBuilder s) {
        if (unique) {
            s.append("unique ");
        }
        s.append("index `");
        if (!name.isEmpty()) {
            s.append(name);
        } else {
            s.append(unique ? "uk" : "ix");
            for (String columnName : columnNames) {
                s.append('_').append(columnName);
            }
        }
        s.append("`(`").append(columnNames.get(0)).append('`');
        int size = columnNames.size();
        for (int i = 1; i < size; i++) {
            s.append(", `").append(columnNames.get(i)).append('`');
        }
        s.append(')');
    }

    private String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null) {
            return column.name();
        } else if (useCamel) {
            return field.getName();
        } else {
            return StringUtil.toSnakeCase(field.getName());
        }
    }

    /**
     * java type to db type
     *
     * @param field entity field
     * @return db type
     * @see Column#length()
     */
    private String getColumnType(Field field) {
        Class<?> fieldType = field.getType();
        Column column = field.getAnnotation(Column.class);

        int curLength = length;
        int curPrecision = precision;
        int curScale = scale;
        String columnDefinition;
        if (column != null && !(columnDefinition = column.columnDefinition()).isEmpty()) {
            return columnDefinition;
        }

        if (fieldType == Long.class) {
            return "bigint";
        } else if (fieldType == Integer.class) {
            return "int";
        } else if (fieldType == Short.class) {
            return "smallint";
        } else if (fieldType == Byte.class) {
            return "tinyint";
        } else if (fieldType == String.class) {
            if (column != null) {
                curLength = column.length();
            }
            return "varchar(" + curLength + ")";
        } else if (fieldType == Boolean.class) {
            return useBit ? "bit(1)" : "tinyint";
        } else if (Date.class.isAssignableFrom(fieldType)) {
            return useDatetime ? "datetime" : "timestamp";
        } else if (fieldType == BigDecimal.class) {
            if (column != null) {
                curPrecision = column.precision();
                if (curPrecision == 0) curPrecision = precision;
                curScale = column.scale();
                if (curScale == 0) curScale = scale;
            }
            return "decimal(" + curPrecision + ", " + curScale + ")";
        } else if (fieldType == BigInteger.class) {
            if (column != null) {
                curPrecision = column.precision();
                if (curPrecision == 0) curPrecision = precision;
                curScale = column.scale();
            }
            return "decimal(" + curPrecision + ", " + curScale + ")";
        } else if (Enum.class.isAssignableFrom(fieldType)) {
            if (storeEnumInInt) {
                return enumIntType;
            } else {
                return "varchar(" + enumStringLength + ")";
            }
        }
        return "unknown";
    }

    private String getDefault(String columnType) {
        if (columnType.endsWith("int")) {
            return "0";
        } else if (columnType.startsWith("decimal")) {
            return "0";
        } else if (columnType.startsWith("varchar")) {
            return "''";
        } else if (columnType.startsWith("bit")) {
            return "bit('0')";
        } else {
            return null;
        }
    }

    private void fillEmbeddableClass(Field field, StringBuilder s, List<String> embeddedFields) {
        Class<?> fieldType = field.getType();
        if (fieldType.getAnnotation(Embeddable.class) == null) {
            throw new IllegalArgumentException(
                    "@javax.persistence.Embeddable is required since " + field + " has @javax.persistence.EmbeddedId");
        }
        List<Field> fieldFields = ReflectUtil.retrieveBeanFields(fieldType);
        for (Field fieldField : fieldFields) {
            if (fieldField.getAnnotation(Embedded.class) != null) {
                fillEmbeddableClass(fieldField, s, embeddedFields);
                continue;
            }

            String columnName = getColumnName(fieldField);
            embeddedFields.add(columnName);
            String columnType = getColumnType(fieldField);
            s.append("    `").append(columnName).append("` ").append(columnType).append(",\n");
        }
    }

}
