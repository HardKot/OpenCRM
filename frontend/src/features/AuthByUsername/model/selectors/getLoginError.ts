import { createSelector } from '@reduxjs/toolkit';
import { getLoginState } from './getLoginState';

export const getLoginError = createSelector(
    getLoginState,
    (login) => login?.error,
);
