import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import type { UserState } from './UserState';
import { authApi } from '#shared/index';
import { UserRole } from './UserRole';
import { isUserPermission, isUserRole } from '../libs/typeGuards';
import { UserPermission } from './UserPermission';

const initialState: UserState = {
    isAuth: false,
    authData: null,
    entity: null,
};

export const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {},

    extraReducers: (builder) => {
        builder
            .addMatcher(
                authApi.endpoints.loginByUsername.matchFulfilled,
                (state, action) => {
                    const authData = {
                        username: action.meta.arg.originalArgs.email,
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
            .addMatcher(
                authApi.endpoints.holdSession.matchFulfilled,
                (state, action) => {
                    state.isAuth = true;
                    state.authData = {
                        username: action.payload.entity.email,
                        userId: action.payload.userId,
                        tenantId: action.payload.tenantId,
                        entityId: action.payload.entity.id,
                        role: UserRole.Employee,
                        permissions: new Array<UserPermission>(),
                    };
                    state.entity = action.payload.entity;
                }
            )
            .addMatcher(
                authApi.endpoints.holdSession.matchRejected,
                (state) => {
                    state.authData = null;
                    state.isAuth = false;
                    state.entity = null;
                }
            )
    }
});

export const { actions: userActions } = userSlice;
export const { reducer: userReducer } = userSlice;
