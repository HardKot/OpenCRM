import { ChangeLanguage, ChangeTheme } from "#features/AppSettings";
import { ChangePassword } from "#features/ChangePassword/ui/ChangePassword";
import { useI18n } from "#shared/hooks";
import { Layout, Text, View } from "#shared/ui";

const Settings = () => {
  const { t } = useI18n();

  return (
    <View bgcolor="background.default" minHeight="calc(100vh - 64px)">
      <View
        maxWidth={1100}
        mx="auto"
        px={{ xs: 2, sm: 3, md: 4 }}
        py={{ xs: 2, sm: 3 }}
        display="flex"
        flexDirection="column"
        gap={3}
      >
        <Layout.Paper padding={2}>
          <Text variant="h4" color="text.primary" gutterBottom>
            {t("application.shortName")} · {t("settings.personalization")}
          </Text>
          <Text variant="body1" color="text.secondary">
            {t("settings.theme_description")}
          </Text>
        </Layout.Paper>

        <View
          display="grid"
          gridTemplateColumns={{ xs: "1fr", md: "repeat(2, minmax(0, 1fr))" }}
          gap={3}
        >
          <Layout.Paper padding={2} minHeight={200}>
            <Text variant="h6" color="text.primary" sx={{ mb: 2 }}>
              {t("settings.language")}
            </Text>
            <ChangeLanguage />
          </Layout.Paper>

          <Layout.Paper padding={2} minHeight={200}>
            <Text variant="h6" color="text.primary" sx={{ mb: 2 }}>
              {t("settings.theme")}
            </Text>
            <ChangeTheme />
          </Layout.Paper>

          <Layout.ItemPaper padding={2} column={"1 / -1"}>
            <Text variant="h6" color="text.primary" sx={{ mb: 2 }}>
              {t("settings.security")}
            </Text>
            <ChangePassword />
          </Layout.ItemPaper>
        </View>
      </View>
    </View>
  );
};

export { Settings };
