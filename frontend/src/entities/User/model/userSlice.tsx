import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import type { UserState } from './UserState';
import type { User } from './User';
import { authApi } from '#shared/index';
import { UserRole } from './UserRole';
import { isUserPermission, isUserRole } from '../libs/typeGuards';
import { UserPermission } from './UserPermission';

const initialState: UserState = {
    isAuth: false,
    authData: null
};

export const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {
        setAuthData: (state, action: PayloadAction<User>) => {
            state.isAuth = true;
            state.authData = action.payload;
        },
       
        logout: (state) => {
            state.authData = null;
            state.isAuth = false;
        },
    },

    extraReducers: (builder) => {
        builder
            .addMatcher(
                authApi.endpoints.loginByUsername.matchFulfilled,
                (state, action) => {
                    const authData = {
                        username: action.meta.arg.originalArgs.username,
                        userId: action.payload.userId,
                        tenantId: action.payload.tenantId,
                        entityId: action.payload.entityId,
                        role: UserRole.Employee,
                        permissions: new Array<UserPermission>(),
                    };

                    if (isUserRole(action.payload.role)) authData.role = action.payload.role;
                    for (const permission of action.payload.permissions) {
                        if (isUserPermission(permission)) authData.permissions.push(permission);
                    }

                    state.isAuth = true;
                    state.authData = authData;
                }
            )
            .addMatcher(
                authApi.endpoints.logout.matchFulfilled,
                (state) => {
                    state.authData = null;
                    state.isAuth = false;
                }
            )


    }
});

export const { actions: userActions } = userSlice;
export const { reducer: userReducer } = userSlice;
