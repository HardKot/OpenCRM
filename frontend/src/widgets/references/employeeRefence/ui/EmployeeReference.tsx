import { EmployeeCard } from "#entities/Employee";
import { EmployeeFilter, useEmployeeFilter } from "#features/filter/employeeFilter";
import { useGetPageEmployees } from "#shared/api"
import { useI18n, usePage } from "#shared/hooks";
import { Layout, Table, Text, Button, View } from "#shared/ui";

interface EmployeeReferenceProps {
    filter?: boolean;
    add?: boolean;
    delete?: boolean;
    edit?: boolean;
}

const EmployeeReference = ({ filter, add }: EmployeeReferenceProps) => {
    const { t } = useI18n();
    const { page, size, onPageChange, onSizeChange } = usePage();
    const { fullname, position } = useEmployeeFilter();
    const { data, isLoading } = useGetPageEmployees({ page, size, fullname, position });

    const showSharedLayout = filter || add;
    const tableIsEmpty = !isLoading && !data?.totalElements;
    

    return (
        <View sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
            { 
                showSharedLayout && (
                    <Layout.Paper>
                        { filter && <EmployeeFilter /> }
                    </Layout.Paper>
                )
            }
            {
                isLoading && <Table.Loading count={size} />
            }
            {
                tableIsEmpty && (<Table.Empty />)
            }
            {
                !!data && (
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
                )
            }
        </View>
    )
}

export { EmployeeReference }