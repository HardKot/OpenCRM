package com.open.crm.core.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.open.crm.core.entities.client.Client;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.core.entities.investigationLog.InvestigationLog;

public class InvestigationLogCreatorTest {

    private final InvestigationLogCreator creator = new InvestigationLogCreator();

    @Test
    public void testUpdateClientLog() {
        Client client = new Client();
        client.setId(1L);
        Author author = new Author();

        InvestigationLog log = creator.updateClientLog(client, author);

        assertEquals(author, log.getAuthor());
        assertEquals("UPDATE", log.getDetails().getAction());
        assertEquals(1L, log.getDetails().getEntityId());
        assertEquals("CLIENT", log.getDetails().getEntityName());
    }

    @Test
    public void testCreateClientLog() {
        Client client = new Client();
        client.setId(2L);
        Author author = new Author();

        InvestigationLog log = creator.createClientLog(client, author);

        assertEquals(author, log.getAuthor());
        assertEquals("CREATE", log.getDetails().getAction());
        assertEquals(2L, log.getDetails().getEntityId());
        assertEquals("CLIENT", log.getDetails().getEntityName());
    }

    @Test
    public void testMergeClientLog() {
        Client client = new Client();
        client.setId(3L);
        Client other = new Client();
        other.setId(4L);
        Author author = new Author();

        InvestigationLog log = creator.mergeClientLog(client, new Client[] { other }, author);

        assertEquals(author, log.getAuthor());
        assertEquals("MERGE", log.getDetails().getAction());
        assertEquals(3L, log.getDetails().getEntityId());
        assertEquals("CLIENT", log.getDetails().getEntityName());
    }
}