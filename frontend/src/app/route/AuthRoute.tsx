import { Dashboard } from "#pages/Dashboard";
import { AppBar } from "#shared/ui";
import { Route, Routes } from "react-router-dom";

const AuthRoute = () => (
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

export { AuthRoute }