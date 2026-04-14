import { useSaveEmployeeForm, useGetEmployeeForm } from "#shared/api";
import { yupResolver } from "@hookform/resolvers/yup";
import {
  FormProvider,
  useForm,
  useFormContext,
  Controller,
  useWatch,
} from "react-hook-form";
import { EmployeeFormSchema, IEmployeeForm } from "../model/EmployeeFormSchema";
import { useI18n } from "#shared/hooks";
import {
  Button,
  Layout,
  Text,
  TextInput,
  View,
  Tabs,
  Checkbox,
  TransferList,
} from "#shared/ui";
import { ReactNode } from "react";
import type { EmployeeDto } from "#shared/api";

interface EmployeeFormProps {
  employeeId?: number;
  onSave?: (data: EmployeeDto) => void;
  onCancel?: () => void;
}

const EmployeeForm = ({ employeeId, onSave, onCancel }: EmployeeFormProps) => {
  const { t } = useI18n();
  const [getTrigger, { data: employeeData }] = useGetEmployeeForm();
  const [saveTrigger, { isLoading: isSaving }] = useSaveEmployeeForm();

  const schema = EmployeeFormSchema(t);

  const form = useForm({
    defaultValues: async () => {
      let form: IEmployeeForm = schema.getDefault();
      if (employeeId) {
        const data = await getTrigger(employeeId).unwrap();
        form = { ...form, ...data };
      }

      return form;
    },
    resolver: yupResolver(schema),
  });

  const handleSubmit = form.handleSubmit(async (data) => {
    const result = await saveTrigger({
      ...data,
      isDeleted: employeeData?.isDeleted ?? false,
      id: employeeId,
    }).unwrap();

    form.reset({ ...data, ...result });
    onSave?.(result);
  });

  const handleCancel = () => {
    form.reset();
    onCancel?.();
  };

  const role = useWatch({ control: form.control, name: "role" });
  const isOwner = role === "ROLE_OWNER";

  return (
    <FormProvider {...form}>
      <View
        component="form"
        onSubmit={handleSubmit}
        sx={{
          display: "flex",
          flexDirection: "column",
          gap: 2,
          width: { xs: "100%", md: 800 },
          maxWidth: "100%",
          py: 0.5,
        }}
      >
        <Text variant="h6" sx={{ fontWeight: 600 }}>
          {employeeId ? t("employee.edit") : t("employee.create")}
        </Text>

        <Tabs
          tabs={[
            {
              label: t("employee.tabs.main"),
              Component: (
                <View
                  sx={{
                    display: "flex",
                    flexDirection: "column",
                    gap: 2,
                    pt: 1,
                  }}
                >
                  <PersonalInformation />
                  <ContactInformation />
                  <StaffInformation />
                </View>
              ),
            },
            {
              label: t("employee.tabs.security"),
              Component: (
                <View
                  sx={{
                    display: "flex",
                    flexDirection: "column",
                    gap: 2,
                    pt: 1,
                  }}
                >
                  <SecurityInformation />
                </View>
              ),
            },
          ]}
        />

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
            disabled={form.formState.isLoading || isOwner}
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

const SecurityInformation = () => {
  const { t } = useI18n();
  const methods = useFormContext<IEmployeeForm>();
  const isAccessAllowed = useWatch({
    control: methods.control,
    name: "isAccessAllowed",
  });
  const role = useWatch({ control: methods.control, name: "role" });
  const isOwner = role === "ROLE_OWNER";
  const isLoading = methods.formState.isLoading;

  const ALL_PERMISSIONS = [
    "EMPLOYEE_READ",
    "EMPLOYEE_UPDATE",
    "EMPLOYEE_ACCESS",
    "CLIENT_READ",
    "CLIENT_UPDATE",
    "CLIENT_NAME_SHOW",
    "CLIENT_CONTACT_SHOW",
    "INVESTIGATION_LOG_READ",
    "COMMODITY_READ",
    "COMMODITY_UPDATE",
  ];

  return (
    <>
      <Layout.Paper>
        <Checkbox.Form
          control={methods.control}
          name="isAccessAllowed"
          label={t("employee.security.allowAccess")}
          disabled={isLoading || isOwner}
        />
      </Layout.Paper>

      {isAccessAllowed && (
        <Layout.Paper sx={{ mt: 2 }}>
          <Text variant="subtitle1" sx={{ fontWeight: 600, mb: 2 }}>
            {t("employee.security.permissionsTitle")}
          </Text>
          <Controller
            name="permissions"
            control={methods.control}
            render={({ field }) => (
              <TransferList
                choices={ALL_PERMISSIONS}
                selectedChoices={field.value || []}
                onChange={field.onChange}
                leftTitle={t("employee.security.available")}
                rightTitle={t("employee.security.selected")}
                renderOption={(opt) => t(`permission.${opt}`)}
              />
            )}
          />
        </Layout.Paper>
      )}
    </>
  );
};

export { EmployeeForm };
