import {
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
  Stack,
  FormLabel,
} from "@mui/material";
import { useAppDispatch, useAppSelector, setLanguage } from "#shared/index";
import { useI18n } from "#shared/hooks";

const ChangeLanguage = () => {
  const dispatch = useAppDispatch();
  const language = useAppSelector((state) => state.appConfig.language);
  const { t } = useI18n();

  const handleLanguageChange = (e: any) => {
    dispatch(setLanguage(e.target.value));
  };

  return (
    <Stack spacing={2}>
      <FormLabel sx={{ fontWeight: 600, mb: 1 }}>
        {t("settings.language")}
      </FormLabel>
      <FormControl fullWidth>
        <InputLabel>{t("settings.language")}</InputLabel>
        <Select
          value={language}
          label={t("settings.language")}
          onChange={handleLanguageChange}
        >
          <MenuItem value="system">{t("settings.language_system")}</MenuItem>
          <MenuItem value="ru">Русский</MenuItem>
          <MenuItem value="en">English</MenuItem>
        </Select>
        <FormHelperText>{t("settings.language_description")}</FormHelperText>
      </FormControl>
    </Stack>
  );
};

const ChangeLanguageCompact = () => {
  const dispatch = useAppDispatch();
  const language = useAppSelector((state) => state.appConfig.language);

  const handleLanguageChange = (e: any) => {
    dispatch(setLanguage(e.target.value));
  };

  return (
    <FormControl size="small" sx={{ minWidth: 60 }}>
      <Select
        value={language}
        onChange={handleLanguageChange}
        variant="standard"
        sx={{
          fontSize: "0.75rem",
          padding: "4px 4px",
          minHeight: 28,
        }}
      >
        <MenuItem value="system">AUTO</MenuItem>
        <MenuItem value="ru">РУ</MenuItem>
        <MenuItem value="en">EN</MenuItem>
      </Select>
    </FormControl>
  );
};

ChangeLanguage.Compact = ChangeLanguageCompact;

export { ChangeLanguage };
