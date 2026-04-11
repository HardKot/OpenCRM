import { FC, useState } from "react";
import { ChangeLanguage, ChangeTheme } from "#features/AppSettings";
import { View, Text } from "#shared/ui";
import { useI18n } from "#shared/hooks";
import { AuthPageMode } from "../model/AuthPageMode";
import { Login } from "./Login";
import { ForgoutPassword } from "./ForgoutPassword";
import { CreateTenant } from "./CreateTenant";
import { ModeSwitcher } from "./ModeSwitcher";

const ViewModes: { [key in AuthPageMode]: FC<{}> } = {
  [AuthPageMode.LOGIN]: Login,
  [AuthPageMode.FORGOT_PASSWORD]: ForgoutPassword,
  [AuthPageMode.CREATE_TENANT]: CreateTenant,
};

const AuthPage = () => {
  const [mode, setMode] = useState<AuthPageMode>(AuthPageMode.LOGIN);

  const { t } = useI18n();

  const ViewMode = ViewModes[mode] ?? Login;

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
        flexDirection={{ xs: "column", sm: "row" }}
        maxWidth={{ xs: "90vw", sm: "400px" }}
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
        width={{ xs: "calc(100% - 32px)", sm: 440 }}
        maxWidth={440}
        justifyItems={"center"}
      >
        <Text variant="h4" align="center" gutterBottom color="primary">
          {t("application.name")}
        </Text>

        <ViewMode />

        <ModeSwitcher mode={mode} onModeChange={setMode} />
      </View>
    </View>
  );
};

export { AuthPage };
