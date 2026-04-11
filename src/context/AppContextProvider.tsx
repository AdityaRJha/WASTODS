import {type ReactNode, useState} from "react";
import {AppConstants} from "../util/constants.tsx";
import { AppContext } from "./AppContext.tsx";
import axios from "axios";
import {toast} from "react-toastify";
import type {User} from "../dataholders/User.ts";

type Props = {
    children: ReactNode;
};

export const AppContextProvider = ({ children }: Props): ReactNode => {
    const backendURL = AppConstants.BACKKEND_URL;
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userData, setUserData] = useState<User | null>(null);



    const getUserData = async () => {
        try {
            const response = await axios.get(`${backendURL}/profile`);
            if (response.status === 200) {
                setUserData(response.data);
            } else {
                toast.error("Unable to get user data");
            }
        } catch(error) {
            if(axios.isAxiosError(error)) {
                toast.error(error.message);
            }else{
                toast.error("Unable to get user data");
            }
        }
    }


    const contextValue = {
        backendURL,
        isLoggedIn, setIsLoggedIn,
        userData, setUserData,
        getUserData
    };

    return (
        <AppContext.Provider value={contextValue}>
            {children}
        </AppContext.Provider>
    );
};