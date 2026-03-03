package com.open.crm.core.domain.employee;

import java.util.UUID;

import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.TenantId;

import com.open.crm.core.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees")
@NoArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE employees SET is_deleted = true WHERE id = ?")
public class Employee extends BaseEntity {

    private String firstname = "";
    private String lastname = "";
    private String patronymic = "";

    private String email = "";
    
    @Column(name = "phone_number")
    private String phoneNumber = "";

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
