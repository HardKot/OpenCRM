import { useSessionHoldEffect } from "#app/useSessionHoldEffect";
import { Dashboard } from "#pages/Dashboard";
import { ApplicationBar } from "#widgets/ApplicationBar";
import { Route, Routes } from "react-router-dom";

const AuthRoute = () => {
   useSessionHoldEffect();
   return (
      <>
         <ApplicationBar />
         <Routes>
            <Route path="/" Component={Dashboard} />
         </Routes>   
      </>
   )
}

export { AuthRoute }