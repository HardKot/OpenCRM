
import { useChangePassword, useGeneratePassword, useGetPasswordLevel } from "#shared/api"
import { useI18n, useTranslate } from "#shared/hooks";
import { View, Text, Button, Alert } from "#shared/ui"
import { yupResolver } from "@hookform/resolvers/yup";
import { useForm, useWatch } from "react-hook-form";
import { ChangePasswordForm, changePasswordSchema } from "../model/changePasswordSchema";
import { useEffect } from "react";
import { PasswordInput } from "#shared/ui/PasswordInput";
import { Adapter } from "#shared/index";

const PasswordLevelColor = {
    "WEAK": "error.main",
    "SIMPLE": "error.main",
    "MEDIUM": "warning.main",
    "HARD": "success.main",
}

const ErrorDictionary: Record<string, string> = {
    "Current password is incorrect": 'changePassword.invalidCredentials',
    "New password cannot be the same as the old password": 'changePassword.passwordsMatch',
    "Unknown Error": 'changePassword.unknownError',
    "Password is too weak": 'changePassword.passwordTooWeak',
}

const ChangePassword = () => {
    const { t } = useI18n();
    const [updatePassword, { isLoading, error, isError }] = useChangePassword();
    const { data: generatedPassword = "" } = useGeneratePassword();
    const [
        getPasswordLevel, 
        { data: passwordLevel, reset: resetPasswordLevel }
    ] = useGetPasswordLevel();

    const { control, handleSubmit, setValue, formState } = useForm({
        resolver: yupResolver(changePasswordSchema(t))
    });
    const newPassword = useWatch({ control, name: "newPassword", defaultValue: "" });


    const errorMessage = useTranslate(
        Adapter.getErrorMessage(error, 'changePassword.unknownError'),
        { 
            dict: ErrorDictionary, 
            defaultKey: 'changePassword.unknownError' 
        }
    );
   

    const onSubmit = async ({ password, newPassword, confirmPassword }: ChangePasswordForm) => {
        await updatePassword({
            password,
            newPassword,
            confirmPassword,
        }).unwrap();
    }

    const onSetGeneratedPassword = () => {
        setValue("newPassword", generatedPassword);
        setValue("confirmPassword", generatedPassword);
    }

    useEffect(() => {
        resetPasswordLevel();
        let timer: number | undefined;
        
        if (newPassword.length > 3) {
            timer = setTimeout(() => {
                getPasswordLevel(newPassword);
            }, 500)
        }


        return () => {
            clearTimeout(timer);
        }
    }, [newPassword, resetPasswordLevel])


    return (
        <View
            component={"form"}
            onSubmit={handleSubmit(onSubmit)}
            display={"flex"}
            flexDirection={"column"}
            gap={2}
            width={"100%"}
            maxWidth={560}
            mx="auto"
        >
            <View>
                <Text variant="h6" color="text.primary">
                    {t("changePassword.changePassword")}
                </Text>
            </View>

            <PasswordInput.Form 
                control={control}
                name="password"
                label={t("changePassword.currentPassword")}
                
            />

            {
                !!generatedPassword && (
                    <Button.Text
                        onClick={onSetGeneratedPassword}
                        sx={{
                            display: "flex",
                            justifyContent: "flex-start",
                            textTransform: "none",
                            px: 2,
                            py: 1.5,
                            bgcolor: "action.hover",
                            borderRadius: 1,
                        }}
                    >
                        <View>
                            <Text variant="body2" color="text.secondary">
                                {t("changePassword.generatedPassword")}
                            </Text>
                            <Text variant="body1" color="text.primary" fontWeight={600}>
                                {generatedPassword}
                            </Text>
                        </View>
                    </Button.Text>
                )
            }

            <PasswordInput.Form
                control={control}
                name="newPassword"
                label={t("changePassword.newPassword")}                
            />

            {!!passwordLevel && (
                <Text color={PasswordLevelColor[passwordLevel]}>
                    {t(`changePassword.passwordLevel.${passwordLevel}`)}
                </Text>
            )}

            <PasswordInput.Form
                control={control}
                name="confirmPassword"
                label={t("changePassword.confirmNewPassword")}
            />

            {isError && (
                <Text color="error.main">
                    {errorMessage}
                </Text>
            )}

            <View display="flex" justifyContent={{ xs: "stretch", sm: "flex-end" }}>
                <Button
                    type="submit"
                    disabled={isLoading}
                    fullWidth={false}
                    loading={isLoading}
                    sx={{ width: { xs: "100%", sm: "auto" }, minWidth: { sm: 180 } }}
                >
                    {t("changePassword.submit")}
                </Button>
            </View>

            {formState.isSubmitSuccessful && (
                <Alert.Success message={t("changePassword.passwordChangeSuccess")}/>
            )}
        </View>
    )
}

export { ChangePassword }