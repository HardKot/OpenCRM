package com.open.crm.core.application.investigationLog;

import org.springframework.stereotype.Service;

import com.open.crm.core.entities.client.Client;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.core.entities.investigationLog.InvestigationLog;
import com.open.crm.core.entities.investigationLog.LogDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientInvestigationLog {

    public InvestigationLog updateClientLog(Client client, Author author) {
        InvestigationLog log = new InvestigationLog();
        log.setAuthor(author);
        log.setDetails(LogDetails.builder()
                .action("UPDATE")
                .entityId(client.getId())
                .entityName("CLIENT")
                .build());

        return log;
    }

    public InvestigationLog createClientLog(Client client, Author author) {
        InvestigationLog log = new InvestigationLog();
        log.setAuthor(author);
        log.setDetails(LogDetails.builder()
                .action("CREATE")
                .entityId(client.getId())
                .entityName("CLIENT")
                .build());

        return log;
    }

    public InvestigationLog deleteClientLog(Client client, Author author) {
        InvestigationLog log = new InvestigationLog();
        log.setAuthor(author);
        log.setDetails(LogDetails.builder()
                .action("DELETE")
                .entityId(client.getId())
                .entityName("CLIENT")
                .build());

        return log;
    }

    public InvestigationLog restoreClientLog(Client client, Author author) {
        InvestigationLog log = new InvestigationLog();
        log.setAuthor(author);
        log.setDetails(LogDetails.builder()
                .action("RESTORE")
                .entityId(client.getId())
                .entityName("CLIENT")
                .build());

        return log;
    }

    public InvestigationLog mergeClientLog(Client client, Client[] others, Author author) {
        InvestigationLog log = new InvestigationLog();
        log.setAuthor(author);
        log.setDetails(LogDetails.builder()
                .action("MERGE")
                .entityId(client.getId())
                .entityName("CLIENT")
                .description("Merged with clients: "
                        + java.util.Arrays.toString(java.util.Arrays.stream(others).map(Client::getId).toArray()))
                .build());

        return log;
    }
}
