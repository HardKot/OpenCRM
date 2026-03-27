import { authApi } from "./authApi";

const ApiReducer = {
    [authApi.reducerPath]: authApi.reducer
}

const ApiMiddleware = [authApi.middleware]

export {
    ApiReducer,
    ApiMiddleware
}