import { combineReducers, configureStore } from '@reduxjs/toolkit'
import { userReducer } from '#entities/User';
import { ApiMiddleware, ApiReducer } from '#shared/api';
import { AppConfigReducer } from './config/AppConfigSlice';
import { persistStore, persistReducer, FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER } from 'redux-persist'
import storage from 'redux-persist/lib/storage'

const persistConfig = {
  key: 'root',
  version: 1,
  storage: storage,
}

const appReducer = combineReducers({
  user: userReducer,
  appConfig: AppConfigReducer,
  ...ApiReducer
})


const persistedReducer = persistReducer(persistConfig, appReducer)


export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) => getDefaultMiddleware({ 
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      }
    }).concat(ApiMiddleware),
  enhancers: (getDefaultEnhancers) => getDefaultEnhancers(),
  devTools: import.meta.env.DEV,
})

export const persistor = persistStore(store);

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
export type AppStore = typeof store

export interface ThunkExtraArg {
    api: any; // Ideally AxiosInstance
    navigate?: (to: string) => void;
}
