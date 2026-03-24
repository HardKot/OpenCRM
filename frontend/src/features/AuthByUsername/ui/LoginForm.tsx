import React, { useCallback } from 'react';
import { useForm } from 'react-hook-form';
import { useSelector } from 'react-redux';
import { Button, Text, TextInput, View } from '#shared/ui';
import { LoginByUsernameProps } from '../model/types/loginSchema';
import { loginByUsername } from '../model/services/loginByUsername';
import { useAppDispatch } from '#shared/hooks/reduxHooks'; // assuming this exists or I'll define standard dispatch
import { getLoginError } from '../model/selectors/getLoginError';
import { getLoginIsLoading } from '../model/selectors/getLoginIsLoading';

export interface LoginFormProps {
    className?: string;
    onSuccess?: () => void;
}

export const LoginForm: React.FC<LoginFormProps> = ({ className, onSuccess }) => {
    const dispatch = useAppDispatch();
    const { control, handleSubmit } = useForm<LoginByUsernameProps>();
    const error = useSelector(getLoginError);
    const isLoading = useSelector(getLoginIsLoading);

    const onSubmit = useCallback(async (data: LoginByUsernameProps) => {
        const result = await dispatch(loginByUsername(data));
        if (result.meta.requestStatus === 'fulfilled') {
            onSuccess?.();
        }
    }, [dispatch, onSuccess]);

    return (
        <View 
            component="form" 
            onSubmit={handleSubmit(onSubmit)}
            display="flex"
            flexDirection="column"
            gap={2}
            width="100%"
            maxWidth={400}
            className={className}
        >
            <Text variant="h5" align="center" gutterBottom>
                Вход в систему
            </Text>
            
            {error && (
                <Text color="error" align="center">
                    {error}
                </Text>
            )}

            <TextInput.Form
                control={control}
                name="username"
                label="Имя пользователя"
                autoFocus
            />

            <TextInput.Form
                control={control}
                name="password"
                label="Пароль"
                type="password"
            />

            <Button
                variant="contained"
                type="submit"
                fullWidth
                loading={isLoading}
            >
                Войти
            </Button>
        </View>
    );
};
