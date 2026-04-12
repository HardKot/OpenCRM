import { useAppDispatch, useI18n } from "#shared/hooks";
import { yupResolver } from "@hookform/resolvers/yup";
import { useForm } from "react-hook-form";
import { EmployeeFilterSchema } from "../model/employeeFilterSchema";
import { TextInput, View, SuggestInput, Button } from "#shared/ui";
import {
  dropEmployeeFilter,
  setEmployeeFilter,
} from "../model/employeeFilterSlice";
import { usePositionSuggest } from "../model/usePositionSuggest";

const EmployeeFilter = () => {
  const { t } = useI18n();
  const appDispatch = useAppDispatch();
  const { positions, isLoading, handleInputChange } = usePositionSuggest();

  const { control, handleSubmit, reset } = useForm({
    resolver: yupResolver(EmployeeFilterSchema),
  });

  const resetFilter = () => {
    reset();
    appDispatch(dropEmployeeFilter());
  };

  return (
    <View
      sx={{ display: "flex", flexDirection: "column", gap: 2 }}
      component={"form"}
      onSubmit={handleSubmit((data) => appDispatch(setEmployeeFilter(data)))}
    >
      <View sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
        <TextInput.Form
          sx={{ width: "100%" }}
          control={control}
          name="fullnameLike"
          label={t("employee.fields.fullname")}
        />
        <SuggestInput.Form
          control={control}
          name={"positionSuggest"}
          label={t("employee.fields.position")}
          options={positions}
          onInputChange={handleInputChange}
          loading={isLoading}
        />
        <TextInput.Form
          sx={{ width: "100%" }}
          control={control}
          name="email"
          label={t("employee.fields.email")}
        />
        <TextInput.Form
          sx={{ width: "100%" }}
          control={control}
          name="phoneLike"
          label={t("employee.fields.phone")}
          mask={Number}
        />
      </View>

      <View
        sx={{
          display: "flex",
          flexDirection: "row",
          gap: 2,
          alignSelf: "flex-start",
        }}
      >
        <Button variant="contained" color="primary" type="submit">
          {t("common.actions.search")}
        </Button>

        <Button variant="outlined" color={"inherit"} onClick={resetFilter}>
          {t("common.actions.cancel")}
        </Button>
      </View>
    </View>
  );
};

export { EmployeeFilter };
