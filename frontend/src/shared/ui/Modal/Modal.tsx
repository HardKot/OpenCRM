import React from 'react';
import { Dialog, DialogContent, DialogTitle, DialogActions, DialogProps } from '@mui/material';

export interface ModalProps extends DialogProps {
    title?: string;
    footer?: React.ReactNode;
    onClose: () => void;
}

export const Modal: React.FC<ModalProps> = ({ title, children, footer, onClose, ...props }) => {
    return (
        <Dialog onClose={onClose} {...props}>
            {title && <DialogTitle>{title}</DialogTitle>}
            <DialogContent>
                {children}
            </DialogContent>
            {footer && <DialogActions>{footer}</DialogActions>}
        </Dialog>
    );
};
