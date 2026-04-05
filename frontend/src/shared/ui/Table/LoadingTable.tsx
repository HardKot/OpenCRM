import { Skeleton, SxProps } from "@mui/material";
import { TableWrapper } from "./TableWrapper";

interface LoadingTableProps {
    count?: number;
    sx?: SxProps
}

const LoadingTable = ({ sx = {}, count = 25 }: LoadingTableProps) => (
    <TableWrapper
        sx={{
            display: "flex",
            flexDirection: "column",
            gap: 2,
            ...sx
        }}>
        {Array.from({ length: count }).map((_, index) => (
            <Skeleton variant="rounded" width="100%" height={40} key={index} />
        ))}
    </TableWrapper>
)

export { LoadingTable }