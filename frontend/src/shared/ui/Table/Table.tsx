import { Table as MuiTable, TableBody, TableCell, TableContainer, TableHead, TablePagination, TableRow } from "@mui/material"
import { FC, PropsWithChildren, useState } from "react";

interface TableHeader {
    id: string;
    label: string;
    align?: 'right' | 'left' | 'center';
    minWidth?: number;
    padding?: 'none' | 'normal'
}

interface TableProps<T extends { id: number }> {
    key?: string;
    count: number;
    page: number;
    onRowsPerPageChange?: (newRowsPerPage: number) => void;
    onPageChange?: (newPage: number) => void;
    
    headers: TableHeader[];
    rowData: T[];
    RowWrapper: FC<PropsWithChildren<{ data: T }>>;
    rows: FC<{}>[];
}

const Table = <T extends { id: number }, >({ count, page, onRowsPerPageChange, onPageChange, headers, rowData, rows, RowWrapper, key }: TableProps<T>) => {
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
        <>
            <TableContainer>
                <MuiTable stickyHeader >
                    <TableHead>
                        {headers.map((header) => (
                            <TableCell
                                key={header.id}
                                align={header.align}
                                style={{ minWidth: header.minWidth }}
                                padding={header.padding ?? "normal"}
                            >
                                {header.label}
                            </TableCell>
                        ))}
                    </TableHead>
                    <TableBody>
                        {rowData.map((row) => (
                            <TableRow key={`${key}Row-${row.id}`}>
                                <RowWrapper data={row}>
                                    {rows.map((ColumnComponent, index) => (
                                        <TableCell key={`${key}Cell-${index}`} padding={headers[index]?.padding ?? "normal"} align={headers[index]?.align ?? "left"}>
                                            <ColumnComponent />
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
                page={page}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
            />
        </>
    )
}

export { Table }