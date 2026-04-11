import { useI18n } from "#shared/index";
import { Button, View } from "#shared/ui";
import { PropsWithChildren } from "react";
import { AuthPageMode } from "../model/AuthPageMode";

interface ModeSwitcherProps {
  mode: AuthPageMode;
  onModeChange: (mode: AuthPageMode) => void;
}

interface ModeComponentProps {
  onModeChange: (mode: AuthPageMode) => void;
}

const ModeWrapper = ({ children }: PropsWithChildren) => (
  <View>{children}</View>
);

const ModeLogin = ({ onModeChange }: ModeComponentProps) => {
  const { t } = useI18n();
  return (
    <ModeWrapper>
      <Button onClick={() => onModeChange(AuthPageMode.FORGOT_PASSWORD)}>
        {t("auth.forgotPassword")}
      </Button>
      <Button onClick={() => onModeChange(AuthPageMode.CREATE_TENANT)}>
        {t("auth.createTenant")}
      </Button>
    </ModeWrapper>
  );
};

const ModeForgotPassword = ({ onModeChange }: ModeComponentProps) => {
  const { t } = useI18n();
  return (
    <ModeWrapper>
      <Button onClick={() => onModeChange(AuthPageMode.LOGIN)}>
        {t("auth.backToLogin")}
      </Button>
      <Button onClick={() => onModeChange(AuthPageMode.CREATE_TENANT)}>
        {t("auth.createTenant")}
      </Button>
    </ModeWrapper>
  );
};

const ModeCreateTenant = ({ onModeChange }: ModeComponentProps) => {
  const { t } = useI18n();
  return (
    <ModeWrapper>
      <Button onClick={() => onModeChange(AuthPageMode.LOGIN)}>
        {t("auth.backToLogin")}
      </Button>
      <Button onClick={() => onModeChange(AuthPageMode.FORGOT_PASSWORD)}>
        {t("auth.forgotPassword")}
      </Button>
    </ModeWrapper>
  );
};

const ModeSwitcher = ({ mode, onModeChange }: ModeSwitcherProps) => {
  if (mode === AuthPageMode.LOGIN) {
    return <ModeLogin onModeChange={onModeChange} />;
  }

  if (mode === AuthPageMode.FORGOT_PASSWORD) {
    return <ModeForgotPassword onModeChange={onModeChange} />;
  }

  if (mode === AuthPageMode.CREATE_TENANT) {
    return <ModeCreateTenant onModeChange={onModeChange} />;
  }

  return null;
};

export { ModeSwitcher };
