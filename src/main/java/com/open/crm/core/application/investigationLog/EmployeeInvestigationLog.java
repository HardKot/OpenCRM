package com.open.crm.core.application.investigationLog;

import org.springframework.stereotype.Service;

import com.open.crm.core.application.repositories.IInvestigationLogRepository;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.core.entities.investigationLog.InvestigationLog;
import com.open.crm.core.entities.investigationLog.LogDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeInvestigationLog {
    private final IInvestigationLogRepository investigationLogRepository;

    public InvestigationLog createEmployeeLog(Employee employee, Author author) {
        InvestigationLog log = new InvestigationLog();
        log.setAuthor(author);
        log.setDetails(LogDetails.builder().action("CREATE").entityName("").entityId(employee.getId()).build());
        investigationLogRepository.save(log);
        return log;
    }

    public InvestigationLog updateEmployeeLog(Employee employee, Author author) {
        InvestigationLog log = new InvestigationLog();
        log.setAuthor(author);
        log.setDetails(LogDetails.builder().action("UPDATE").entityName("EMPLOYEE").entityId(employee.getId()).build());
        investigationLogRepository.save(log);
        return log;
    }

    public InvestigationLog inviteEmployeeLog(Employee employee, Author author) {
        InvestigationLog log = new InvestigationLog();
        log.setAuthor(author);
        log.setDetails(LogDetails.builder().action("INVITE").entityName("EMPLOYEE").entityId(employee.getId()).build());
        investigationLogRepository.save(log);
        return log;
    }

    public InvestigationLog updateAccessEmployeeLog(Employee employee, Author author) {
        InvestigationLog log = new InvestigationLog();
        log.setAuthor(author);
        log.setDetails(
                LogDetails.builder().action("UPDATE_ACCESS").entityName("EMPLOYEE").entityId(employee.getId()).build());
        investigationLogRepository.save(log);
        return log;
    }
}
