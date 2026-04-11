import { SxProps } from "@mui/material";
import { TableWrapper } from "./TableWrapper";

interface TableEmptyProps {
  title?: string;
  message?: string;
  sx?: SxProps;
}

const TableEmpty = ({ title, message, sx }: TableEmptyProps) => (
  <TableWrapper sx={sx}>
    <div className="flex flex-col items-center gap-2 py-6">
      <div className="text-2xl font-medium text-gray-500">
        {title || "Нет данных для отображения"}
      </div>
      {message && <div className="text-gray-500">{message}</div>}
    </div>
  </TableWrapper>
);

export { TableEmpty };
