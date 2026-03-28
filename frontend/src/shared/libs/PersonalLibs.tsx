
const PersonalLibs = Object.freeze({
    getFullname: (data: { firstname?: string; lastname?: string; paronymic?: string }) => {
        let fullname = "";
        if (data.lastname) fullname += data.lastname;
        if (data.firstname) fullname += ` ${data.firstname}`;
        if (data.paronymic) fullname += ` ${data.paronymic}`;
        return fullname.trim();
    },
    getInitials: (data: { firstname?: string; lastname?: string; paronymic?: string }) => {
        let initials = "";
        if (data.lastname) initials += data.lastname[0];
        if (data.firstname) initials += data.firstname[0];
        if (data.paronymic) initials += data.paronymic[0];
        return initials.toUpperCase();
    }
});

export { PersonalLibs };