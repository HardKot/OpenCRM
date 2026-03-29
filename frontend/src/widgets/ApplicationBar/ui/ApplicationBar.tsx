import { useLogout } from "#shared/api"
import { AppBar, Menu } from "#shared/ui"
import { UserPreview } from "./UserPreview"

interface ApplicationBarProps {
    goToSettings: () => void;
}

const ApplicationBar = ({ goToSettings }: ApplicationBarProps) => {
    const [logout] = useLogout();
    return (
    <AppBar 
        Search={null}
        Navigation={null}
        Profile={(
            <Menu 
                Component={<UserPreview />}
                MenuItems={[
                    { label: 'Настройки', onClick: () => goToSettings() },
                    { label: 'Выход', onClick: () => logout() },
                ]}
            />
        )}
    />
)}

export { ApplicationBar }