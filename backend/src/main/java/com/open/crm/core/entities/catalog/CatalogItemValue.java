package com.open.crm.core.entities.catalog;

import com.open.crm.core.entities.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "catalogItem")
public class CatalogItemValue extends BaseEntity {
  private Object value;

  @ManyToOne(fetch = FetchType.LAZY)
  private CatalogItem catalogItem;

  @ManyToOne(fetch = FetchType.LAZY)
  private Catalog catalog;

  @ManyToOne(fetch = FetchType.LAZY)
  private CatalogField field;

  public String getName() {
    return field.getName();
  }

  public CatalogFieldType getType() {
    return field.getType();
  }

  public String getDescription() {
    return field.getDescription();
  }

  public void setValue(Object value) {
    if (Objects.isNull(value)) {
      this.value = null;
      return;
    }
    switch (field.getType()) {
      case STRING -> this.value = value.toString().replaceAll("\\R", " ");
      case TEXT -> this.value = value.toString();
      case INTEGER, CURRENCY -> this.value = Integer.parseInt(value.toString());
      case NUMBER -> this.value = Double.parseDouble(value.toString());
      case BOOLEAN -> this.value = Boolean.parseBoolean(value.toString());
      case TIME -> this.value = LocalTime.parse(value.toString());
      case DATE -> this.value = LocalDate.parse(value.toString());
      case DATETIME -> this.value = LocalTime.parse(value.toString());
      case DURATION -> this.value = Duration.parse(value.toString());
      case ENUM -> {
        String enums = field.getOption("enums");
        String valueStr = value.toString();
        if (!enums.contains(valueStr))
          throw new IllegalArgumentException("Value is not in the enum list");
        this.value = value.toString();
      }
      case REFERENCE -> this.value = value;
    }
  }

  public String getAsString() {
    if (getType() != CatalogFieldType.TEXT
        && getType() != CatalogFieldType.STRING
        && getType() != CatalogFieldType.ENUM)
      throw new IllegalStateException("Field type is not TEXT");
    if (Objects.isNull(value)) return null;
    if (value instanceof String) return (String) value;
    return value.toString();
  }

  public Integer getAsInteger() {
    if (getType() != CatalogFieldType.INTEGER && getType() != CatalogFieldType.CURRENCY)
      throw new IllegalStateException("Field type is not INTEGER");
    if (Objects.isNull(value)) return null;
    if (value instanceof Integer) return (Integer) value;
    return Integer.parseInt(value.toString());
  }

  public Double getAsDouble() {
    if (getType() != CatalogFieldType.NUMBER)
      throw new IllegalStateException("Field type is not DOUBLE");

    if (Objects.isNull(value)) return null;
    if (value instanceof Double) return (Double) value;
    return Double.parseDouble(value.toString());
  }

  public Boolean getAsBoolean() {
    if (getType() != CatalogFieldType.BOOLEAN)
      throw new IllegalStateException("Field type is not BOOLEAN");

    if (Objects.isNull(value)) return null;
    if (value instanceof Boolean) return (Boolean) value;
    return Boolean.parseBoolean(value.toString());
  }

  public LocalTime getAsLocalTime() {
    if (getType() != CatalogFieldType.TIME)
      throw new IllegalStateException("Field type is not TIME");

    if (Objects.isNull(value)) return null;
    if (value instanceof LocalTime) return (LocalTime) value;
    return LocalTime.parse(value.toString());
  }

  public LocalDate getAsLocalDate() {
    if (getType() != CatalogFieldType.DATE)
      throw new IllegalStateException("Field type is not DATE");

    if (Objects.isNull(value)) return null;
    if (value instanceof LocalDate) return (LocalDate) value;
    return LocalDate.parse(value.toString());
  }

  public LocalDateTime getAsLocalDateTime() {
    if (getType() != CatalogFieldType.DATETIME)
      throw new IllegalStateException("Field type is not DATETIME");

    if (Objects.isNull(value)) return null;
    if (value instanceof LocalDateTime) return (LocalDateTime) value;
    return LocalDateTime.parse(value.toString());
  }

  public Duration getAsDuration() {
    if (getType() != CatalogFieldType.DURATION)
      throw new IllegalStateException("Field type is not DURATION");

    if (Objects.isNull(value)) return null;
    if (value instanceof Duration) return (Duration) value;
    return Duration.parse(value.toString());
  }
}
