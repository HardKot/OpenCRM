import { EmployeeForm } from "#features/form/employeeForm";
import { useI18n } from "#shared/hooks";
import { Layout, Modal, Text, Button, View } from "#shared/ui";
import { EmployeeReference } from "#widgets/references/employeeRefence/ui/EmployeeReference";
import { useState } from "react";
import { Outlet, useNavigate } from "react-router-dom";

const EmployeeReferencePage = () => {
  const [openModal, setOpenModal] = useState(false);
  const { t } = useI18n();
  const navigate = useNavigate();

  return (
    <View
      sx={{ display: "flex", flexDirection: "column", gap: 3, height: "100%" }}
    >
      <Layout.Paper
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          gap: 2,
          flexWrap: "wrap",
          p: 3,
          borderRadius: 3,
          boxShadow: "0 4px 24px rgba(0, 0, 0, 0.04)",
        }}
      >
        <Text variant="h5" fontWeight={600} color="text.primary">
          {t("employee.title")}
        </Text>
        <Button
          variant="contained"
          color="primary"
          onClick={() => setOpenModal(true)}
          left="Add"
          sx={{
            borderRadius: 2,
            px: 3,
            py: 1,
            textTransform: "none",
            fontWeight: 500,
            boxShadow: "none",
            "&:hover": {
              boxShadow: "0 4px 12px rgba(0, 0, 0, 0.15)",
            },
            display: "flex",
            gap: 1,
            alignItems: "center",
          }}
        >
          {t("employee.add")}
        </Button>
      </Layout.Paper>
      <EmployeeReference
        filter
        actions
        isDeleted
        sx={{ flexGrow: 1 }}
        onClick={(id) => navigate(`/employee/${id}`)}
      />
      <Modal open={openModal} onClose={() => setOpenModal(false)}>
        <EmployeeForm onCancel={() => setOpenModal(false)} />
      </Modal>

      <Outlet />
    </View>
  );
};

export { EmployeeReferencePage };
