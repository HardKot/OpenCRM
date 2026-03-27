import { configureStore } from '@reduxjs/toolkit'
import { userReducer } from '#entities/User';
import { ApiMiddleware, ApiReducer } from '#shared/api';
import { AppConfigReducer } from './config/AppConfigSlice';


export const store = configureStore({
  reducer: {
    user: userReducer,
    appConfig: AppConfigReducer,
    ...ApiReducer
  },
  middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(ApiMiddleware),
  enhancers: (getDefaultEnhancers) => getDefaultEnhancers(),
  devTools: import.meta.env.DEV,
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
export type AppStore = typeof store

export interface ThunkExtraArg {
    api: any; // Ideally AxiosInstance
    navigate?: (to: string) => void;
}
