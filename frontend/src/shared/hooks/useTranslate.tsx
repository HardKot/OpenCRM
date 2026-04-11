import { useI18n } from "./appHooks";

interface IUseTranslateOptions {
  defaultKey?: string;
  dict?: Record<string, string>;
}

const useTranslate = (key: string, options?: IUseTranslateOptions) => {
  const { t } = useI18n();

  const translatonKey = options?.dict?.[key] ?? key;

  return t(translatonKey, {
    defaultValue: () => t(options?.defaultKey ?? key),
  });
};

export { useTranslate };
