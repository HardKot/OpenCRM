import { useSessionHoldEffect } from "#app/useSessionHoldEffect";
import { Dashboard } from "#pages/Dashboard";
import { EmployeeReferencePage } from "#pages/employee/EmployeeReference";
import { Settings } from "#pages/Settings";
import { View } from "#shared/ui";
import { ApplicationBar, NavigationTo } from "#widgets/ApplicationBar";
import { Route, Routes, useNavigate } from "react-router-dom";

const MapNavigation = {
   [NavigationTo.Main]: '/dashboard',
   [NavigationTo.Setting]: '/settings',
   [NavigationTo.Employee]: '/employee'
}

const AuthRoute = () => {
   useSessionHoldEffect();
   const navigate = useNavigate();

   return (
      <>
         <ApplicationBar 
            goTo={(key) => navigate(MapNavigation[key])}
            hrefMap={(key) => MapNavigation[key] }
         />
         <View padding={2}>
            <Routes>
               <Route path="/" Component={Dashboard} />
               <Route path="/dashboard" Component={Dashboard} />
               <Route path="/employee" Component={EmployeeReferencePage} />

               <Route path="/settings" Component={Settings} />
            </Routes>   
         </View>
      </>
   )
}

export { AuthRoute }