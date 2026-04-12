import {
  EmployeeDto,
  useDeleteEmployee,
  useRestoreEmployee,
} from "#shared/api";
import { Button, Loading } from "#shared/ui";

interface EmployeeActionProps {
  data: EmployeeDto;
}

const EmployeeAction = ({ data }: EmployeeActionProps) => {
  const [deleteTrigger, deleteState] = useDeleteEmployee();
  const [restoreTrigger, restoreState] = useRestoreEmployee();

  const isLoading = deleteState.isLoading || restoreState.isLoading;

  const toggle = () => {
    if (data.isDeleted) {
      return restoreTrigger(data.id);
    }
    return deleteTrigger(data.id);
  };

  if (isLoading) return <Loading />;

  return (
    <Button.Icon
      icon={data.isDeleted ? "Restore" : "Delete"}
      onClick={() => toggle()}
    />
  );
};

export { EmployeeAction };
