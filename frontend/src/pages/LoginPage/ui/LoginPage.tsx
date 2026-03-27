import React from 'react';
import { useNavigate } from 'react-router-dom';
import { LoginForm } from '#features/AuthByUsername';
import { ChangeLanguage, ChangeTheme } from '#features/AppSettings';
import { View, Text } from '#shared/ui';
import { useI18n } from '#shared/hooks';

const LoginPage: React.FC = () => {
    const navigate = useNavigate();
    const { t } = useI18n();

    const onSuccess = () => {
        navigate('/');
    };

    return (
        <View 
            display="flex" 
            justifyContent="center" 
            alignItems="center" 
            minHeight="100vh"
            bgcolor="background.default"
            position="relative"
        >
            <View
                position="absolute"
                top={16}
                right={16}
                display="flex"
                gap={2}
                flexDirection={{ xs: 'column', sm: 'row' }}
                maxWidth={{ xs: '90vw', sm: '400px' }}
            >
                <View flex={1} minWidth={120}>
                    <ChangeLanguage.Compact />
                </View>
                <View flex={1} minWidth={120}>
                    <ChangeTheme.Compact />
                </View>
            </View>

             <View 
                p={4} 
                bgcolor="background.paper"
                borderRadius={2}
                boxShadow={3}
                width={{ xs: 'calc(100% - 32px)', sm: 440 }}
                maxWidth={440}
                justifyItems={"center"}
            >
                <Text variant="h4" align="center" gutterBottom color="primary">
                    {t('application.name')}
                </Text>
                <LoginForm onSuccess={onSuccess} />
            </View>
        </View>
    );
};

export default LoginPage;
