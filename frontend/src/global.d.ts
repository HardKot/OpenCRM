import { I18n } from "i18n-js";
import type {
  AppStore,
  RootState as ReduxRootState,
  AppDispatch as ReduxAppDispatch,
} from "./app/store";

declare global {
  // eslint-disable-next-line no-unused-vars
  interface Window {
    app: {
      i18n: I18n;
      store: AppStore;
    };
  }

  // eslint-disable-next-line no-unused-vars
  type RootState = ReduxRootState;
  // eslint-disable-next-line no-unused-vars
  type AppDispatch = ReduxAppDispatch;
}

export {};
