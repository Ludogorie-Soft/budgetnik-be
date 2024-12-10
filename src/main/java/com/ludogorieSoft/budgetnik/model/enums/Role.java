package com.ludogorieSoft.budgetnik.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ludogorieSoft.budgetnik.model.enums.Permission.ADMIN_CREATE;
import static com.ludogorieSoft.budgetnik.model.enums.Permission.ADMIN_DELETE;
import static com.ludogorieSoft.budgetnik.model.enums.Permission.ADMIN_READ;
import static com.ludogorieSoft.budgetnik.model.enums.Permission.ADMIN_UPDATE;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER(Collections.emptySet()),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE
            )
    );

    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;

    }
}
