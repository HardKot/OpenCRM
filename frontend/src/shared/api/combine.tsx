import { authApi } from "./authApi";
import { employeeApi } from "./employeeApi";

const ApiReducer = {
    [authApi.reducerPath]: authApi.reducer,
    [employeeApi.reducerPath]: employeeApi.reducer,
}

const ApiMiddleware = [authApi.middleware, employeeApi.middleware]

export {
    ApiReducer,
    ApiMiddleware
}