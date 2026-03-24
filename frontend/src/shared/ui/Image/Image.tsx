import React, { ImgHTMLAttributes } from 'react';
import { Box, BoxProps } from '@mui/material';

export interface ImageProps extends ImgHTMLAttributes<HTMLImageElement> {
    containerProps?: BoxProps;
}

export const Image: React.FC<ImageProps> = ({ containerProps, style, ...props }) => {
    return (
        <Box component="span" {...containerProps}>
             <img style={{ maxWidth: '100%', ...style }} {...props} />
        </Box>
    );
};
