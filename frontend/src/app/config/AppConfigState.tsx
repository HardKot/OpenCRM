interface AppConfigState {
    theme: "light" | "dark" | "system";
    language: 'system' | 'en' | 'ru';
    host: string;
}



export type { AppConfigState }