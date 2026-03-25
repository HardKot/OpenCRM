import { LoginPage } from "#pages/LoginPage";
import { Route, Routes } from "react-router-dom";

const NoAuthRoute = () => (
    <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/about" element={<LoginPage />} />

        <Route path="*" element={<LoginPage />} />
    </Routes>
)

export { NoAuthRoute }