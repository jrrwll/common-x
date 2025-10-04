package org.dreamcat.common.jpa;

import javax.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;
import org.dreamcat.common.json.JsonUtil;

/**
 * Create by tuke on 2020/9/17
 */
@RequiredArgsConstructor
public class JsonAttributeConverter<T> implements AttributeConverter<T, String> {

    private final Class<T> clazz;

    @Override
    public String convertToDatabaseColumn(T attribute) {
        return JsonUtil.toJson(attribute);
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        return JsonUtil.fromJson(dbData, clazz);
    }
}
