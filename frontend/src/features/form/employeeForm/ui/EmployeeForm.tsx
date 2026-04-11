import { useEmployeeById } from "#shared/api";
import { yupResolver } from "@hookform/resolvers/yup";
import { FormProvider, useForm, useFormContext } from "react-hook-form";
import { EmployeeFormSchema, IEmployeeForm } from "../model/EmployeeFormSchema";
import { useI18n } from "#shared/hooks";
import { Button, Layout, Text, TextInput, View } from "#shared/ui";
import { EmployeeDto, useSaveEmployee } from "#shared/api/employeeApi";
import { ReactNode } from "react";

interface EmployeeFormProps {
  employeeId?: number;
  onSave?: (data: EmployeeDto) => void;
  onCancel?: () => void;
}

const EmployeeForm = ({ employeeId, onSave, onCancel }: EmployeeFormProps) => {
  const { t } = useI18n();
  const [getTrigger, { data: employeeData }] = useEmployeeById();
  const [saveTrigger, { isLoading: isSaving }] = useSaveEmployee();
  const schema = EmployeeFormSchema(t);

  const form = useForm({
    defaultValues: async () => {
      let form: IEmployeeForm = schema.getDefault();
      if (employeeId) {
        const data = await getTrigger(employeeId).unwrap();
        form = data;
      }

      return form;
    },
    resolver: yupResolver(schema),
  });

  const handleSubmit = form.handleSubmit(async (data) => {
    const dto: EmployeeDto = {
      id: employeeId,
      isDeleted: false,
      ...employeeData,
      ...data,
    };
    await saveTrigger(dto).unwrap();
    onSave?.(dto);
  });

  const handleCancel = () => {
    form.reset();
    onCancel?.();
  };

  return (
    <FormProvider {...form}>
      <View
        component="form"
        onSubmit={handleSubmit}
        sx={{
          display: "flex",
          flexDirection: "column",
          gap: 2,
          width: { xs: "100%", sm: 620 },
          maxWidth: "100%",
          py: 0.5,
        }}
      >
        <Text variant="h6" sx={{ fontWeight: 600 }}>
          {employeeId ? t("employee.edit") : t("employee.create")}
        </Text>

        <PersonalInformation />
        <ContactInformation />
        <StaffInformation />

        <View
          sx={{
            display: "flex",
            gap: 1,
            justifyContent: "flex-end",
            mt: 1,
          }}
        >
          <Button
            variant="outlined"
            color="inherit"
            type="button"
            onClick={handleCancel}
            disabled={isSaving || form.formState.isLoading}
          >
            {t("common.actions.cancel")}
          </Button>
          <Button
            variant="contained"
            color="primary"
            type="submit"
            loading={isSaving}
            disabled={form.formState.isLoading}
          >
            {t("common.actions.confirm")}
          </Button>
        </View>
      </View>
    </FormProvider>
  );
};

interface FormSectionProps {
  title: string;
  children: ReactNode;
}

const FormSection = ({ title, children }: FormSectionProps) => {
  return (
    <Layout.Paper>
      <Text variant="subtitle1" sx={{ fontWeight: 600, mb: 2 }}>
        {title}
      </Text>
      <View
        sx={{
          display: "grid",
          gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" },
          gap: 2,
        }}
      >
        {children}
      </View>
    </Layout.Paper>
  );
};

const PersonalInformation = () => {
  const { t } = useI18n();
  const methods = useFormContext<IEmployeeForm>();
  const isLoading = methods.formState.isLoading;

  return (
    <FormSection title={t("employee.fields.name")}>
      <TextInput.Form
        control={methods.control}
        name="firstname"
        label={t("employee.fields.firstname")}
        disabled={isLoading}
      />

      <TextInput.Form
        control={methods.control}
        name="lastname"
        label={t("employee.fields.lastname")}
        disabled={isLoading}
      />

      <TextInput.Form
        sx={{ gridColumn: { sm: "1 / -1" } }}
        control={methods.control}
        name="patronymic"
        label={t("employee.fields.patronymic")}
        disabled={isLoading}
      />
    </FormSection>
  );
};

const ContactInformation = () => {
  const { t } = useI18n();
  const methods = useFormContext<IEmployeeForm>();
  const isLoading = methods.formState.isLoading;

  return (
    <FormSection title={t("employee.fields.email")}>
      <TextInput.Form
        control={methods.control}
        name="email"
        label={t("employee.fields.email")}
        type="email"
        disabled={isLoading}
      />

      <TextInput.Form
        control={methods.control}
        name="phone"
        label={t("employee.fields.phone")}
        disabled={isLoading}
      />
    </FormSection>
  );
};

const StaffInformation = () => {
  const { t } = useI18n();
  const methods = useFormContext<IEmployeeForm>();
  const isLoading = methods.formState.isLoading;

  return (
    <FormSection title={t("employee.fields.position")}>
      <TextInput.Form
        sx={{ gridColumn: { sm: "1 / -1" } }}
        control={methods.control}
        name="position"
        label={t("employee.fields.position")}
        disabled={isLoading}
      />
    </FormSection>
  );
};

export { EmployeeForm };
