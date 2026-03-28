import { createContext, PropsWithChildren, useContext } from "react";
import { IEmployee } from "../model/IEmployee";
import { TextPropsWithoutChildren, Text, Avatar } from "#shared/ui";
import { PersonalLibs } from "#shared/libs/PersonalLibs";

const EmployeeContext = createContext<Partial<IEmployee>>({});

const useEmployeeContext = () => useContext(EmployeeContext);

const EmployeeId = (props: TextPropsWithoutChildren) => {
    const { id } = useEmployeeContext();
    if (!id) return null;
    return <Text {...props}>{id}</Text>
}

const EmployeeFullName = (props: TextPropsWithoutChildren) => {
    const { firstname, lastname, paronymic } = useEmployeeContext();
    if (!firstname && !lastname) return null;
    return <Text {...props}>{PersonalLibs.getFullname({ 
        firstname,
        lastname,
        paronymic
     })}</Text>
}

const EmployeePosition = (props: TextPropsWithoutChildren) => {
    const { position } = useEmployeeContext();
    if (!position) return null;
    return <Text {...props}>{position}</Text>
}

const EmployeeAvatar = () => {
    const { firstname, lastname, paronymic } = useEmployeeContext();
    const initials = PersonalLibs.getInitials({ firstname, lastname, paronymic });
    if (!initials) return null;
    return <Avatar.Text text={initials} />
}

const EmployeeEmail = (props: TextPropsWithoutChildren) => {
    const { email } = useEmployeeContext();
    if (!email) return null;
    return <Text {...props}>{email}</Text>
}

const EmpoyeePhoneNumber = (props: TextPropsWithoutChildren) => {
    const { phoneNumber } = useEmployeeContext();
    if (!phoneNumber) return null;
    return <Text {...props}>{phoneNumber}</Text>
}

const EmployeeCard = ({ data, children }: PropsWithChildren<{data: IEmployee}>) => (
    <EmployeeContext.Provider value={data}>
        {children}
    </EmployeeContext.Provider>
)

EmployeeCard.Id = EmployeeId;
EmployeeCard.FullName = EmployeeFullName;
EmployeeCard.Position = EmployeePosition;
EmployeeCard.Avatar = EmployeeAvatar;
EmployeeCard.Email = EmployeeEmail;
EmployeeCard.PhoneNumber = EmpoyeePhoneNumber;

export { EmployeeCard }