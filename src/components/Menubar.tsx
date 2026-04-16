import {assets} from "../assets/assets.tsx";
import { useNavigate} from "react-router-dom";
import {useContext, useEffect, useRef, useState} from "react";
import {AppContext} from "../context/AppContext.tsx";
import axios from "axios";
import {toast} from "react-toastify";
import "../styles/Menubar.css"

const Menubar = () => {
    const navigate = useNavigate();
    const {userData, backendURL, setIsLoggedIn, setUserData} = useContext(AppContext);
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const dropdownRef = useRef<HTMLDivElement | null>(null);


    useEffect(() => {
        const handleClickOutside = (event: Event): void => {
            if(dropdownRef.current && !dropdownRef.current.contains(event.target as Node)){
                setDropdownOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const handleLogout = async () => {
        try{
            axios.defaults.withCredentials = true;
            const response = await axios.post(backendURL + "/logout");
            if(response.status === 200){
                setIsLoggedIn(false);
                setUserData(null);
                navigate("/");
            }
        }catch(error){
            if(axios.isAxiosError(error)){
                toast.error(error.message);
            } else {
                toast.error("Something went wrong");
            }
        }
    }

    const sendEmailVerificationOtp = async () => {
        try{
            axios.defaults.withCredentials = true;
            const response = await axios.post(backendURL + "/send-otp");
            if(response.status === 200){
                navigate("/email-verify");
                toast.success("OTP has been sent to your registration email id.")
            }else{
                toast.error("Unable to send the OTP.")
            }
        }catch (error){
            if(axios.isAxiosError(error)){
                toast.error(error.message);
            }else{
                toast.error("Something went wrong");
            }
        }
    }

    return (
        <nav className="navbar">
            <div className="d-flex align-items-center gap-2">
                <img src={assets.teapotLogo} alt={"logo"} width={32} height={32}/>
                <span className="fw-bold fs-4 text-dark">teApot</span>
            </div>

            {userData ? (
                <div className="position-relative" ref={dropdownRef}>
                    <div className="bg-dark text-white rounded-circle d-flex justify-content-center align-items-center"
                        style={{
                            width: "40px",
                            height: "40px",
                            cursor: "pointer",
                            userSelect: "none",
                        }}
                         onClick={() => setDropdownOpen((prev) => !prev)}
                    >
                        {userData.name[0].toUpperCase()}
                    </div>
                    {dropdownOpen && (
                        <div className="position-absolute shadow bg-white rounded p-2"
                        style={{
                            top: "50px",
                            right: 0,
                            zIndex: 100,
                        }}>
                            {!userData.isAccountVerified && (
                                <div className="dropdown-item py-1 px-2"
                                     style={{cursor: "pointer"}}
                                >
                                    <span onClick={sendEmailVerificationOtp}>
                                        Verify Email
                                    </span>
                                </div>
                            )}
                            <div className="dropdown-item py-1 px-2 text-danger"
                                 style={{cursor: "pointer"}}
                                 onClick={handleLogout}
                            >
                                Logout
                            </div>
                        </div>
                    )}
                </div>
            ) : (
                <div className="btn btn-outline-dark rounded-pill px-3" onClick={() => navigate("/login")}>
                    Login <i className="bi bi-arrow-right ms-2"></i>
                </div>
            )}

        </nav>
    )
}

export default Menubar;