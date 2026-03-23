package com.open.crm.core.application.investigation;

import com.open.crm.core.entities.client.Client;
import com.open.crm.core.entities.commodity.Commodity;
import com.open.crm.core.entities.commodity.CommodityCategory;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.core.entities.investigationLog.InvestigationLog;
import com.open.crm.core.entities.investigationLog.LogDetails;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvestigationLogCreator {
  public InvestigationLog updateClientLog(Client client, Author author) {
    InvestigationLog log = new InvestigationLog();
    log.setAuthor(author);
    log.setDetails(
        LogDetails.builder()
            .action("UPDATE")
            .entityId(client.getId())
            .entityName("CLIENT")
            .build());

    return log;
  }

  public InvestigationLog createClientLog(Client client, Author author) {
    InvestigationLog log = new InvestigationLog();
    log.setAuthor(author);
    log.setDetails(
        LogDetails.builder()
            .action("CREATE")
            .entityId(client.getId())
            .entityName("CLIENT")
            .build());

    return log;
  }

  public InvestigationLog mergeClientLog(Client client, Client[] others, Author author) {
    InvestigationLog log = new InvestigationLog();
    log.setAuthor(author);
    log.setDetails(
        LogDetails.builder()
            .action("MERGE")
            .entityId(client.getId())
            .entityName("CLIENT")
            .description(
                "Merged with clients: "
                    + Arrays.toString(Arrays.stream(others).map(Client::getId).toArray()))
            .build());

    return log;
  }

  public InvestigationLog createEmployeeLog(Employee employee, Author author) {
    InvestigationLog log = new InvestigationLog();
    log.setAuthor(author);
    log.setDetails(
        LogDetails.builder().action("CREATE").entityName("").entityId(employee.getId()).build());
    return log;
  }

  public InvestigationLog updateEmployeeLog(Employee employee, Author author) {
    InvestigationLog log = new InvestigationLog();
    log.setAuthor(author);
    log.setDetails(
        LogDetails.builder()
            .action("UPDATE")
            .entityName("EMPLOYEE")
            .entityId(employee.getId())
            .build());
    return log;
  }

  public InvestigationLog inviteEmployeeLog(Employee employee, Author author) {
    InvestigationLog log = new InvestigationLog();
    log.setAuthor(author);
    log.setDetails(
        LogDetails.builder()
            .action("INVITE")
            .entityName("EMPLOYEE")
            .entityId(employee.getId())
            .build());
    return log;
  }

  public InvestigationLog updateAccessEmployeeLog(Employee employee, Author author) {
    InvestigationLog log = new InvestigationLog();
    log.setAuthor(author);
    log.setDetails(
        LogDetails.builder()
            .action("UPDATE_ACCESS")
            .entityName("EMPLOYEE")
            .entityId(employee.getId())
            .build());
    return log;
  }

  public InvestigationLog updateClientBalanceLog(Client client, Author author) {
    InvestigationLog log = new InvestigationLog();
    log.setAuthor(author);
    log.setDetails(
        LogDetails.builder()
            .action("UPDATE_BALANCE")
            .entityId(client.getId())
            .entityName("CLIENT")
            .description("New balance: " + client.getBalance())
            .build());

    return log;
  }

  public InvestigationLog createCommodityCategoryCreationLog(
      CommodityCategory category, Author author) {
    InvestigationLog log = new InvestigationLog();
    log.setAuthor(author);
    log.setDetails(
        LogDetails.builder()
            .action("CREATE")
            .entityId(category.getId())
            .entityName("COMMODITY_CATEGORY")
            .description("Created category: " + category.getName())
            .build());

    return log;
  }

  public InvestigationLog createCommodityCategoryUpdateLog(
      CommodityCategory category, Author author) {
    InvestigationLog log = new InvestigationLog();
    log.setAuthor(author);
    log.setDetails(
        LogDetails.builder()
            .action("UPDATE")
            .entityId(category.getId())
            .entityName("COMMODITY_CATEGORY")
            .description("Updated category: " + category.getName())
            .build());

    return log;
  }

  public InvestigationLog createCommodityCreationLog(Commodity commodity, Author author) {
    InvestigationLog log = new InvestigationLog();
    log.setAuthor(author);
    log.setDetails(
        LogDetails.builder()
            .action("CREATE")
            .entityId(commodity.getId())
            .entityName("COMMODITY")
            .description("Created commodity: " + commodity.getName())
            .build());

    return log;
  }

  public InvestigationLog createCommodityUpdateLog(Commodity commodity, Author author) {
    InvestigationLog log = new InvestigationLog();
    log.setAuthor(author);
    log.setDetails(
        LogDetails.builder()
            .action("UPDATE")
            .entityId(commodity.getId())
            .entityName("COMMODITY")
            .description("Updated commodity: " + commodity.getName())
            .build());

    return log;
  }
}
