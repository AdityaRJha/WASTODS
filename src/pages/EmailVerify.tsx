import {Link, Navigate, useNavigate} from "react-router-dom";
import {assets} from "../assets/assets.tsx";
import {type ChangeEvent, useContext, useRef, useState} from "react";
import {AppContext} from "../context/AppContext.tsx";
import {toast} from "react-toastify";
import axios, {isAxiosError} from "axios";
import * as React from "react";

const EmailVerify = () =>{
    const inputRef = useRef<(HTMLInputElement | null)[]>([]);
    const [loading, setLoading] = useState(false);
    const {backendURL, getUserData, isLoggedIn, userData} = useContext(AppContext);
    const navigate = useNavigate();

    const handleChange = (e: ChangeEvent<HTMLInputElement>, index: number) => {
        const value = e.target.value.replace(/\D/,"");
        e.target.value = value;
        if(value && index < 5){
            inputRef.current[index + 1]?.focus();
        }
    }

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
        if(e.key === "Backspace" && !e.currentTarget.value && index > 0){
            inputRef.current[index - 1]?.focus();
        }
    }

    const handlePaste = (e : React.ClipboardEvent<HTMLInputElement>) => {
        e.preventDefault();
        const paste = e.clipboardData
            .getData("text")
            .replace(/\D/g, "")
            .slice(0, 6)
            .split("");
        paste.forEach((digit: string, i : number) => {
            const input = inputRef.current[i];
            if(input){
                input.value = digit;
            }
        })

        const next = paste.length < 6 ? paste.length : 5;
        inputRef.current[next]?.focus();
    }

    const handleVerify = async () => {
        const otp = inputRef.current.map(input => input?.value).join("");
        if(otp.length != 6){
            toast.error("Six digits required in OTP.")
            return;
        }
        setLoading(true);
        try {
            const response = await axios.post(backendURL + "/verify-account", {otp});
            if (response.status === 200) {
                toast.success("OTP verified successfully.");
                await getUserData();
                navigate("/");
            }else{
                toast.error("Invalid OTP");
            }
        }catch (error) {
            if(isAxiosError(error)){
                toast.error(error.message);
            }else{
                toast.error("OTP verification failed, please try again.");
            }
        }finally {
            setLoading(false);
        }
    }

    if(!isLoggedIn){
        return <Navigate to={"/login"} />;
    }

    if(userData?.isAccountVerified){
        return <Navigate to={"/"} />
    }

    return(
        <div className="email-verify-container d-flex align-items-center justify-content-center vh-100 position-relative"
            style={{background: "linear-gradient(90deg, #2baad8, #2baaed)", border: "none"}}
        >

            <div
                style={{
                    position: "absolute",
                    top:"20px",
                    left:"30px",
                    display:"flex",
                    alignItems: "center",
            }}>

                <Link to="/" style={{textDecoration: "none"}}>
                    <div className="card p-1 rounded-pill" style={{background: "antiquewhite"}}>
                        <div id="logoholder" style={{
                            display: "flex",
                            gap: 5,
                            alignItems: "center",
                            fontWeight: "bold",
                            fontSize: "24px",
                            textDecoration: "none",
                        }}>
                            <img src={assets.teapotLogo} alt="logo" height={40} width={40}/>
                            <span className="fw-bold fs-3 ">teApot</span>
                        </div>
                    </div>
                </Link>

        </div>

            <div className="p-5 rounded-4 shadow"
                 style={{
                     width: "400px",
                     background: "antiquewhite"
            }}>
                <h4 className="text-center fw-bold mb-2">Email Verification OTP</h4>
                <p className="text-center text-cyan-50 mb-4">
                    Enter the 6-digit code sent to your email.
                </p>

                <div className="d-flex justify-content-between gap-2 mb-4 text-center text-white-50 mb-2">
                    {[...Array(6)].map((_, i) => (
                        <input
                            key={i}
                            type="text"
                            className="form-control text-center fs-4 otp-input"
                            maxLength={1}
                            ref={(el) => {inputRef.current[i] = el}}
                            onChange={(e) => handleChange(e, i)}
                            onKeyDown={(e) => handleKeyDown(e, i)}
                            onPaste={handlePaste}
                        />
                    ))}
                </div>

                <button className="btn btn-primary w-100 fw-semibold"
                        disabled={loading}
                        onClick={handleVerify}
                >
                    {loading ? "Verifying...": "Verify email"}
                </button>
            </div>

        </div>
    )
}

export default EmailVerify;