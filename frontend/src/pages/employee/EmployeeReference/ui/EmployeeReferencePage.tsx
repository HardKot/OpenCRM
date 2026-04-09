import { EmployeeForm } from "#features/form/employeeForm";
import { useI18n } from "#shared/hooks";
import { Layout, Modal, Text, Button, View } from "#shared/ui";
import { EmployeeReference } from "#widgets/references/employeeRefence/ui/EmployeeReference";
import { useState } from "react";

const EmployeeReferencePage = () => {
  const [openModal, setOpenModal] = useState(false);
  const { t } = useI18n();

  return (
    <View sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
      <Layout.Paper>
        <Text variant="h6">{t("employee.title")}</Text>
        <Button
          variant="contained"
          color="primary"
          onClick={() => setOpenModal(true)}
        >
          {t("employee.add")}
        </Button>
      </Layout.Paper>
      <EmployeeReference filter actions isDeleted />
      <Modal open={openModal} onClose={() => setOpenModal(false)}>
        <EmployeeForm onCancel={() => setOpenModal(false)} />
      </Modal>
    </View>
  );
};

export { EmployeeReferencePage };
