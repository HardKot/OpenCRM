const PersonalLibs = Object.freeze({
  getFullname: (data: {
    firstname?: string;
    lastname?: string;
    patronymic?: string;
  }) => {
    let fullname = "";
    if (data.lastname) fullname += data.lastname;
    if (data.firstname) fullname += ` ${data.firstname}`;
    if (data.patronymic) fullname += ` ${data.patronymic}`;
    return fullname.trim();
  },
  getInitials: (data: {
    firstname?: string;
    lastname?: string;
    patronymic?: string;
  }) => {
    let initials = "";
    if (data.lastname) initials += data.lastname[0];
    if (data.firstname) initials += data.firstname[0];
    if (data.patronymic) initials += data.patronymic[0];
    return initials.toUpperCase();
  },
  getShortName: (data: {
    firstname?: string;
    lastname?: string;
    patronymic?: string;
  }) => {
    let shortName = "";
    if (data.lastname) shortName += data.lastname;
    if (data.firstname) shortName += ` ${data.firstname[0]}.`;
    if (data.patronymic) shortName += ` ${data.patronymic[0]}.`;
    return shortName.trim();
  },
});

export { PersonalLibs };
