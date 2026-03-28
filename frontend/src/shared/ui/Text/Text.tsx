import React from 'react';
import { Typography, TypographyProps } from '@mui/material';

export interface TextProps extends TypographyProps {
    component?: React.ElementType;
}

export type TextPropsWithoutChildren = Omit<TextProps, 'children'>;

export const Text: React.FC<TextProps> = (props) => {
    return <Typography {...props} />;
};
