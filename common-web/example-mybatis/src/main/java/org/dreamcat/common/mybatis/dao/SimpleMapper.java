package org.dreamcat.common.mybatis.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dreamcat.common.mybatis.entity.SimpleEntity;
import org.dreamcat.common.spring.features.repository.RepositoryAfterReturning;
import org.dreamcat.common.spring.features.repository.RepositoryAfterReturning.Type;

/**
 * Create by tuke on 2020/7/17
 */
@Mapper
public interface SimpleMapper {

    @RepositoryAfterReturning(type = Type.SELECT, args = {
            "'simple'", "#id", "#result.tenantId"
    })
    SimpleEntity select(@Param("id") Long id);

    @RepositoryAfterReturning(type = Type.INSERT, args = {
            "'simple'", "#entity.id", "#tenantId"
    })
    int insert(@Param("entity") SimpleEntity entity, @Param("tenantId") String tenantId);

}
