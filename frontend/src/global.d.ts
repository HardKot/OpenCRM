import { I18n } from 'i18n-js';
import type { AppStore, RootState as ReduxRootState, AppDispatch as ReduxAppDispatch } from './app/store';

declare global {
  interface Window {
    app: {
      i18n: I18n;
      store: AppStore;
    };
  }

  type RootState = ReduxRootState;
  type AppDispatch = ReduxAppDispatch;
}

export {};
