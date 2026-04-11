export type ITranslation = {
  (key: string, options?: { defaultValue?: string }): string;
};
