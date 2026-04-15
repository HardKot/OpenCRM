import { useNavigate, useParams } from "react-router-dom";
import { Modal } from "#shared/ui";
import { EmployeeForm } from "#features/form/employeeForm";
import { useHasPermission } from "#features/auth/AccessPermission";
import { UserPermission } from "#entities/User";

const EmployeeFormPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const employeeId = id ? Number.parseInt(id, 10) : undefined;
  const hasCreateEmployee = useHasPermission(UserPermission.EmployeeUpdate);
  const hasUpdateAccess = useHasPermission(UserPermission.EmployeeAccess);

  const handleClose = () => {
    navigate("/employee");
  };

  if (!hasCreateEmployee) return null;

  return (
    <Modal open={true} onClose={handleClose} maxWidth="md" fullWidth>
      <EmployeeForm
        employeeId={employeeId}
        onCancel={handleClose}
        userAccess={hasUpdateAccess}
      />
    </Modal>
  );
};

export { EmployeeFormPage };
