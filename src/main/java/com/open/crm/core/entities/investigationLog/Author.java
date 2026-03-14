package com.open.crm.core.entities.investigationLog;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Author {
    private long entityId;
    private String entityName;

    static public Author of(long entityId, String entityName) {
        Author author = new Author();
        author.setEntityId(entityId);
        author.setEntityName(entityName);
        return author;
    }
}