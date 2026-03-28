import { useI18n } from '#shared/hooks';
import { Box, AppBar as MuiAppBar, TextField, Toolbar, Typography } from '@mui/material'
import React from 'react';

interface AppBarProps {
   Search: React.ReactNode;
   Navigation: React.ReactNode;
   Profile: React.ReactNode;
}

const AppBar = ({ Search, Navigation, Profile }: AppBarProps) => {
    const { t } = useI18n();
    return (
        <MuiAppBar position='static'>
            <Toolbar>
                <Typography
                    variant="h6"
                    noWrap
                    component="div"
                    color='white'
                    sx={{ display: { xs: 'none', sm: 'block' } }}
                >            
                {t('application.shortName')}
                </Typography>
                <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' }}}>
                    {Search}
                    {Navigation}

                </Box>
                {Profile}
            </Toolbar>
        </MuiAppBar>
    )
}

export { AppBar }