package com.open.crm.core.entities.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BaseEntityTest {

    @Test
    public void testSoftDelete() {
        BaseEntity entity = new BaseEntity();
        assertFalse(entity.isDeleted());

        entity.softDelete();
        assertTrue(entity.isDeleted());

        entity.restore();
        assertFalse(entity.isDeleted());
    }
}