package org.dreamcat.common.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.dreamcat.common.json.JsonUtil;

/**
 * Create by tuke on 2020/7/8
 */
@RequiredArgsConstructor
public class JsonBatisTypeHandler<T> extends BaseTypeHandler<T> {

    private final Class<T> clazz;

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        String json = JsonUtil.toJson(parameter);
        ps.setString(i, json);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        if (rs.wasNull()) {
            return null;
        }
        String json = rs.getString(columnName);
        return JsonUtil.fromJson(json, clazz);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        if (rs.wasNull()) {
            return null;
        }
        String json = rs.getString(columnIndex);
        return JsonUtil.fromJson(json, clazz);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (cs.wasNull()) {
            return null;
        }
        String json = cs.getString(columnIndex);
        return JsonUtil.fromJson(json, clazz);
    }
}
