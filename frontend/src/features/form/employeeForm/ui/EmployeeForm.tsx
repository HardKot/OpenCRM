import { useSaveEmployeeForm, useGetEmployeeForm } from "#shared/api";
import { yupResolver } from "@hookform/resolvers/yup";
import {
  FormProvider,
  useForm,
  useFormContext,
  useWatch,
  useController,
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
} from "#shared/ui";
import { ChangeEvent, ReactNode } from "react";
import type { EmployeeDto } from "#shared/api";

interface EmployeeFormProps {
  employeeId?: number;
  onSave?: (data: EmployeeDto) => void;
  onCancel?: () => void;

  userAccess?: boolean;
}

const EmployeeForm = ({
  employeeId,
  onSave,
  onCancel,
  userAccess = false,
}: EmployeeFormProps) => {
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
  const isNeedTabs = userAccess;

  const MainComponents = (
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
  );

  const UserAccess = (
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
  );

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

        {!isNeedTabs && MainComponents}
        {isNeedTabs && (
          <Tabs
            tabs={[
              {
                label: t("employee.tabs.main"),
                Component: MainComponents,
              },
              {
                label: t("employee.tabs.security"),
                Component: UserAccess,
              },
            ]}
          />
        )}

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

      {isAccessAllowed && <EmployeePermissions />}
    </>
  );
};

const EmployeePermissions = () => {
  const { t } = useI18n();
  const { control } = useFormContext<IEmployeeForm>();
  const { field } = useController({ control, name: "permissions" });

  return (
    <Layout.Paper>
      <Text variant="subtitle1" sx={{ fontWeight: 600, mb: 2 }}>
        {t("employee.security.permissionsTitle")}
      </Text>

      <CheckBoxWithSubItems
        label={t("permission.EMPLOYEE")}
        list={["EMPLOYEE_READ", "EMPLOYEE_UPDATE", "EMPLOYEE_ACCESS"]}
        value={field.value}
        onChange={(newList) => field.onChange(newList)}
      />

      <CheckBoxWithSubItems
        label={t("permission.CLIENT")}
        list={[
          "CLIENT_READ",
          "CLIENT_UPDATE",
          "CLIENT_NAME_SHOW",
          "CLIENT_CONTACT_SHOW",
        ]}
        value={field.value}
        onChange={(newList) => field.onChange(newList)}
      />

      <CheckBoxWithSubItems
        label={t("permission.COMMODITY")}
        list={["COMMODITY_READ", "COMMODITY_UPDATE"]}
        value={field.value}
        onChange={(newList) => field.onChange(newList)}
      />

      <Checkbox
        label={t("permission.INVESTIGATION_LOG_READ")}
        checked={field.value.includes("INVESTIGATION_LOG_READ")}
        onChange={(e) =>
          onChangeField(
            e,
            field.onChange,
            field.value,
            "INVESTIGATION_LOG_READ",
          )
        }
      />
    </Layout.Paper>
  );
};

function onChangeField<T>(
  e: ChangeEvent<HTMLInputElement>,
  onChange: (value: T[]) => void,
  allItems: T[],
  value: T | T[],
) {
  if (Array.isArray(value)) {
    let accumulate = [...allItems];
    for (const item of value)
      onChangeField(
        e,
        (v) => {
          accumulate = v;
        },
        accumulate,
        item,
      );
    onChange(accumulate);
    return;
  }

  if (e.target.checked) return onChange([...allItems, value]);
  onChange(allItems.filter((v) => v !== value));
}

interface CheckBoxWithSubItemsProps {
  label: string;
  list: string[];
  onChange: (newList: string[]) => void;
  value: string[];
}

const CheckBoxWithSubItems = ({
  label,
  list,
  onChange,
  value,
}: CheckBoxWithSubItemsProps) => {
  const { t } = useI18n();

  const isChecked = list.every((item) => value.includes(item));
  const isIndeterminate =
    list.some((item) => value.includes(item)) && !isChecked;

  return (
    <View>
      <Checkbox
        label={label}
        checked={isChecked}
        indeterminate={isIndeterminate}
        onChange={(e) => onChangeField(e, onChange, value, list)}
      />
      <View sx={{ ml: 3, display: "flex", flexDirection: "column", gap: 1 }}>
        {list.map((it) => (
          <Checkbox
            key={it}
            label={t(`permission.${it}`)}
            checked={value.includes(it)}
            onChange={(e) => onChangeField(e, onChange, value, it)}
          />
        ))}
      </View>
    </View>
  );
};

export { EmployeeForm };
