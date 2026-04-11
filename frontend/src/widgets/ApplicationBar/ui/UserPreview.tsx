import { entitySelector } from "#entities/User";
import { useAppSelector } from "#shared/hooks";
import { useMemo } from "react";
import { entityToEmployee } from "../libs/Adapter";
import { EmployeeCard } from "#entities/Employee";
import { View } from "#shared/ui";

const UserPreview = () => {
  const entity = useAppSelector(entitySelector);
  const employeeDto = useMemo(() => {
    if (!entity) return null;
    return entityToEmployee(entity);
  }, [entity]);

  if (!employeeDto) return null;

  return (
    <View columnGap={2} display="flex" alignItems="center" bgcolor={""}>
      <EmployeeCard data={employeeDto}>
        <EmployeeCard.Avatar />
        <View>
          <EmployeeCard.ShortName color="white" />
          <EmployeeCard.Position color="textSecondary" />
        </View>
      </EmployeeCard>
    </View>
  );
};

export { UserPreview };
