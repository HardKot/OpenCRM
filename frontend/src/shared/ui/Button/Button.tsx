import { Button as MuiButton, ButtonProps as MuiButtonProps, CircularProgress } from '@mui/material';
import { Icon, IconName } from '../Icon';
import { forwardRef } from 'react';

export interface ButtonProps extends MuiButtonProps {
    loading?: boolean;
}

export interface ButtonIconProps extends Omit<ButtonProps, 'children'> {
    icon: IconName;
}

const Button = ({ loading, children, disabled, ...props }: ButtonProps) => {
    return (
        <MuiButton disabled={disabled || loading} {...props}>
            {loading ? <CircularProgress size={24} color="inherit" /> : children}
        </MuiButton>
    );
};

const ButtonIcon = forwardRef(({ icon, loading, disabled, ...props }: ButtonIconProps, ref) =>  (
    <MuiButton disabled={disabled || loading} {...props}>
        {loading ? <CircularProgress size={24} color="inherit" /> : <Icon name={icon} />}
    </MuiButton>
));

Button.Icon = ButtonIcon;

export { Button} 