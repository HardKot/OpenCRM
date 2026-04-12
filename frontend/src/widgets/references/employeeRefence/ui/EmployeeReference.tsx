import { EmployeeCard } from "#entities/Employee";
import {
  EmployeeFilter,
  useEmployeeFilter,
} from "#features/filter/employeeFilter";
import { EmployeeAction } from "#pages/employee/EmployeeFormPage";
import { useGetPageEmployees } from "#shared/api";
import { useI18n, usePage } from "#shared/hooks";
import { Layout, Table, Text, View } from "#shared/ui";
import { SxProps } from "@mui/material";

interface EmployeeReferenceProps {
  filter?: boolean;
  actions?: boolean;
  isDeleted?: boolean;
  onClick?: (id: number) => void;
  sx?: SxProps;
}

const EmployeeReference = ({
  filter,
  actions = false,
  isDeleted = false,
  onClick,
  sx = {},
}: EmployeeReferenceProps) => {
  const { t } = useI18n();
  const { page, size, onPageChange, onSizeChange, sort, onSortChange } =
    usePage();
  const employeeFilter = useEmployeeFilter();
  const { data, isLoading } = useGetPageEmployees({
    ...employeeFilter,
    page,
    size,
    isDeleted,
    sortBy: sort?.columnId,
    sortDirection: sort?.direction,
  });

  const tableIsEmpty = !isLoading && !data?.totalElements;
  const tableHasData = !!data?.totalElements;
  const indexOffset = (page - 1) * size + 1;

  return (
    <View sx={{ display: "flex", flexDirection: "column", gap: 2, ...sx }}>
      <View
        sx={{
          display: "flex",
          flexDirection: { xs: "column", md: "row" },
          gap: 2,
          alignItems: "flex-start",
          flexGrow: 1,
          minHeight: 0,
        }}
      >
        <View
          sx={{
            flex: 1,
            minWidth: 0,
            width: "100%",
            height: "100%",
            display: "flex",
            flexDirection: "column",
          }}
        >
          {isLoading && <Table.Loading count={size} />}
          {tableIsEmpty && (
            <Table.Empty
              title={t("employee.reference.empty")}
              message={t("employee.reference.emptyMessage")}
            />
          )}
          {tableHasData && (
            <Table
              sx={{ height: "100%" }}
              count={data.totalElements}
              page={page}
              rowsPerPage={size}
              onPageChange={onPageChange}
              onRowsPerPageChange={onSizeChange}
              sort={sort}
              onSortChange={onSortChange}
              onRowClick={onClick}
              rows={[
                {
                  id: "id",
                  label: t("employee.fields.id"),
                  padding: "none",
                  align: "right",
                  Component: ({ index }: { index: number }) => (
                    <Text>{index + indexOffset}</Text>
                  ),
                  isSortable: true,
                },
                {
                  id: "fullname",
                  label: t("employee.fields.name"),
                  minWidth: 150,
                  Component: () => <EmployeeCard.FullName />,
                  isSortable: true,
                },
                {
                  id: "position",
                  label: t("employee.fields.position"),
                  minWidth: 150,
                  Component: () => <EmployeeCard.Position />,
                },
                {
                  id: "email",
                  label: t("employee.fields.email"),
                  minWidth: 150,
                  Component: () => <EmployeeCard.Email default="-" />,
                  isSortable: true,
                },
                {
                  id: "phone",
                  label: t("employee.fields.phone"),
                  minWidth: 150,
                  Component: () => <EmployeeCard.Phone default="-" />,
                },
              ]}
              rowData={data.models}
              RowWrapper={({ children, data }) => (
                <EmployeeCard data={data}>{children}</EmployeeCard>
              )}
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
              top: { md: 24 },
            }}
          >
            <Layout.Paper
              sx={{
                p: 3,
                borderRadius: 3,
                boxShadow: "0 4px 24px rgba(0, 0, 0, 0.04)",
              }}
            >
              <Text
                variant="h6"
                fontWeight={600}
                color="text.primary"
                sx={{ mb: 3, opacity: 0.9 }}
              >
                {t("employee.reference.filterTitle")}
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
