import { LoginForm } from "#features/auth/LoginForm";
import { useNavigate } from "react-router-dom";

export const Login = () => {
    const navigate = useNavigate();

    const onSuccess = () => {
        navigate('/');
    };

    return (
        <LoginForm onSuccess={onSuccess} />
    )
}