package org.dreamcat.common.web.mybatis;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.OgnlCache;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.SimpleTypeRegistry;
import org.dreamcat.common.Pair;
import org.dreamcat.common.util.DateUtil;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.util.ReflectUtil;
import org.dreamcat.common.util.StringUtil;

/**
 * @author Jerry Will
 * @version 2021-10-12
 */
public class MybatisUtil {

    private static final String template = "<!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\"><configuration><mappers><mapper resource=\"%s\"/></mappers></configuration>";

    @SneakyThrows
    public static Pair<PreparedStatement, String> getPreparedStatementAndFinalSql(
            Connection connection, Configuration configuration, String mappedStatementId, Object bean) {
        MappedStatement mappedStatement = configuration.getMappedStatement(mappedStatementId);
        if (mappedStatement == null) {
            return null;
        }
        BoundSql boundSql = mappedStatement.getBoundSql(bean);
        String sql = boundSql.getSql();

        PreparedStatement statement = connection.prepareStatement(sql);
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, bean, boundSql);
        parameterHandler.setParameters(statement);

        List<Object> parameterValues = getParameterValues(configuration, boundSql);
        String finalSql = getFinalSql(sql, parameterValues);
        return Pair.of(statement, finalSql);
    }

    private static List<Object> getParameterValues(Configuration configuration, BoundSql boundSql) {
        List<Object> paramValues = new ArrayList<>();

        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (ObjectUtil.isEmpty(parameterMappings)) {
            return paramValues;
        }

        MetaObject metaObject = null;
        if (parameterObject != null && !(parameterObject instanceof Map)) {
            metaObject = configuration.newMetaObject(parameterObject);
        }

        for (ParameterMapping parameterMapping : parameterMappings) {
            if (parameterMapping.getMode() == ParameterMode.OUT) {
                continue;
            }

            String propertyName = parameterMapping.getProperty();

            Object value;
            // 1. extra param, such as: <bind>
            if (boundSql.hasAdditionalParameter(propertyName)) {
                value = boundSql.getAdditionalParameter(propertyName);
            }
            // 2. map
            else if (parameterObject instanceof Map) {
                value = ((Map<?, ?>) parameterObject).get(propertyName);
            }
            // 3. bean
            else if (metaObject != null) {
                value = metaObject.getValue(propertyName);
            }
            // 4. single param
            else {
                value = parameterObject;
            }

            if (value != null && ReflectUtil.isCollectionOrArray(value.getClass())) {
                paramValues.addAll(ReflectUtil.castAsCollection(value));
            } else {
                paramValues.add(value);
            }
        }
        return paramValues;
    }

    private static String getFinalSql(String sqlTemplate, List<Object> parameterValues) {
        String sql = sqlTemplate;
        for (Object parameterValue : parameterValues) {
            String value;
            if (parameterValue == null) {
                value = "null";
            } else if (parameterValue instanceof Number) {
                value = parameterValue.toString();
            } else {
                String literal;
                if (parameterValue instanceof Date) {
                    literal = DateUtil.format((Date)parameterValue);
                } else if (parameterValue instanceof LocalDateTime) {
                    literal = DateUtil.format((LocalDateTime)parameterValue);
                } else if (parameterValue instanceof LocalDate) {
                    literal = DateUtil.formatDate((LocalDate)parameterValue);
                } else {
                    literal = StringUtil.escape(parameterValue.toString(), "'");
                }
                value = "'" + literal + "'";
            }
            sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(value));
        }
        return sql;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static String getSql(String mapperPath, String mappedStatementId, Object bean) {
        XMLConfigBuilder parser = new XMLConfigBuilder(
                new StringReader(String.format(template, mapperPath)), null, null);
        Configuration configuration = parser.parse();

        MappedStatement mappedStatement = configuration.getMappedStatement(mappedStatementId);
        BoundSql boundSql = mappedStatement.getBoundSql(bean);
        return boundSql.getSql();
    }

    @SneakyThrows
    public static String getFinalSql(Configuration configuration, String mappedStatementId, Object bean) {
        MappedStatement mappedStatement = configuration.getMappedStatement(mappedStatementId);
        if (mappedStatement == null) {
            return null;
        }
        DynamicSqlSource sqlSource = (DynamicSqlSource) mappedStatement.getSqlSource();
        SqlNode rootSqlNode = (SqlNode) ReflectUtil.getFieldValue(
                sqlSource, "rootSqlNode");

        DynamicContext context = new DynamicContext(configuration, bean);
        rootSqlNode.apply(context);
        String originalSql =  context.getSql();

        LiteralTokenHandler handler = new LiteralTokenHandler(context);
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        return parser.parse(originalSql);
    }

    /**
     * @see org.apache.ibatis.scripting.xmltags.TextSqlNode#apply(DynamicContext) for ${}
     * @see org.apache.ibatis.builder.SqlSourceBuilder#parse(String, Class, Map) for #{}
     */
    @RequiredArgsConstructor
    private static class LiteralTokenHandler implements TokenHandler {

        private final DynamicContext context;

        @Override
        public String handleToken(String content) {
            Object parameter = context.getBindings().get("_parameter");
            if (parameter == null) {
                context.getBindings().put("value", null);
            } else if (SimpleTypeRegistry.isSimpleType(parameter.getClass())) {
                context.getBindings().put("value", parameter);
            }
            Object value = OgnlCache.getValue(content, context.getBindings());
            if (value == null) return "";
            if (value instanceof Number) {
                return value.toString();
            }
            return "'" + StringUtil.escape(value.toString(), "'\\") + "'";
        }
    }
}
