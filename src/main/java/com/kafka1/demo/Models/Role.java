package com.kafka1.demo.Models;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Getter
public enum Role {
    UNVERIFIED(Set.of(Permission.UNVERIFIED)),
    USER(Set.of(Permission.USER)),
    RE_SET_PASSWORD(Set.of(Permission.RE_SET_PASSWORD)),
    ADMIN(Set.of(Permission.USER, Permission.ADMIN,Permission.DOCTOR)),
    DOCTOR(Set.of(Permission.USER, Permission.DOCTOR));

    private final Set<Permission> permissions;
    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (Permission permission : getPermissions()) {
            authorities.add(new SimpleGrantedAuthority(permission.getPermission()));
        }
        return authorities;
    }
}
