import { Box, Table as MuiTable, Paper, SxProps, TableBody, TableCell, TableContainer, TableHead, TablePagination, TableRow } from "@mui/material"
import { FC, PropsWithChildren, useState } from "react";

interface TableHeader {
    id: string;
    label: string;
    align?: 'right' | 'left' | 'center';
    minWidth?: number;
    padding?: 'none' | 'normal'
    Component: FC<{}>
}

interface TableProps<T extends { id: number }> {
    key?: string;
    count: number;
    page: number;
    onRowsPerPageChange?: (newRowsPerPage: number) => void;
    onPageChange?: (newPage: number) => void;
    
    rows: TableHeader[];
    rowData: T[];
    RowWrapper: FC<PropsWithChildren<{ data: T }>>;
    sx?: SxProps
}

const Table = <T extends { id: number }, >({ count, page, onRowsPerPageChange, onPageChange, rows, rowData, RowWrapper, key, sx }: TableProps<T>) => {
    const [rowsPerPage, setRowsPerPage] = useState(25);

    const handleChangePage = (event: unknown, newPage: number) => {
        onPageChange?.(newPage);
    }

    const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>) => {
        const newRowsPerPage = parseInt(event.target.value, 10);
        setRowsPerPage(newRowsPerPage);
        onRowsPerPageChange?.(newRowsPerPage);
    }

    return (
        <Paper 
            elevation={2} 
            sx={{
                p: { xs: 2, sm: 3 },
                borderRadius: 2,
                gridColumn: { xs: 'span 1', md: '1 / -1' },
                ...sx
            }}>
            <TableContainer>
                <MuiTable stickyHeader >
                    <TableHead>
                        <TableRow>
                            {rows.map((header) => (
                                <TableCell
                                    key={header.id}
                                    align={header.align}
                                    style={{ minWidth: header.minWidth }}
                                    padding={header.padding ?? "normal"}
                                >
                                    {header.label}
                                </TableCell>
                            ))}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {rowData.map((row) => (
                            <TableRow key={`${key}Row-${row.id}`}>
                                <RowWrapper data={row}>
                                    {rows.map(({ Component, padding, align }, index) => (
                                        <TableCell key={`${key}Cell-${index}`} padding={padding ?? "normal"} align={align ?? "left"}>
                                            <Component />
                                        </TableCell>
                                    ))}
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
        </Paper>
    )
}

export { Table }