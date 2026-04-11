import type { RootState } from "#app/store";

export const userIsAuthSelector = (state: RootState) => state.user.isAuth;

export const entitySelector = (state: RootState) => state.user.entity;
