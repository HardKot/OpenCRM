package com.open.crm.admin.entities.user;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.open.crm.admin.entities.common.BaseAdminEntity;
import com.open.crm.admin.entities.tenant.Tenant;
import com.open.crm.core.application.services.ClientInfoCleaner;

import groovyjarjarantlr4.v4.parse.ANTLRParser.optionValue_return;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", schema = "public")
@NoArgsConstructor
@Getter
@Setter
public class User extends BaseAdminEntity implements UserDetails {

    @Column(nullable = false, unique = true)
    private String email = "";

    @Column(nullable = false)
    private String password = "";

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private Tenant tenant;

    @Column(nullable = true, name = "entity_name")
    @Enumerated(EnumType.STRING)
    private UserEntity entityName;

    @Column(nullable = true, name = "entity_id")
    private long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.ROLE_EMPLOYEE;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_permissions", schema = "public", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    private Set<UserPermission> permissions = Set.of();

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = true;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isEmployeeUser() {
        return entityName.equals(UserEntity.EMPLOYEE);
    }

    public UserPermission[] getPermissions() {
        if (role.equals(UserRole.ROLE_OWNER)) {
            return UserPermission.values();
        }

        if (role.equals(UserRole.ROLE_ADMIN)) {
            return UserPermission.values();
        }

        return permissions.toArray(new UserPermission[0]);
    }

    public boolean hasPermission(UserPermission permission) {
        if (role.equals(UserRole.ROLE_OWNER)) {
            return true;
        }

        if (role.equals(UserRole.ROLE_ADMIN)) {
            return true;
        }

        return permissions.contains(permission);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(it -> new SimpleGrantedAuthority(it.name()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }

}
