import { userIsAuthSelector } from "#entities/User"
import { useAppSelector } from "#shared/hooks"
import { AuthRoute } from "./AuthRoute";
import { NoAuthRoute } from "./NoAuthRoute";

const Route = () => {
    const isAuth = useAppSelector(userIsAuthSelector);

    if (!isAuth) return <NoAuthRoute />
    return <AuthRoute />
}

export { Route }