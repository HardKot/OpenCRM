package com.open.crm.core.application.repositories;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.open.crm.core.entities.investigationLog.InvestigationLog;

public interface IInvestigationLogRepository extends JpaRepository<InvestigationLog, Long> {
    @Query("SELECT l FROM InvestigationLog l ORDER BY l.createdAt DESC")
    List<InvestigationLog> findAllByOrderByCreatedAtDesc(PageRequest pageRequest);

}
