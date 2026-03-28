import { useI18n, useRegisterTenant, Adapter, useTranslate } from "#shared/index";
import { View, Text, TextInput, Button, Alert } from "#shared/ui";
import { yupResolver } from "@hookform/resolvers/yup";
import { useForm } from 'react-hook-form';
import { createTenantSchema, CreateTenantSchema } from "../model/CreateTenantSchema";
import { useCallback } from "react";

interface CreateTenantFormProps {
    onSuccess?: () => void;
}


const ErrorDictionary: Record<string, string> = {
    "Unknown Error": 'createTenant.unknownError',
}

const CreateTenantForm = ({ onSuccess }: CreateTenantFormProps) => {
    const { t } = useI18n();
    
    const [registerTenant, { isError, error }] = useRegisterTenant()


    const { control, handleSubmit, formState } = useForm({ 
        resolver: yupResolver(createTenantSchema(t)),
    });

    const errorMessage = useTranslate(
        Adapter.getErrorMessage(error, 'createTenant.unknownError'),
        { 
            dict: ErrorDictionary, 
            defaultKey: 'createTenant.unknownError' 
        }
    );

    const onSubmit = useCallback(async ({ email }: CreateTenantSchema) => {
        registerTenant({
            email
        }).unwrap();
        onSuccess?.();
    }, [ onSuccess ]);

    return (
        <View
            component={"form"}
            onSubmit={handleSubmit(onSubmit)}
            display="flex"
            flexDirection={"column"}
            alignItems={"center"}
            gap={2}
            width="100%"
            maxWidth={400}
        >
            <Text variant="h5" align="center" gutterBottom color="text.primary">
                {t("createTenant.title")}
            </Text>


            <TextInput.Form 
                control={control}
                name="email"
                label={t("createTenant.email")}
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
                    {isError ? errorMessage : ' '}
                </Text>
            </View>

            <Button
                variant="contained"
                type="submit"
                fullWidth
                loading={formState.isSubmitting}
            >
                {t('createTenant.submit')}
            </Button>
            {formState.isSubmitSuccessful && (
                <Alert.Success message={t('createTenant.success')} />
            )}
        </View>
    )
}

export { CreateTenantForm }