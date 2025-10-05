package org.dreamcat.common.web.spring.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreamcat.common.crypto.Jwt;
import org.dreamcat.common.util.ObjectUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Create by tuke on 2020/3/5
 */
@Getter
@RequiredArgsConstructor
public class JwtFactory {

    public static final String TOKEN_TYPE = "Bearer";
    public static final String TOKEN_PREFIX = TOKEN_TYPE + " ";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_COOKIE_NAME = "Bearer";
    public static final String TOKEN_ATTRIBUTE = JwtFactory.class.getPackage().getName();

    protected final JwtProperties jwtProperties;

    public String generateToken(String subject, String... permissions) {
        if (ObjectUtil.isEmpty(permissions)) return generateToken(subject, null, null);
        return generateToken(subject, Arrays.asList(permissions));
    }

    public String generateToken(String subject, Collection<String> permissions) {
        if (ObjectUtil.isEmpty(permissions)) permissions = null;
        return generateToken(subject, permissions, null);
    }

    public String generateToken(String subject, Collection<String> permissions,
            Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwt.builder()
                .subject(subject)
                .addPermissions(permissions)
                .addClaims(claims)
                .issuedAt(now)
                .ttlMs(jwtProperties.getTtlMs())
                .signWith(jwtProperties.getSecretKey());
    }

    public String regenerateToken(String token) {
        Jwt jwt = parse(token);
        if (jwt == null) {
            throw new IllegalArgumentException("Invalid JWT token: " + token);
        }

        long now = System.currentTimeMillis();
        jwt.setIssuedAt(now);
        jwt.setExpiredAt(now + jwtProperties.getTtlMs());
        return jwt.encode(jwtProperties.getSecretKey());
    }

    public Jwt parse(String token) {
        return Jwt.decode(token, jwtProperties.getSecretKey());
    }

    public JwtBuilder newJwt() {
        return new JwtBuilder(this.jwtProperties);
    }

    public static class JwtBuilder {

        private final JwtProperties jwtProperties;
        private final Jwt jwt;
        private Long maxAge;

        private JwtBuilder(JwtProperties jwtProperties) {
            long now = System.currentTimeMillis();
            this.jwt = new Jwt();
            jwt.setIssuedAt(now);
            jwt.setExpiredAt(Long.MAX_VALUE); // never expired
            this.jwtProperties = jwtProperties;
        }

        public JwtBuilder subject(String subject) {
            jwt.setSubject(subject);
            return this;
        }

        public JwtBuilder maxAge(long maxAgeInSecond) {
            this.maxAge = maxAgeInSecond;
            return this;
        }

        public String signAndCompact() {
            // since I already set expiredAt
            if (maxAge != null) {
                jwt.setExpiredAt(jwt.getIssuedAt() + maxAge * 1000);
            }
            return jwt.encode(jwtProperties.getSecretKey());
        }

        public JwtBuilder issuedAt(long issuedAt) {
            jwt.setIssuedAt(issuedAt);
            return this;
        }

        public JwtBuilder issuedAt(Date issuedAt) {
            jwt.setIssuedAt(issuedAt.getTime());
            return this;
        }

        public JwtBuilder expiredAt(long expiredAt) {
            jwt.setExpiredAt(expiredAt);
            return this;
        }

        public JwtBuilder expiredAt(Date expiredAt) {
            jwt.setExpiredAt(expiredAt.getTime());
            return this;
        }

        public JwtBuilder permission(String permission) {
            Set<String> permissions = jwt.getPermissions();
            if (permissions == null) {
                permissions = new HashSet<>();
                jwt.setPermissions(permissions);
            }
            permissions.add(permission);
            return this;
        }

        public JwtBuilder addPermissions(Collection<String> permissions) {
            Set<String> perms = jwt.getPermissions();
            if (perms == null) {
                perms = new HashSet<>();
                jwt.setPermissions(perms);
            }
            perms.addAll(permissions);
            return this;
        }

        public JwtBuilder claim(String name, Object value) {
            Map<String, Object> claims = jwt.getClaims();
            if (claims == null) {
                claims = new HashMap<>();
                jwt.setClaims(claims);
            }
            claims.put(name, value);
            return this;
        }

        public JwtBuilder addClaims(Map<String, Object> claims) {
            Map<String, Object> m = jwt.getClaims();
            if (m == null) {
                m = new HashMap<>();
                jwt.setClaims(m);
            }
            m.putAll(claims);
            return this;
        }
    }
}
