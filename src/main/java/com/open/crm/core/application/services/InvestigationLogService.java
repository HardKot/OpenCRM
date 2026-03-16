package com.open.crm.core.application.services;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.open.crm.core.application.repositories.IInvestigationLogRepository;
import com.open.crm.core.entities.investigationLog.InvestigationLog;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestigationLogService {
    private final IInvestigationLogRepository investigationLogRepository;

    public void saveLog(InvestigationLog log) {
        log.setId(null);
        investigationLogRepository.save(log);
    }

    public List<InvestigationLog> getLogs(int page, int size) {
        return investigationLogRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page,
                size));
    }

}
