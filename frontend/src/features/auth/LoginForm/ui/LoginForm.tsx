import { useState } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { Button, Text, TextInput, View } from "#shared/ui";
import { LoginSchema, loginSchema } from "../model/loginSchema";
import {
  useLoginByUsername,
  useI18n,
  useTranslate,
  Adapter,
} from "#shared/index";
import { useHoldSession } from "#shared/api/authApi";

export interface LoginFormProps {
  onSuccess?: () => void;
}

const ErrorDictionary: Record<string, string> = {
  "Invalid email or password": "authByUsername.invalidCredentials",
  "Unknown Error": "authByUsername.unknownError",
};

export const LoginForm = ({ onSuccess }: LoginFormProps) => {
  const { t } = useI18n();
  const [loginByUsername, { isError, error }] = useLoginByUsername();
  const [holdSession] = useHoldSession();

  const [isShowPassword, setIsShowPassword] = useState(false);
  const { control, handleSubmit, formState } = useForm({
    resolver: yupResolver(loginSchema(t)),
  });

  const errorMessage = useTranslate(
    Adapter.getErrorMessage(error, "authByUsername.unknownError"),
    {
      dict: ErrorDictionary,
      defaultKey: "authByUsername.unknownError",
    },
  );

  const onSubmit = async ({ username, password }: LoginSchema) => {
    await loginByUsername({
      email: username,
      password,
    }).unwrap();
    await holdSession();
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
        {t("authByUsername.title")}
      </Text>

      <TextInput.Form
        control={control}
        name="username"
        label={t("authByUsername.username")}
        autoFocus
      />

      <TextInput.Form
        control={control}
        name="password"
        label={t("authByUsername.password")}
        type={isShowPassword ? "text" : "password"}
        right={
          <Button.Icon
            icon={isShowPassword ? "VisibilityOff" : "Visibility"}
            onClick={() => setIsShowPassword(!isShowPassword)}
            size="small"
          />
        }
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
          {isError ? errorMessage : " "}
        </Text>
      </View>

      <Button
        variant="contained"
        type="submit"
        fullWidth
        loading={formState.isSubmitting}
      >
        {t("authByUsername.submit")}
      </Button>
    </View>
  );
};
