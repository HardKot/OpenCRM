import { authApi } from "./authApi";
import { employeeApi } from "./employeeApi";
import { investigationLogApi } from "./investigationLogApi";

const ApiReducer = {
  [authApi.reducerPath]: authApi.reducer,
  [employeeApi.reducerPath]: employeeApi.reducer,
  [investigationLogApi.reducerPath]: investigationLogApi.reducer,
};

const ApiMiddleware = [
  authApi.middleware,
  employeeApi.middleware,
  investigationLogApi.middleware,
];

export { ApiReducer, ApiMiddleware };
