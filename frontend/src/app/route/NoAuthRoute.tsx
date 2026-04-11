import { AuthPage } from "#pages/AuthPage";
import { Route, Routes } from "react-router-dom";

const NoAuthRoute = () => (
  <Routes>
    <Route path="/auth" element={<AuthPage />} />

    <Route path="*" element={<AuthPage />} />
  </Routes>
);

export { NoAuthRoute };
