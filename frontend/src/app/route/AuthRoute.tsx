import { useSessionHoldEffect } from "#app/useSessionHoldEffect";
import { Dashboard } from "#pages/Dashboard";
import { AppBar } from "#shared/ui";
import { Route, Routes } from "react-router-dom";

const AuthRoute = () => {
   useSessionHoldEffect();
   return (
      <>
         <AppBar
            Search={<></>}
            Navigation={<></>}
            Profile={<></>}
         />
         <Routes>
            <Route path="/" Component={Dashboard} />
         </Routes>   
      </>
   )
}

export { AuthRoute }