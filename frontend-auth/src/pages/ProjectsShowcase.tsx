import Menubar from "../components/Menubar.tsx";
import Showpiece from "../components/Showpiece.tsx";

const Showcase = () => {
    return (
        <div className="flex flex-col min-vh-100">
            <Menubar />
            <Showpiece />
        </div>
    )
}

export default Showcase;