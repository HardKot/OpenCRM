import { useCallback, useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { Button, Text, TextInput, View } from '#shared/ui';
import { LoginSchema, loginSchema } from '../model/loginSchema';
import { Visibility, VisibilityOff } from '@mui/icons-material';
import { useLoginByUsername, useI18n, Utils, ITranslation,  } from '#shared/index';
import { IconButton, InputAdornment } from '@mui/material';

export interface LoginFormProps {
    onSuccess?: () => void;
}

const ErrorDictionary: Record<string, string> = {
    "Invalid email or password": 'authByUsername.invalidCredentials',
    "Unknown Error": 'authByUsername.unknownError',
}

const getErrorMessage = (error: any, t: ITranslation) => {
    let errorKey = "";
    if (Utils.isIError(error)) errorKey = error.error;

    if (!errorKey) errorKey = 'authByUsername.unknownError';
    const translationKey = ErrorDictionary[errorKey] ?? 'authByUsername.unknownError';
    return t(translationKey, { defaultValue: t('authByUsername.unknownError') });
}

export const LoginForm = ({ onSuccess }: LoginFormProps) => {
    const { t } = useI18n();
    const [loginByUsername, { isError, error }] = useLoginByUsername();

    const [isShowPassword, setIsShowPassword] = useState(false);
    const { control, handleSubmit, formState } = useForm({
        resolver: yupResolver(loginSchema(t)),
        mode: 'onBlur',
    });


    const errorMessage = getErrorMessage(error, t);
   
  
    const onSubmit = useCallback(async ({ username, password }: LoginSchema) => {
        await loginByUsername({
            username,
            password,
        });
        onSuccess?.();
    }, [onSuccess]);


    return (
        <View 
            component="form" 
            onSubmit={handleSubmit(onSubmit)}
            display="flex"
            flexDirection="column"
            gap={2}
            width="100%"
            maxWidth={400}
        >
            <Text variant="h5" align="center" gutterBottom>
                {t('authByUsername.title')}
            </Text>
            

            <TextInput.Form
                control={control}
                name="username"
                label={t('authByUsername.username')}
                autoFocus
                
            />

            <TextInput.Form
                control={control}
                name="password"
                label={t('authByUsername.password')}
                type={isShowPassword ? "text" : "password" }
                right={<Button.Icon
                    onClick={() => setIsShowPassword(!isShowPassword)}
                    icon={isShowPassword ? "VisibilityOff" : "Visibility"}
                    color='inherit'
                />}
            />

            {
                isError && (
                    <Text color="error" align="center">
                        {errorMessage}
                    </Text>
                )
            }

            <Button
                variant="contained"
                type="submit"
                fullWidth
                loading={formState.isSubmitting}
            >
                {t('authByUsername.submit')}
            </Button>
        </View>
    );
};
