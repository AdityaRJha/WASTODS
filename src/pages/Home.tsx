import Menubar from "../components/Menubar.tsx";
import Header from "../components/Header.tsx";

const Home = () => {
    return (
        <div className="flex flex-col items-center justify-content-center min-vh-100">
            <Menubar />
            <Header />
        </div>
    )
}

export default Home;