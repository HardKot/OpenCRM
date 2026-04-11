import {
  Button as MuiButton,
  IconButton as MuiIconButton,
  ButtonProps as MuiButtonProps,
  CircularProgress,
} from "@mui/material";
import { Icon, IconName } from "../Icon";
import { Icons } from "../Icon/Icon";

export interface ButtonProps extends MuiButtonProps {
  left?: IconName;
  right?: IconName;
  loading?: boolean;
}

export interface ButtonIconProps extends Omit<ButtonProps, "children"> {
  icon: IconName;
}

const Button = ({ loading, children, disabled, ...props }: ButtonProps) => {
  let leftIcon,
    rightIcon = null;
  if (props.left && Icons[props.left]) leftIcon = <Icon name={props.left} />;
  if (props.right && Icons[props.right])
    rightIcon = <Icon name={props.right} />;

  return (
    <MuiButton
      disabled={disabled || loading}
      startIcon={leftIcon}
      endIcon={rightIcon}
      {...props}
    >
      {loading ? <CircularProgress size={24} color="inherit" /> : children}
    </MuiButton>
  );
};

const ButtonIcon = ({ icon, loading, disabled, ...props }: ButtonIconProps) => (
  <MuiIconButton disabled={disabled || loading} {...props}>
    {loading ? (
      <CircularProgress size={24} color="inherit" />
    ) : (
      <Icon name={icon} />
    )}
  </MuiIconButton>
);

const ButtonText = ({ children, ...props }: Omit<ButtonProps, "variant">) => (
  <MuiButton {...props} variant="text">
    {children}
  </MuiButton>
);

Button.Icon = ButtonIcon;
Button.Text = ButtonText;

export { Button };
