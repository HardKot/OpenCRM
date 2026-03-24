import React from 'react';
import { useNavigate } from 'react-router-dom';
import { LoginForm } from '#features/AuthByUsername';
import { View } from '#shared/ui';

const LoginPage: React.FC = () => {
    const navigate = useNavigate();

    const onSuccess = () => {
        navigate('/');
    };

    return (
        <View 
            display="flex" 
            justifyContent="center" 
            alignItems="center" 
            minHeight="100vh"
            bgcolor="background.default"
        >
             <View 
                p={4} 
                bgcolor="background.paper"
                borderRadius={2}
                boxShadow={3}
            >
                 <LoginForm onSuccess={onSuccess} />
             </View>
        </View>
    );
};

export default LoginPage;
