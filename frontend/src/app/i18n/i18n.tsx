import { I18n } from "i18n-js";

import ruRU from "./ru.json";
import { store } from "#app/store";


const vocabularies = {
    ru: ruRU,
};


const i18n = new I18n(vocabularies);


i18n.locale = "ru";
i18n.defaultLocale = "ru";
i18n.enableFallback = true;

// store.subscribe(() => {
//     const state = store.getState();
//     let language = state.appConfig.language;
//     if (language === "system") {
//         const systemLanguage = navigator.language;
//         if (systemLanguage in vocabularies) {
//             language = systemLanguage;
//         } else {
//             language = i18n.defaultLocale;
//         }
//     }

//     i18n.locale = language;
// });


export { i18n }
