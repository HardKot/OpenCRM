import {
    FormControl,
    FormLabel,
    RadioGroup,
    FormControlLabel,
    Radio,
    Paper,
    Stack,
    FormHelperText,
    ButtonGroup,
    Tooltip,
} from '@mui/material';
import { useAppDispatch, useAppSelector, setTheme } from '#shared/index';
import { Button } from '#shared/ui';
import { useI18n } from '#shared/hooks';

const ChangeTheme = () => {
    const dispatch = useAppDispatch();
    const theme = useAppSelector(state => state.appConfig.theme);
    const { t } = useI18n();

    const handleThemeChange = (e: any) => {
        dispatch(setTheme(e.target.value));
    };

    return (
        <Paper sx={{ p: 3 }}>
            <Stack spacing={2}>
                <FormControl>
                    <FormLabel sx={{ fontWeight: 600, mb: 1 }}>
                        {t('settings.theme')}
                    </FormLabel>
                    <RadioGroup
                        row
                        value={theme}
                        onChange={handleThemeChange}
                    >
                        <FormControlLabel
                            value="light"
                            control={<Radio />}
                            label={t('settings.theme_light')}
                        />
                        <FormControlLabel
                            value="dark"
                            control={<Radio />}
                            label={t('settings.theme_dark')}
                        />
                        <FormControlLabel
                            value="system"
                            control={<Radio />}
                            label={t('settings.theme_system')}
                        />
                    </RadioGroup>
                    <FormHelperText sx={{ mt: 1 }}>
                        {t('settings.theme_description')}
                    </FormHelperText>
                </FormControl>
            </Stack>
        </Paper>
    );
};

type ThemeOption = 'light' | 'dark' | 'system';

const ChangeThemeCompact = () => {
    const dispatch = useAppDispatch();
    const theme = useAppSelector(state => state.appConfig.theme);
    const { t } = useI18n();

    const handleThemeChange = (value: ThemeOption) => {
        dispatch(setTheme(value));
    };

    return (
        <ButtonGroup size="small" variant="outlined" sx={{ height: 28 }}>
            <Tooltip title={t('settings.theme_light')}>
                <Button.Icon
                    icon="LightMode"
                    size="small"
                    onClick={() => handleThemeChange('light')}
                    variant={theme === 'light' ? 'contained' : 'outlined'}
                    sx={{ fontSize: '0.75rem', px: 0.75, py: 0.5 }}
                />
            </Tooltip>
            <Tooltip title={t('settings.theme_dark')}>
                <Button.Icon
                    icon="DarkMode"
                    size="small"
                    onClick={() => handleThemeChange('dark')}
                    variant={theme === 'dark' ? 'contained' : 'outlined'}
                    sx={{ fontSize: '0.75rem', px: 0.75, py: 0.5 }}
                />
            </Tooltip>
            <Tooltip title={t('settings.theme_system')}>
                <Button.Icon
                    icon="BrightnessMedium"
                    size="small"
                    onClick={() => handleThemeChange('system')}
                    variant={theme === 'system' ? 'contained' : 'outlined'}
                    sx={{ fontSize: '0.75rem', px: 0.75, py: 0.5 }}
                />
            </Tooltip>
        </ButtonGroup>
    );
};

ChangeTheme.Compact = ChangeThemeCompact;

export { ChangeTheme };
