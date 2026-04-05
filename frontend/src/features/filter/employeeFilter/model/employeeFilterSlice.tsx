import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { IEmployeeFilter } from "./employeeFilterSchema"

const EmployeeFilterSlice = createSlice({
    name: "filter/employee",
    initialState: {
        fullnameLike: "",
        position: ""
    },
    reducers: {
        setEmployeeFilter: (state, action: PayloadAction<IEmployeeFilter>) => {
            state.fullnameLike = action.payload.fullnameLike;
            state.position = action.payload.position;
        },
        dropEmployeeFilter: (state) => {
            state.fullnameLike = "";
            state.position = "";
        }
    }
});


const EmployeeFilterReducer = EmployeeFilterSlice.reducer;
const { setEmployeeFilter, dropEmployeeFilter } = EmployeeFilterSlice.actions;

export {
    EmployeeFilterReducer,
    setEmployeeFilter,
    dropEmployeeFilter
}