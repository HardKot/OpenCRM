import { useEmployeeById } from "#shared/api";
import { yupResolver } from "@hookform/resolvers/yup";
import { FormProvider, useForm, useFormContext } from "react-hook-form";
import { EmployeeFormSchema, IEmployeeForm } from "../model/EmployeeFormSchema";
import { useI18n } from "#shared/hooks";
import { TextInput, View } from "#shared/ui";

interface EmployeeFormProps {
  employeeId?: number;
}

const EmployeeForm = ({ employeeId }: EmployeeFormProps) => {
  const { t } = useI18n();
  const [trigger] = useEmployeeById();
  const schema = EmployeeFormSchema(t);

  const form = useForm({
    defaultValues: async () => {
      let form: IEmployeeForm = schema.getDefault();
      if (employeeId) {
        const data = await trigger(employeeId).unwrap();
        form = data;
      }

      return form;
    },
    resolver: yupResolver(schema),
  });

  return (
    <FormProvider {...form}>
      <PersonalInformation />
      <ContactInformation />
      <StaffInformation />
    </FormProvider>
  );
};

const PersonalInformation = () => {
  const { t } = useI18n();
  const methods = useFormContext<IEmployeeForm>();

  return (
    <View>
      <TextInput.Form
        control={methods.control}
        name="firstname"
        label={t("employee.fields.firstname")}
      />

      <TextInput.Form
        control={methods.control}
        name="lastname"
        label={t("employee.fields.lastname")}
      />

      <TextInput.Form
        control={methods.control}
        name="patronymic"
        label={t("employee.fields.patronymic")}
      />
    </View>
  );
};

const ContactInformation = () => {
  const { t } = useI18n();
  const methods = useFormContext<IEmployeeForm>();

  return (
    <View>
      <TextInput.Form
        control={methods.control}
        name="email"
        label={t("employee.fields.email")}
      />

      <TextInput.Form
        control={methods.control}
        name="phone"
        label={t("employee.fields.phone")}
      />
    </View>
  );
};

const StaffInformation = () => {
  const { t } = useI18n();
  const methods = useFormContext<IEmployeeForm>();

  return (
    <View>
      <TextInput.Form
        control={methods.control}
        name="position"
        label={t("employee.fields.position")}
      />
    </View>
  );
};
