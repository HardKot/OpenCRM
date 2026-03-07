package com.open.crm.admin.entities.user;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.open.crm.admin.entities.common.BaseAdminEntity;
import com.open.crm.admin.entities.tenant.Tenant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    private String username = "";

    @Column(nullable = false)
    private String password = "";

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Tenant tenant;

    @Column(nullable = false, name = "employee_id")
    private long employeeId;

    @Column(nullable = false)
    private UserRole role = UserRole.ROLE_USER;

    @Column(name = "permissions")
    private Set<String> permissions = Set.of();

    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = permissions.stream()
                .filter(permission -> !permission.isBlank())
                .map(it -> new SimpleGrantedAuthority(it))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }
}
