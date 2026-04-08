import { setLanguage, setTheme } from "#shared/index";
import { createReducer } from "@reduxjs/toolkit";
import { AppConfigState } from "./AppConfigState";

const initialState: AppConfigState = {
  theme: "system",
  language: "system",
};

const AppConfigReducer = createReducer(initialState, (builder) => {
  builder
    .addCase(setTheme, (state, action) => {
      state.theme = action.payload;
    })
    .addCase(setLanguage, (state, action) => {
      state.language = action.payload;
    });
});

export { AppConfigReducer };
