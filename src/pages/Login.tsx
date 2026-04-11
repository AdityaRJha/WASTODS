import {Link, useNavigate} from "react-router-dom";
import {assets} from "../assets/assets.tsx";
import axios from "axios";
import {AppContext} from "../context/AppContext.tsx";
import {toast} from "react-toastify";
import { type SyntheticEvent, useContext, useState} from "react";

const Login = () => {
    const [isCreateAccount, setIsCreateAccount] = useState(false);
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const {backendURL, setIsLoggedIn, getUserData} = useContext(AppContext);
    const navigate = useNavigate();

    const onSubmitHandler = async (e: SyntheticEvent<HTMLFormElement>) => {
        e.preventDefault();
        axios.defaults.withCredentials = true;
        try{
            if(isCreateAccount){
                //register API
                const response = await axios.post(`${backendURL}/register`, {name, email, password})
                if(response.status === 201){
                    navigate("/");
                    toast.success("Account created successfully.");
                } else {
                    toast.error("Email already exists");
                }
            } else {
                //login API
                const response = await axios.post(`${backendURL}/login`, {email, password})
                if(response.status === 200){
                    setIsLoggedIn(true);
                    await getUserData();
                    navigate("/");
                } else {
                    toast.error("Email or Password does not match");
                }
            }
        } catch(x){
            if(axios.isAxiosError(x)){
                toast.error(x.response?.data?.message || "Something went wrong");
            }else{
                toast.error("Unexpected error occurred.");
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="position-relative min-vh-100 d-flex justify-content-center align-items-center"
            style={{background : "linear-gradient(90deg, #6a5af9, #8268f9)", border: "none"}}>

            <div style={{position: "absolute", top:"20px", left:"30px", display:"flex", alignItems: "center"}}>

                <Link to="/" style={{textDecoration: "none"}}>
                    <div className="card p-1 rounded-pill">
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
            <div className="card p-4" style={{maxWidth: "400px", width: "100%"}}>
                <h2 className="text-center mb-4">
                    {isCreateAccount ? "Create Account" : "Login"}
                </h2>

                <form onSubmit={onSubmitHandler}>
                    {
                        isCreateAccount && (
                            <div className="mb-3">
                                <label htmlFor="name" className="form-label">Full Name</label>
                                <input type="text"
                                       id="Name"
                                       className="form-control"
                                       placeholder="Enter your name"
                                       required
                                       onChange={(e) => setName(e.target.value)}
                                       value={name}
                                />
                            </div>
                        )
                    }
                    <div className="mb-3">
                        <label htmlFor="email" className="form-label">Email</label>
                        <input type="text"
                            id="email"
                            className="form-control"
                            placeholder="Enter your email"
                            required
                            onChange={(e) => setEmail(e.target.value)}
                            value={email}
                        />
                    </div>
                    <div className="mb-3">
                        <label htmlFor="password" className="form-label">Password</label>
                        <input
                            type="password"
                            id="password"
                            className="form-control"
                            placeholder="************"
                            required
                            onChange={(e) => setPassword(e.target.value)}
                            value={password}
                        />
                    </div>
                    <div className="d-flex justify-content-between mb-3">
                        <Link to = "/reset-password" className="text-decoration-none">
                            Forgot password?
                        </Link>
                    </div>

                    <button type="submit" className="btn btn-primary w-100" disabled={loading}>
                        {loading ? "Loading..." : isCreateAccount ? "Sign Up" : "Login"}
                    </button>
                </form>

                <div className="text-center mt-3">
                    <p className="mb-0">
                        {isCreateAccount ?
                            (<>
                                Already have an account?{" "}
                                <span
                                    onClick={() => {setIsCreateAccount(!isCreateAccount)}}
                                    className="text-decoration-underline" style={{cursor: "pointer"}}>
                                    Login here
                                </span>
                            </>
                            ): (
                                <>
                                    Don't have an account?{" "}
                                    <span
                                        onClick={() => {setIsCreateAccount(!isCreateAccount)}}
                                        className="text-decoration-underline" style={{cursor: "pointer"}}>
                                        Sign Up
                                    </span>
                                </>
                            )
                        }
                    </p>
                </div>
            </div>
        </div>
    )
}

export default Login;