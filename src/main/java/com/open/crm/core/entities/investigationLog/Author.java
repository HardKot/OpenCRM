package com.open.crm.core.entities.investigationLog;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Author {
    @Column(name = "author_entity_id")
    private Long entityId;

    @Column(name = "author_entity_name")
    @Enumerated(EnumType.STRING)
    private AuthorEntityName entityName;
}