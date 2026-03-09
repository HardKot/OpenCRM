package com.open.crm.core.application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.open.crm.core.entities.investigationLog.InvestigationLog;

public interface IInvestigationLogRepository extends JpaRepository<InvestigationLog, Long> {

}
