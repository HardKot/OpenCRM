import type { StateSchema } from '#app/store';

export const getUserAuthData = (state: StateSchema) => state.user.authData;
export const getUserInited = (state: StateSchema) => state.user._inited;
