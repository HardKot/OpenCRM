import { useSessionHoldEffect } from "#app/useSessionHoldEffect";
import { Dashboard } from "#pages/Dashboard";
import { Settings } from "#pages/Settings";
import { ApplicationBar } from "#widgets/ApplicationBar";
import { Route, Routes, useNavigate } from "react-router-dom";

const AuthRoute = () => {
   useSessionHoldEffect();
   let navigate = useNavigate();
   return (
      <>
         <ApplicationBar 
            goToSettings={() => navigate('/settings')}
         />
         <Routes>
            <Route path="/" Component={Dashboard} />
            <Route path="/dashboard" Component={Dashboard} />

            <Route path="/settings" Component={Settings} />
         </Routes>   
      </>
   )
}

export { AuthRoute }