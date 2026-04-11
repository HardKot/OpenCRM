import { createContext, PropsWithChildren, useContext } from "react";
import { TextPropsWithoutChildren, Text, Avatar } from "#shared/ui";
import { PersonalLibs } from "#shared/libs/PersonalLibs";
import { EmployeeDto } from "#shared/api/employeeApi";

const EmployeeContext = createContext<Partial<EmployeeDto>>({});

const useEmployeeContext = () => useContext(EmployeeContext);

const EmployeeId = (props: TextPropsWithoutChildren) => {
  const { id } = useEmployeeContext();
  if (!id) return null;
  return <Text {...props}>{id}</Text>;
};

const EmployeeFullName = (props: TextPropsWithoutChildren) => {
  const { firstname, lastname, patronymic } = useEmployeeContext();
  if (!firstname && !lastname) return null;
  return (
    <Text {...props}>
      {PersonalLibs.getFullname({
        firstname,
        lastname,
        patronymic,
      })}
    </Text>
  );
};

const EmployeePosition = (props: TextPropsWithoutChildren) => {
  const { position } = useEmployeeContext();
  if (!position) return null;
  return <Text {...props}>{position}</Text>;
};

const EmployeeAvatar = () => {
  const { firstname, lastname, patronymic } = useEmployeeContext();
  const initials = PersonalLibs.getInitials({
    firstname,
    lastname,
    patronymic,
  });
  if (!initials) return null;
  return <Avatar.Text text={initials} />;
};

const EmployeeEmail = (props: TextPropsWithoutChildren) => {
  const { email } = useEmployeeContext();
  if (!email) return null;
  return <Text {...props}>{email}</Text>;
};

const EmployeePhone = (props: TextPropsWithoutChildren) => {
  const { phone } = useEmployeeContext();
  if (!phone) return null;
  return <Text {...props}>{phone}</Text>;
};

const EmployeeShortName = (props: TextPropsWithoutChildren) => {
  const { firstname, lastname, patronymic } = useEmployeeContext();
  if (!firstname && !lastname && !patronymic) return null;
  return (
    <Text {...props}>
      {PersonalLibs.getShortName({
        firstname,
        lastname,
        patronymic,
      })}
    </Text>
  );
};

const EmployeeCard = ({
  data,
  children,
}: PropsWithChildren<{ data: EmployeeDto }>) => (
  <EmployeeContext.Provider value={data}>{children}</EmployeeContext.Provider>
);

EmployeeCard.Id = EmployeeId;
EmployeeCard.FullName = EmployeeFullName;
EmployeeCard.Position = EmployeePosition;
EmployeeCard.Avatar = EmployeeAvatar;
EmployeeCard.Email = EmployeeEmail;
EmployeeCard.Phone = EmployeePhone;
EmployeeCard.ShortName = EmployeeShortName;

export { EmployeeCard };
