import { configureStore } from '@reduxjs/toolkit'
import { userReducer, UserSchema } from '#entities/User';
import { loginReducer, LoginSchema } from '#features/AuthByUsername';

export interface StateSchema {
    user: UserSchema;
    login: LoginSchema;
}

export const store = configureStore({
  reducer: {
    user: userReducer,
    login: loginReducer,
  },
  middleware: (getDefaultMiddleware) => getDefaultMiddleware(),
  enhancers: (getDefaultEnhancers) => getDefaultEnhancers(),
  devTools: import.meta.env.DEV,
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch

export interface ThunkExtraArg {
    api: any; // Ideally AxiosInstance
    navigate?: (to: string) => void;
}
