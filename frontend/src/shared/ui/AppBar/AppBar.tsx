import { useI18n } from '#shared/hooks';
import { Box, Button, AppBar as MuiAppBar, Toolbar, Typography } from '@mui/material'
import React from 'react';

interface AppBarProps {
    goToMain: () => void;
    Search: React.ReactNode;
    Navigation: React.ReactNode;
    Profile: React.ReactNode;
}

const AppBar = ({ goToMain, Search, Navigation, Profile }: AppBarProps) => {
    const { t } = useI18n();
    return (
        <MuiAppBar position='static' color='primary' enableColorOnDark>
            <Toolbar>
                <Button variant='text' color='inherit' onClick={goToMain}>
                <Typography
                    variant="h6"
                    noWrap
                    component="div"
                    color='inherit'
                    sx={{ display: { xs: 'none', sm: 'block' } }}
                >            
                {t('application.shortName')}
                </Typography>
                </Button>
                <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' }, justifyContent: 'flex-end', gap: 2 }}>
                    {Search}
                    {Navigation}
                </Box>
                {Profile}
            </Toolbar>
        </MuiAppBar>
    )
}

export { AppBar }