import { EmployeeCard } from "#entities/Employee";
import { useGetPageEmployees } from "#shared/api"
import { useI18n, usePage } from "#shared/hooks";
import { Layout, Table, Text } from "#shared/ui";

const EmployeeReference = () => {
    const { t } = useI18n();
    const { page, size, onPageChange, onSizeChange } = usePage();
    const { data, isLoading } = useGetPageEmployees({ page, size });
    
    if (isLoading) return <div>Loading...</div>;

    if (!data) return <div>No data</div>;


    return (
        <>
            <Layout.Paper>
                <Text variant="h6">{t("employee.title")}</Text>
                
            </Layout.Paper>

            <Table
                sx={{ mt: 2 }}
                count={data.totalElements}
                page={page}
                onPageChange={onPageChange}
                onRowsPerPageChange={onSizeChange}
                rows={[
                    { id: "id", label: t("employee.fields.id"), padding: "none", align: "right", Component: EmployeeCard.Id },
                    { id: "name", label: t("employee.fields.name"), minWidth: 150, Component: EmployeeCard.FullName },
                    { id: "position", label: t("employee.fields.position"), minWidth: 150, Component: EmployeeCard.Position },
                ]}
                rowData={data.models}
                RowWrapper={EmployeeCard}
            />
        </>
    )
}

export { EmployeeReference }