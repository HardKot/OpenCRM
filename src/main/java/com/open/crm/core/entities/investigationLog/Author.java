package com.open.crm.core.entities.investigationLog;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Author {
    @Column(name = "author_entity_id")
    private long entityId;
    @Column(name = "author_entity_name")
    private String entityName;

    static public Author of(long entityId, String entityName) {
        Author author = new Author();
        author.setEntityId(entityId);
        author.setEntityName(entityName);
        return author;
    }
}