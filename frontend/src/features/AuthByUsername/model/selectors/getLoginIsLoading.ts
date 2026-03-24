import { createSelector } from '@reduxjs/toolkit';
import { getLoginState } from './getLoginState';

export const getLoginIsLoading = createSelector(
    getLoginState,
    (login) => login?.isLoading || false,
);
