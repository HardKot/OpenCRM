package com.open.crm.core.entities.investigationLog;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class LogDetails implements Serializable {

    private String description;

    private String action;

    private String entityName;

    private Long entityId;
}
