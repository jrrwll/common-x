package org.dreamcat.common.spring.features.repository;

/**
 * Create by tuke on 2021/2/8
 */
public interface RepositoryAfterReturningAdvice {

    void insert(Object... args);

    void update(Object... args);

    void select(Object... args);

    void delete(Object... args);
}
