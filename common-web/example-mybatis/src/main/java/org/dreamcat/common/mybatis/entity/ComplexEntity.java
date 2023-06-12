package org.dreamcat.common.mybatis.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.dreamcat.common.mybatis.JsonBatisTypeHandler;
import org.dreamcat.common.mybatis.JsonListBatisTypeHandler;
import org.springframework.stereotype.Component;

/**
 * Create by tuke on 2020/7/8
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ComplexEntity extends BaseEntity {

    private String name;
    private List<Tag> tags;
    private User user;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class User {

        private String firstName;
        private String lastName;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Tag {

        private String name;
    }

    /**
     * Create by tuke on 2020/7/8
     */
    @Component
    public static class TagsTypeHandler extends JsonListBatisTypeHandler<Tag> {

        public TagsTypeHandler() {
            super(Tag.class);
        }
    }

    /**
     * Create by tuke on 2020/7/8
     */
    @Component
    public static class UserTypeHandler extends JsonBatisTypeHandler<User> {

        public UserTypeHandler() {
            super(User.class);
        }
    }
}
