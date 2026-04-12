import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { IEmployeeFilter } from "./employeeFilterSchema";

const EmployeeFilterSlice = createSlice({
  name: "filter/employee",
  initialState: {
    fullname: "",
    position: "",
    phone: "",
    email: "",
  },
  reducers: {
    setEmployeeFilter: (state, action: PayloadAction<IEmployeeFilter>) => {
      state.fullname = action.payload.fullnameLike;
      state.position = action.payload.positionSuggest;
      state.email = action.payload.email;
      state.phone = action.payload.phoneLike;
    },
    dropEmployeeFilter: (state) => {
      state.fullname = "";
      state.position = "";
      state.email = "";
      state.phone = "";
    },
  },
});

const EmployeeFilterReducer = EmployeeFilterSlice.reducer;
const { setEmployeeFilter, dropEmployeeFilter } = EmployeeFilterSlice.actions;

export { EmployeeFilterReducer, setEmployeeFilter, dropEmployeeFilter };
