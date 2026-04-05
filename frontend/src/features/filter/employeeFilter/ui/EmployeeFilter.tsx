import { useAppDispatch, useI18n } from "#shared/hooks"
import { yupResolver } from "@hookform/resolvers/yup";
import { useForm } from "react-hook-form";
import { EmployeeFilterSchema } from "../model/employeeFilterSchema";
import { TextInput, View } from "#shared/ui";
import { Button } from "@mui/material";
import { dropEmployeeFilter, setEmployeeFilter } from "../model/employeeFilterSlice";

const EmployeeFilter = () => {
    const {t} = useI18n();
    const appDispatch = useAppDispatch();

    const { control, handleSubmit, reset } = useForm({
        resolver: yupResolver(EmployeeFilterSchema)
    });

    const resetFilter = () => {
        reset();
        appDispatch(dropEmployeeFilter());
    }

    return (
        <View
            sx={{ display: "flex", flexDirection: "column", gap: 2 }}
            component={"form"}
            onSubmit={handleSubmit(data => appDispatch(setEmployeeFilter(data)))}
        >
            <View sx={{ display: "flex", flexDirection: "row", gap: 2, flexWrap: "wrap" }}>
                <TextInput.Form sx={{width: 300}} control={control} name="fullnameLike" label={t("employee.fields.fullname")} />
                <TextInput.Form sx={{width: 300}} control={control} name="position" label={t("employee.fields.position")} />
            </View>
            
            <View sx={{ display: "flex", flexDirection: "row", gap: 2, alignSelf: "flex-start" }}>
                <Button variant="contained" color="primary" type="submit">
                    {t("common.actions.search")}
                </Button>

                <Button variant="outlined" color="secondary" onClick={resetFilter}>
                    {t("common.actions.cancel")}
                </Button>
            </View>
        </View>
    )
}

export { EmployeeFilter }