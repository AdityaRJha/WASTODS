import {type ChangeEvent, type SyntheticEvent, useContext, useEffect, useRef, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {assets} from "../assets/assets.tsx";
import axios from "axios";
import * as React from "react";
import {AppContext} from "../context/AppContext.tsx";
import {toast} from "react-toastify";

const ResetPassword = () => {
    const inputRef = useRef<(HTMLInputElement | null)[]>([]);
    const [isOTPSent, setOTPSent] = useState(false);
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    const [reseting, setReseting] = useState(false);
    const [sendingOTP, setSendingOTP] = useState(false);
    const {backendURL} = useContext(AppContext);
    const [showPassword, setShowPassword] = useState(false);
    const navigate = useNavigate();
    const [timer, setTimer] = useState(60);
    const [canResend, setCanResend] = useState(false);

    useEffect(() => {
        if (timer > 0) {
            const interval = setInterval(() => {
                setTimer((prev) => prev - 1);
            }, 1000);

            return () => clearInterval(interval);
        } else {
            setCanResend(true);
        }
    }, [timer]);

    const handleResend = async () => {
        if (!canResend) return;

        setCanResend(false);
        await sendOTP();

        setTimer(60);
        setCanResend(false);
    };

    const sendOTP = async () =>{
        try{
            const response = await axios.post(backendURL+"/send-reset-otp", null, {
                params: {
                    email: email,
                },
            });
            if(response.status === 200) {
                toast.success("OTP Sent, please check your mail.");
                setOTPSent(true);    // ✅ start flow
                setTimer(60);          // reset timer
                setCanResend(false);
            }
        }catch(err){
            if(axios.isAxiosError(err)){
                toast.error(err.response?.data.message);
            }else{
                toast.error("Failed to send the reset OTP. Try Again.")
            }
            setOTPSent(false);
        }finally {
            setSendingOTP(false);
        }
    }

    const onSubmitForOTP = async (e: SyntheticEvent<HTMLButtonElement>) => {
        e.preventDefault();
        await sendOTP();
    }

    const handlePasswordReset = async (e: SyntheticEvent<HTMLButtonElement>) => {
        setReseting(true);
        e.preventDefault();
        try{
            setReseting(true);
            const otp = inputRef.current.map(input => input?.value).join("");
            const response = await axios.post(backendURL+"/reset-password", {email, otp, newPassword: password});
            if(response.status === 200) {
                toast.success("Password reset successfully.");
                navigate("/login");
            }
        }catch(err){
            if(axios.isAxiosError(err)){
                toast.error(err?.response?.data.message);
            }else{
                toast.error("Failed to reset password. Try Again.")
            }
        }finally {
            setReseting(false);
        }
    }

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

    return (
        <div className="position-relative min-vh-100 d-flex justify-content-center align-items-center"
             style={{background : "linear-gradient(90deg, #2baad8, #2baaed)", border: "none"}}>

            <div style={{position: "absolute", top:"20px", left:"30px", display:"flex", alignItems: "center"}}>

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

            <div className="card p-4"
                 style={{
                     maxWidth: "400px",
                     width: "100%",
                     background: "antiquewhite"
                 }}>
            <h2 className="text-center mb-4">Password Reset</h2>
            {!isOTPSent ? (
                <>
                    <span className="text-center mb-4">Send OTP</span>
                    <div className="mb-3">
                        <label htmlFor="email" className="form-label">Email</label>
                        <input type="text"
                               id="email"
                               className="form-control"
                               placeholder="Enter your registered email"
                               required
                               onChange={(e) => setEmail(e.target.value)}
                               value={email}
                        />
                    </div>
                    <button onClick={onSubmitForOTP} className="btn btn-primary w-100" disabled={sendingOTP}>
                        {sendingOTP ? "Sending OTP..." : "Send OTP"}
                    </button>
                </>
            ) : ( <>
                    <span className="text-center mb-4">Enter OTP and new password</span>
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

                    <div className="mb-3 position-relative">
                        <label htmlFor="password" className="form-label">Password</label>
                        <input
                            type={showPassword ? "text" : "password"}
                            id="password"
                            className="form-control"
                            placeholder="************"
                            required
                            onChange={(e) => setPassword(e.target.value)}
                            value={password}
                        />
                        <span onClick={() => setShowPassword((prev) => !prev)}
                              style={{
                                  position: "absolute",
                                  right: "10px",
                                  top: "38px",
                                  cursor: "pointer",
                                  userSelect: "none",
                                  fontSize: "20px",
                              }}>
                            {showPassword ? "😊" : "🫣"}
                        </span>
                    </div>

                    {!canResend ? (
                        <p>Resend OTP in {timer}s</p>
                    ) : (
                        <span onClick={handleResend} style={{cursor: "pointer"}}>
                            Resend OTP
                        </span>
                    )}

                    <button type="button"
                        className="btn btn-primary w-100"
                        disabled={reseting}
                        onClick={handlePasswordReset}
                    >
                        {reseting ? "Reseting..." : "Change Password"}
                    </button>
                </>
            )}
            </div>
        </div>

    )
}

export default ResetPassword;