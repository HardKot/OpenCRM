import {
  Table as MuiTable,
  SxProps,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TableSortLabel,
} from "@mui/material";
import { ChangeEvent, FC, PropsWithChildren } from "react";
import { LoadingTable } from "./LoadingTable";
import { TableWrapper } from "./TableWrapper";
import { TableEmpty } from "./TableEmpty";

interface TableHeader {
  id: string;
  label: string;
  align?: "right" | "left" | "center";
  minWidth?: number;
  padding?: "none" | "normal";
  isSortable?: boolean;
  Component: FC<{ index: number }>;
}

interface TableProps<T extends { id?: number }> {
  key?: string;
  count: number;
  page: number;
  rowsPerPage: number;
  sort: { columnId: string; direction: "asc" | "desc" } | null;
  onRowsPerPageChange?: (newRowsPerPage: number) => void;
  onPageChange?: (newPage: number) => void;
  onSortChange?: (columnId: string, direction: "asc" | "desc") => void;
  onRowClick?: (id: number) => void;
  rows: TableHeader[];
  rowData: T[];
  RowWrapper: FC<PropsWithChildren<{ data: T }>>;
  sx?: SxProps;
  ActionComponent?: FC<{ data: T }>;
}

const isSortColumn = (
  column: TableHeader,
  sort: { columnId: string; direction: "asc" | "desc" } | null,
) => {
  if (!column.isSortable || !sort) return false;
  if (sort.columnId !== column.id) return false;
  return true;
};

const Table = <T extends { id?: number }>({
  count,
  page,
  rowsPerPage,
  sort,
  onRowsPerPageChange,
  onPageChange,
  onSortChange,
  onRowClick,
  rows,
  rowData,
  RowWrapper,
  key,
  sx = {},
  ActionComponent,
}: TableProps<T>) => {
  const handleChangePage = (event: unknown, newPage: number) => {
    onPageChange?.(newPage);
  };

  const handleChangeRowsPerPage = (
    event: ChangeEvent<HTMLTextAreaElement | HTMLInputElement>,
  ) => {
    const newRowsPerPage = parseInt(event.target.value, 10);
    onRowsPerPageChange?.(newRowsPerPage);
  };

  const handleChangeSort = (columnId: string) => {
    let direction: "asc" | "desc" = "asc";
    if (sort?.columnId === columnId && sort.direction === "asc") {
      direction = "desc";
    }
    onSortChange?.(columnId, direction);
  };

  return (
    <TableWrapper sx={{ display: "flex", flexDirection: "column", ...sx }}>
      <TableContainer sx={{ flexGrow: 1, overflow: "auto" }}>
        <MuiTable stickyHeader>
          <TableHead>
            <TableRow>
              {rows.map((header) => (
                <TableCell
                  key={header.id}
                  align={header.align}
                  style={{ minWidth: header.minWidth }}
                  padding={header.padding ?? "normal"}
                  sortDirection={isSortColumn(header, sort) && sort?.direction}
                >
                  {header.isSortable ? (
                    <TableSortLabel
                      active={isSortColumn(header, sort)}
                      direction={
                        isSortColumn(header, sort) ? sort?.direction : "asc"
                      }
                      onClick={() => handleChangeSort(header.id)}
                    >
                      {header.label}
                    </TableSortLabel>
                  ) : (
                    header.label
                  )}
                </TableCell>
              ))}
              {!!ActionComponent && <TableCell key="actions" padding="none" />}
            </TableRow>
          </TableHead>
          <TableBody>
            {rowData.map((row, rowIndex) => (
              <TableRow
                key={`${key}Row-${row.id}`}
                onClick={
                  onRowClick && row.id !== undefined
                    ? () => onRowClick(row.id as number)
                    : undefined
                }
                sx={{
                  cursor: onRowClick ? "pointer" : "default",
                  "&:hover": onRowClick ? { bgcolor: "action.hover" } : {},
                }}
              >
                <RowWrapper data={row}>
                  {rows.map(({ Component, padding, align }, index) => (
                    <TableCell
                      key={`${key}Cell-${index}`}
                      padding={padding ?? "normal"}
                      align={align ?? "left"}
                    >
                      <Component index={rowIndex} />
                    </TableCell>
                  ))}
                  {!!ActionComponent && (
                    <TableCell
                      key={`${key}Cell-actions`}
                      align="right"
                      padding="none"
                    >
                      <ActionComponent data={row} />
                    </TableCell>
                  )}
                </RowWrapper>
              </TableRow>
            ))}
          </TableBody>
        </MuiTable>
      </TableContainer>
      <TablePagination
        component="div"
        rowsPerPageOptions={[25, 50, 100, 150]}
        count={count}
        rowsPerPage={rowsPerPage}
        page={page - 1}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
      />
    </TableWrapper>
  );
};

Table.Loading = LoadingTable;
Table.Empty = TableEmpty;

export { Table };
