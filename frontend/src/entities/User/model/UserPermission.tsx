enum UserPermission {
    EmployeeRead = 'EMPLOYEE_READ',
    EmployeeUpdate = 'EMPLOYEE_UPDATE',
    EmployeeAccess = 'EMPLOYEE_ACCESS',

    ClientRead = 'CLIENT_READ',
    ClientUpdate = 'CLIENT_UPDATE',
    ClientNameShow = 'CLIENT_NAME_SHOW',
    ClientContactShow = 'CLIENT_CONTACT_SHOW',

    InvestigationLogRead = 'INVESTIGATION_LOG_READ',

    CommodityRead = 'COMMODITY_READ',
    CommodityUpdate = 'COMMODITY_UPDATE',    
}

export { UserPermission }