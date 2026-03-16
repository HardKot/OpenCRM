package com.open.crm.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.open.crm.core.application.services.InvestigationLogService;
import com.open.crm.core.entities.investigationLog.InvestigationLog;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/log")
@PreAuthorize("hasPermission(null, 'INVESTIGATION_LOG_READ')")
@RequiredArgsConstructor
public class InvestigationLogController {
    private final InvestigationLogService investigationLogService;

    @GetMapping
    public List<InvestigationLog> actionGetLogs(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "100") int size) {

        return investigationLogService.getLogs(page - 1, size);
    }

}
