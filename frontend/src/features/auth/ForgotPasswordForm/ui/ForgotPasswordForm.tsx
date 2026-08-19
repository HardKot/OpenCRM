import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { Button, Text, TextInput, View } from "#shared/ui";
import {
  ForgotPasswordSchema,
  forgotPasswordSchema,
} from "../model/ForgotPasswordSchema";
import { useI18n, useForgotPassword } from "#shared/index";

interface ForgotPasswordFormProps {
  onSuccess?: () => void;
}

export const ForgotPasswordForm = ({ onSuccess }: ForgotPasswordFormProps) => {
  const { t } = useI18n();
  const [loginByUsername, { isError }] = useForgotPassword();

  const { control, handleSubmit, formState } = useForm({
    resolver: yupResolver(forgotPasswordSchema(t)),
  });

  const onSubmit = async ({ username }: ForgotPasswordSchema) => {
    await loginByUsername({
      email: username,
    }).unwrap();

    onSuccess?.();
  };

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
        {t("forgotPassword.title")}
      </Text>

      <TextInput.Form
        control={control}
        name="username"
        label={t("forgotPassword.username")}
        autoFocus
      />

      <View
        minHeight={22}
        mt={-1}
        mb={-0.5}
        display="flex"
        alignItems="center"
        justifyContent="center"
      >
        <Text
          variant="caption"
          color="error"
          align="center"
          visibility={isError ? "visible" : "hidden"}
          lineHeight={1.1}
          width="100%"
          sx={{ overflowWrap: "anywhere", wordBreak: "break-word" }}
        >
          {isError ? t("forgotPassword.error") : " "}
        </Text>
      </View>

      <Button
        variant="contained"
        type="submit"
        fullWidth
        loading={formState.isSubmitting}
      >
        {t("forgotPassword.submit")}
      </Button>
    </View>
  );
};
