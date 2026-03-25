import { useLocation, Navigate } from 'react-router-dom';
import { useAppSelector } from '#shared/hooks/reduxHooks';
import { getUserAuthData } from '#entities/User';

interface RequireAuthProps {
    children: JSX.Element;
}

export function RequireAuth({ children }: RequireAuthProps) {
    const auth = useAppSelector(getUserAuthData);
    const location = useLocation();

    if (!auth) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    return children;
}
