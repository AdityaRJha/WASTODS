import {assets} from "../assets/assets.tsx";
import {Link, useNavigate} from "react-router-dom";
import {useContext} from "react";
import {AppContext} from "../context/AppContext.tsx";

const Header = () => {
    const {userData} = useContext(AppContext);
    const navigate = useNavigate();

    const handleGetStarted = () => {
        if(userData == null) {
            navigate("/login")
        }else{
            navigate("/showcase");
        }
    }

    return(
        <div className="text-center d-flex flex-column align-items-center justify-content-center py-5 px-3" style={{minHeight : "80vh"}}>
            <Link to = "/" style={{
                display: "flex",
                gap: "5",
                alignItems: "center",
                fontWeight: "bold",
                fontSize: "24px",
                textDecoration: "none",
            }}>
                <img src={assets.myFace} alt="Developer" width={120} height={120} className="mb-4 rounded-pill object-cover"/>
            </Link>

            <h5 className="fw-semibold">
                Hey {userData ? userData.name : 'buddy'}, the developer here! <span role="img" aria-label="wave">🚀</span>
            </h5>
            <h1 className="fw-bold display-5 mb-3">Welcome to my site!</h1>

            <p className="text-muted fs-5 mb-4" style={{maxWidth:"500px"}}>
                Where this is a product and a project on my resume as well. Let's start with a quick tour of this site. !
            </p>

            <button className="btn btn-outline-dark rounded-pill px-4 py-2" onClick={handleGetStarted}>
                Get Started
            </button>
        </div>
    )
}

export default Header;