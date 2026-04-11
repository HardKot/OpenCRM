import { I18n } from "i18n-js";

import ruRU from "./ru.json";
import enUS from "./en.json";
import { store } from "#app/store";
import { AppConfigState } from "#app/config/AppConfigState";

const vocabularies = {
  ru: ruRU,
  en: enUS,
};

const i18n = new I18n(vocabularies);

i18n.locale = "ru";
i18n.defaultLocale = "ru";
i18n.enableFallback = true;

store.subscribe(() => {
  const state = store.getState();
  let language: AppConfigState["language"] = state.appConfig.language;
  if (language === "system") {
    const systemLanguage = navigator.language.split(
      "-",
    )[0] as AppConfigState["language"];
    if (systemLanguage in vocabularies) {
      language = systemLanguage;
    } else {
      language = i18n.defaultLocale as AppConfigState["language"];
    }
  }

  if (i18n.locale !== language) {
    i18n.locale = language;
  }
});

export { i18n };
