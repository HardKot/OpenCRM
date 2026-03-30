import { EmployeeCard } from "#entities/Employee";
import { useGetPageEmployees } from "#shared/api"
import { useI18n, usePage } from "#shared/hooks";
import { Table } from "#shared/ui/Table";

const EmployeeReference = () => {
    const { t } = useI18n();
    const { page, size, onPageChange, onSizeChange } = usePage();
    const { data, isLoading } = useGetPageEmployees({ page, size });
    
    if (isLoading) return <div>Loading...</div>;

    if (!data) return <div>No data</div>;


    return (
        <Table 
            key="employeeReferenceTable"
            count={data.totalElements}
            page={page}
            onPageChange={onPageChange}
            onRowsPerPageChange={onSizeChange}
            headers={[
                { id: "id", label: t("employee.fields.id"), padding: "none", align: "right" },
                { id: "name", label: t("employee.fields.name"), minWidth: 150 },
                { id: "position", label: t("employee.fields.position"), minWidth: 150 },
            ]}
            rowData={data.models}
            RowWrapper={EmployeeCard}
            rows={[
                EmployeeCard.Id,
                EmployeeCard.FullName,
                EmployeeCard.Position,
            ]}
        />
    )
}

export { EmployeeReference }