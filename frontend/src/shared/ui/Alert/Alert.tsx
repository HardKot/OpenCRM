import { Alert as MuiAlert } from "@mui/material";

interface IAlertProps {
  message: string;
}

const SuccessAlert = ({ message }: IAlertProps) => (
  <MuiAlert severity="success">{message}</MuiAlert>
);

const Alert = {
  Success: SuccessAlert,
};

export { Alert, SuccessAlert };
