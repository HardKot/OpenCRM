import { useNavigate, useParams } from "react-router-dom";
import { Modal } from "#shared/ui";
import { EmployeeForm } from "#features/form/employeeForm";

const EmployeeFormPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const employeeId = id ? Number.parseInt(id, 10) : undefined;

  const handleClose = () => {
    navigate("/employee");
  };

  return (
    <Modal open={true} onClose={handleClose}>
      <EmployeeForm employeeId={employeeId} onCancel={handleClose} />
    </Modal>
  );
};

export { EmployeeFormPage };
