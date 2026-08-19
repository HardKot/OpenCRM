import { UserPermission } from "#entities/User";
import { useHasPermission } from "#features/auth/AccessPermission";
import { useGetPageInvestigationLogs } from "#shared/api";
import { useI18n, usePage } from "#shared/hooks";
import { Layout, View, Text, Table } from "#shared/ui";
import { InvestigationLogItem } from "./InvestigatioLogItem";

const InvestigationLogPage = () => {
  const hasReadLogPermission = useHasPermission(
    UserPermission.InvestigationLogRead,
  );
  const { t } = useI18n();
  const { page, onPageChange, onSizeChange, size } = usePage();

  const { data, isLoading } = useGetPageInvestigationLogs({
    page,
    size,
  });

  const tableIsEmpty = !isLoading && !data?.totalElements;
  const tableHasData = !!data?.totalElements;
  const indexOffset = (page - 1) * size + 1;

  if (!hasReadLogPermission) return null;

  return (
    <View
      sx={{ display: "flex", flexDirection: "column", gap: 3, height: "100%" }}
    >
      <Layout.Paper
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          gap: 2,
          flexWrap: "wrap",
          p: 3,
          borderRadius: 3,
          boxShadow: "0 4px 24px rgba(0, 0, 0, 0.04)",
        }}
      >
        <Text variant="h5" fontWeight={600} color="text.primary">
          {t("investigationLog.title")}
        </Text>
      </Layout.Paper>

      {isLoading && <Table.Loading count={size} />}
      {tableIsEmpty && (
        <Table.Empty
          title={t("investigationLog.emptyTitle")}
          message={t("investigationLog.emptyMessage")}
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
          rowData={data.models}
          RowWrapper={({ children, data }) => (
            <InvestigationLogItem data={data}>{children}</InvestigationLogItem>
          )}
          sort={{ columnId: "id", direction: "desc" }}
          rows={[
            {
              id: "id",
              label: t("investigationLog.field.id"),
              padding: "none",
              align: "right",
              Component: ({ index }: { index: number }) => (
                <Text>{index + indexOffset}</Text>
              ),
            },
            {
              id: "createdAt",
              label: t("investigationLog.field.createdAt"),
              Component: () => <InvestigationLogItem.CreatedAt />,
            },
            {
              id: "author",
              label: t("investigationLog.field.author"),
              Component: () => <InvestigationLogItem.Author />,
            },

            {
              id: "action",
              label: t("investigationLog.field.action"),
              Component: () => <InvestigationLogItem.Action />,
            },
            {
              id: "description",
              label: t("investigationLog.field.description"),
              Component: () => (
                <Text>
                  <InvestigationLogItem.Entity />
                  <InvestigationLogItem.Details />
                </Text>
              ),
            },
          ]}
        />
      )}
    </View>
  );
};

export { InvestigationLogPage };
