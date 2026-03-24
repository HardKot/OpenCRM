import { createAsyncThunk } from '@reduxjs/toolkit';
import type { User } from '#entities/User';
import { userActions } from '#entities/User';
import { $api } from '#shared/api';
import { LoginByUsernameProps } from '../types/loginSchema';
import { StateSchema, ThunkExtraArg } from '#app/store'; // Assuming store types are here

export const loginByUsername = createAsyncThunk<User, LoginByUsernameProps, { rejectValue: string }>(
    'login/loginByUsername',
    async (authData, thunkAPI) => {
        const { extra, rejectWithValue, dispatch } = thunkAPI;
        try {
            const response = await $api.post<User>('/login', authData);

            if (!response.data) {
                throw new Error();
            }

            localStorage.setItem('user', JSON.stringify(response.data));
            dispatch(userActions.setAuthData(response.data));

            return response.data;
        } catch (e) {
            console.log(e);
            return rejectWithValue('error');
        }
    },
);
