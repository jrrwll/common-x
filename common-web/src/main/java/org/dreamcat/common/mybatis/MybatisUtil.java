package org.dreamcat.common.mybatis;

import java.io.StringReader;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.OgnlCache;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.SimpleTypeRegistry;
import org.dreamcat.common.util.ReflectUtil;
import org.dreamcat.common.util.StringUtil;

/**
 * @author Jerry Will
 * @version 2021-10-12
 */
public class MybatisUtil {

    private static final String template = "<!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\"><configuration><mappers><mapper resource=\"%s\"/></mappers></configuration>";

    public static String getSql(String mapperPath, String mapperCanonicalName, Object bean) {
        XMLConfigBuilder parser = new XMLConfigBuilder(
                new StringReader(String.format(template, mapperPath)), null, null);
        Configuration configuration = parser.parse();

        MappedStatement mappedStatement = configuration.getMappedStatement(mapperCanonicalName);
        BoundSql boundSql = mappedStatement.getBoundSql(bean);
        return boundSql.getSql();
    }

    @SneakyThrows
    public static String getFinalSql(Configuration configuration, String mapperCanonicalName, Object bean) {
        MappedStatement mappedStatement = configuration.getMappedStatement(mapperCanonicalName);
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
