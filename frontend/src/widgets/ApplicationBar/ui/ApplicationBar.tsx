import { useLogout } from "#shared/api"
import { AppBar, Menu } from "#shared/ui"
import { NavigationTo } from "../libs/types";
import { NavigationMenu } from "./NavigationMenu";
import { UserPreview } from "./UserPreview"

interface ApplicationBarProps {
    goTo: (to: NavigationTo) => void;
    hrefMap: (to: NavigationTo) => string;
}


const ApplicationBar = ({ goTo, hrefMap }: ApplicationBarProps) => {
    const [logout] = useLogout();
    return (
        <AppBar 
            goToMain={() => goTo(NavigationTo.Main)}
            Search={null}
            Navigation={<NavigationMenu hrefMap={hrefMap}/>}
            Profile={(
                <Menu 
                    Component={<UserPreview />}
                    MenuItems={[
                        { label: 'Настройки', onClick: () => goTo(NavigationTo.Setting) },
                        { label: 'Выход', onClick: () => logout() },
                    ]}
                />
            )}
        />
)}

export { ApplicationBar }