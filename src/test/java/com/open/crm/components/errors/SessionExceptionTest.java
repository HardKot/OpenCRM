package com.open.crm.components.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class SessionExceptionTest {

    @Test
    public void testConstructor() {
        String message = "Test message";
        SessionException exception = new SessionException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testThrow() {
        assertThrows(SessionException.class, () -> {
            throw new SessionException("Unauthorized");
        });
    }
}