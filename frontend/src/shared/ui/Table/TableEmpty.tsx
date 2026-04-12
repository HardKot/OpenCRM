import { Box, Typography, SxProps } from "@mui/material";
import { TableWrapper } from "./TableWrapper";

interface TableEmptyProps {
  title?: string;
  message?: string;
  sx?: SxProps;
}

const TableEmpty = ({ title, message, sx }: TableEmptyProps) => (
  <TableWrapper sx={sx}>
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        py: 8,
        px: 3,
        textAlign: "center",
        gap: 1.5,
      }}
    >
      <Box
        sx={{
          width: 80,
          height: 80,
          borderRadius: "50%",
          bgcolor: "action.hover",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          mb: 1,
        }}
      >
        <svg
          width="40"
          height="40"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth={1.5}
          style={{ opacity: 0.5 }}
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M20.25 7.5l-.625 10.632a2.25 2.25 0 01-2.247 2.118H6.622a2.25 2.25 0 01-2.247-2.118L3.75 7.5M10 11.25h4M3.375 7.5h17.25c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125z"
          />
        </svg>
      </Box>
      <Typography variant="h6" color="text.primary" fontWeight={600}>
        {title || "Нет данных для отображения"}
      </Typography>
      {message && (
        <Typography
          variant="body1"
          color="text.secondary"
          sx={{ maxWidth: 400, mx: "auto" }}
        >
          {message}
        </Typography>
      )}
    </Box>
  </TableWrapper>
);

export { TableEmpty };
