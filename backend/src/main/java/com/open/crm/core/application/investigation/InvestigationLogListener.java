package com.open.crm.core.application.investigation;

import com.open.crm.core.application.investigation.events.*;
import com.open.crm.core.entities.investigationLog.InvestigationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Service
public class InvestigationLogListener {
  private final InvestigationLogService investigationLogService;
  private final InvestigationLogCreator investigationLogCreator;

  @TransactionalEventListener
  public void onCreateEmployee(CreateEmployeeEvent event) {
    InvestigationLog log =
        investigationLogCreator.createEmployeeLog(event.employee(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onInviteEmployee(InviteEmployeeEvent event) {
    InvestigationLog log =
        investigationLogCreator.inviteEmployeeLog(event.employee(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onUpdateEmployee(UpdateEmployeeEvent event) {
    InvestigationLog log =
        investigationLogCreator.updateEmployeeLog(event.employee(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onUpdateAccessEmployee(UpdateAccessEmployeeEvent event) {
    InvestigationLog log =
        investigationLogCreator.updateAccessEmployeeLog(event.employee(), event.author());
    investigationLogService.saveLog(log);
  }

  // Commodity events
  @TransactionalEventListener
  public void onCreateCommodity(CreateCommodityEvent event) {
    InvestigationLog log =
        investigationLogCreator.createCommodityCreationLog(event.commodity(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onUpdateCommodity(UpdateCommodityEvent event) {
    InvestigationLog log =
        investigationLogCreator.createCommodityUpdateLog(event.commodity(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onDeleteCommodity(DeleteCommodityEvent event) {
    InvestigationLog log =
        investigationLogCreator.createCommodityUpdateLog(event.commodity(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onRestoreCommodity(RestoreCommodityEvent event) {
    InvestigationLog log =
        investigationLogCreator.createCommodityUpdateLog(event.commodity(), event.author());
    investigationLogService.saveLog(log);
  }

  // CommodityCategory events
  @TransactionalEventListener
  public void onCreateCommodityCategory(CreateCommodityCategoryEvent event) {
    InvestigationLog log =
        investigationLogCreator.createCommodityCategoryCreationLog(
            event.category(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onUpdateCommodityCategory(UpdateCommodityCategoryEvent event) {
    InvestigationLog log =
        investigationLogCreator.createCommodityCategoryUpdateLog(event.category(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onDeleteCommodityCategory(DeleteCommodityCategoryEvent event) {
    InvestigationLog log =
        investigationLogCreator.createCommodityCategoryUpdateLog(event.category(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onRestoreCommodityCategory(RestoreCommodityCategoryEvent event) {
    InvestigationLog log =
        investigationLogCreator.createCommodityCategoryUpdateLog(event.category(), event.author());
    investigationLogService.saveLog(log);
  }

  // Client events
  @TransactionalEventListener
  public void onCreateClient(CreateClientEvent event) {
    InvestigationLog log = investigationLogCreator.createClientLog(event.client(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onUpdateClient(UpdateClientEvent event) {
    InvestigationLog log = investigationLogCreator.updateClientLog(event.client(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onDeleteClient(DeleteClientEvent event) {
    InvestigationLog log = investigationLogCreator.updateClientLog(event.client(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onRestoreClient(RestoreClientEvent event) {
    InvestigationLog log = investigationLogCreator.updateClientLog(event.client(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onMergeClient(MergeClientEvent event) {
    InvestigationLog log =
        investigationLogCreator.mergeClientLog(
            event.targetClient(), event.sourceClients(), event.author());
    investigationLogService.saveLog(log);
  }

  @TransactionalEventListener
  public void onUpdateClientBalance(UpdateClientBalanceEvent event) {
    InvestigationLog log =
        investigationLogCreator.updateClientBalanceLog(event.client(), event.author());
    investigationLogService.saveLog(log);
  }
}
