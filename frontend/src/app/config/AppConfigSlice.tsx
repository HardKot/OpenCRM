import { setHost, setLanguage, setTheme } from "#shared/index";
import { createReducer } from "@reduxjs/toolkit";
import { AppConfigState } from "./AppConfigState";

const initialState: AppConfigState = {
    theme: "system",
    language: "system",
    host: (import.meta as any).env.VITE_API_URL || 'http://localhost:8080'
}


const AppConfigReducer = createReducer(
    initialState, 
    (builder) => {
        builder
            .addCase(setTheme, (state, action) => {
                state.theme = action.payload;
            })
            .addCase(setLanguage, (state, action) => {
                state.language = action.payload;
            })
            .addCase(setHost, (state, action) => {
                state.host = action.payload;
            })
    }
);

export { AppConfigReducer }
