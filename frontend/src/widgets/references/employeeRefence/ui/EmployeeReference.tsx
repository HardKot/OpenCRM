import { EmployeeCard } from "#entities/Employee";
import {
  EmployeeFilter,
  useEmployeeFilter,
} from "#features/filter/employeeFilter";
import { EmployeeAction } from "#pages/employee/EmployeeForm";
import { useGetPageEmployees } from "#shared/api";
import { useI18n, usePage } from "#shared/hooks";
import { Layout, Table, Text, View } from "#shared/ui";

interface EmployeeReferenceProps {
  filter?: boolean;
  actions?: boolean;
  isDeleted?: boolean;
  onClick?: (id: number) => void;
  onEdit?: (id: number) => void;
}

const EmployeeReference = ({
  filter,
  actions = false,
  isDeleted = false,
}: EmployeeReferenceProps) => {
  const { t } = useI18n();
  const { page, size, onPageChange, onSizeChange, sort, onSortChange } =
    usePage();
  const { fullname, position } = useEmployeeFilter();
  const { data, isLoading } = useGetPageEmployees({
    page,
    size,
    fullname,
    position,
    isDeleted,
    sortBy: sort?.columnId,
    sortDirection: sort?.direction,
  });

  const tableIsEmpty = !isLoading && !data?.totalElements;

  return (
    <View sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
      <View
        sx={{
          display: "flex",
          flexDirection: { xs: "column", md: "row" },
          gap: 2,
          alignItems: "flex-start",
        }}
      >
        <View sx={{ flex: 1, minWidth: 0, width: "100%" }}>
          {isLoading && <Table.Loading count={size} />}
          {tableIsEmpty && <Table.Empty />}
          {!!data && (
            <Table
              count={data.totalElements}
              page={page}
              rowsPerPage={size}
              onPageChange={onPageChange}
              onRowsPerPageChange={onSizeChange}
              sort={sort}
              onSortChange={onSortChange}
              rows={[
                {
                  id: "id",
                  label: t("employee.fields.id"),
                  padding: "none",
                  align: "right",
                  Component: EmployeeCard.Id,
                  isSortable: true,
                },
                {
                  id: "fullname",
                  label: t("employee.fields.name"),
                  minWidth: 150,
                  Component: EmployeeCard.FullName,
                  isSortable: true,
                },
                {
                  id: "position",
                  label: t("employee.fields.position"),
                  minWidth: 150,
                  Component: EmployeeCard.Position,
                },
              ]}
              rowData={data.models}
              RowWrapper={EmployeeCard}
              {...(actions && {
                ActionComponent: EmployeeAction,
              })}
            />
          )}
        </View>

        {filter && (
          <View
            sx={{
              width: { xs: "100%", md: 340 },
              flexShrink: 0,
              position: { md: "sticky" },
              top: { md: 16 },
            }}
          >
            <Layout.Paper>
              <Text variant="subtitle1" sx={{ mb: 2 }}>
                Фильтр
              </Text>
              <EmployeeFilter />
            </Layout.Paper>
          </View>
        )}
      </View>
    </View>
  );
};

export { EmployeeReference };
