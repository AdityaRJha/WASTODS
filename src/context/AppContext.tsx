import {type Context, createContext, type Dispatch, type SetStateAction} from "react";
import type {User} from "../dataholders/User.ts";

type AppContextType = {
    backendURL: string;

    isLoggedIn: boolean;
    setIsLoggedIn: Dispatch<SetStateAction<boolean>>;

    userData: User | null;
    setUserData: Dispatch<SetStateAction<User | null>>;

    getUserData: () => Promise<void>;
};

export const AppContext: Context<AppContextType> = createContext<AppContextType>(
    {} as AppContextType
);

