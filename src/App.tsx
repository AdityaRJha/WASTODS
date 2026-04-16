import './App.css'
import {ToastContainer} from "react-toastify";
import {Route, Routes} from "react-router-dom";
import Home from "./pages/Home.tsx";
import Login from "./pages/Login.tsx";
import EmailVerify from "./pages/EmailVerify.tsx";
import ResetPassword from "./pages/ResetPassword.tsx";
import Showcase from "./pages/ProjectsShowcase.tsx";

const App = () => {

    return (
        <div>
            <ToastContainer />
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/email-verify" element={<EmailVerify />} />
                <Route path="/reset-password" element={<ResetPassword />} />
                <Route path="/showcase" element={<Showcase />}/>
            </Routes>
        </div>
    )
}

export default App
