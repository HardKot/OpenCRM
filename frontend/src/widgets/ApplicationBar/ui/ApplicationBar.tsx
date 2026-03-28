import { useLogout } from "#shared/api"
import { AppBar, Menu } from "#shared/ui"
import { UserPreview } from "./UserPreview"

const ApplicationBar = () => {
    const [logout] = useLogout();
    return (
    <AppBar 
        Search={null}
        Navigation={null}
        Profile={(
            <Menu 
                Component={<UserPreview />}
                MenuItems={[
                    { label: 'Настройки', onClick: () => console.log('Настройки') },
                    { label: 'Выход', onClick: () => logout() },
                ]}
            />
        )}
    />
)}

export { ApplicationBar }