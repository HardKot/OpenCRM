import { User } from "./User";


interface UserState {
    isAuth: boolean;
    authData: User | null;
}


export type { UserState }