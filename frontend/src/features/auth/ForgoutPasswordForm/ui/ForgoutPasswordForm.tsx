import { useCallback, useState } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { Button, Text, TextInput, View } from '#shared/ui';
import { ForgoutPasswordSchema, forgoutPasswordSchema } from '../model/ForgoutPasswordSchema';
import { useI18n, useForgoutPassword  } from '#shared/index';


interface ForgoutPasswordFormProps {
    onSuccess?: () => void;
}

export const ForgoutPasswordForm = ({ onSuccess }: ForgoutPasswordFormProps) => {
    const { t } = useI18n();
    const [loginByUsername, { isError }] = useForgoutPassword();

    const { control, handleSubmit, formState } = useForm({
        resolver: yupResolver(forgoutPasswordSchema(t)),
    });

  
    const onSubmit = useCallback(async ({ username }: ForgoutPasswordSchema) => {
        await loginByUsername({
            username,
        }).unwrap();

        onSuccess?.();
    }, []);


    return (
        <View 
            component="form" 
            onSubmit={handleSubmit(onSubmit)}
            display="flex"
            flexDirection="column"
            alignItems={"center"}
            gap={2}
            width="100%"
            maxWidth={400}
        >
            <Text variant="h5" align="center" gutterBottom color="text.primary">
                {t('forgoutPassword.title')}
            </Text>
            

            <TextInput.Form
                control={control}
                name="username"
                label={t('forgoutPassword.username')}
                autoFocus
                
            />

            <View minHeight={22} mt={-1} mb={-0.5} display="flex" alignItems="center" justifyContent="center">
                <Text
                    variant="caption"
                    color="error"
                    align="center"
                    visibility={isError ? 'visible' : 'hidden'}
                    lineHeight={1.1}
                    width="100%"
                    sx={{ overflowWrap: 'anywhere', wordBreak: 'break-word' }}
                >
                    {isError ? t('forgoutPassword.error') : ' '}
                </Text>
            </View>

            <Button
                variant="contained"
                type="submit"
                fullWidth
                loading={formState.isSubmitting}
            >
                {t('forgoutPassword.submit')}
            </Button>
        </View>
    );
};
