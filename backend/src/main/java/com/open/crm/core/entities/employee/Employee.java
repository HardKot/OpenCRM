package com.open.crm.core.entities.employee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.open.crm.core.entities.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees")
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee extends BaseEntity {

  private String firstname = "";

  private String lastname = "";

  private String patronymic = "";

  private String position = "";

  @Column(name = "phone_number")
  private String phoneNumber = "";

  private String email = "";
}
