import { AppConfigState } from "#app/config/AppConfigState";
import { createAction } from "@reduxjs/toolkit";

const setTheme = createAction<AppConfigState["theme"]>("appConfig/setTheme");
const setLanguage = createAction<AppConfigState["language"]>("appConfig/setLanguage");
const setHost = createAction<AppConfigState["host"]>("appConfig/setHost");

export { setTheme, setLanguage, setHost }