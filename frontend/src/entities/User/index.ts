export {
    userReducer,
    userActions,
} from './model/slice/userSlice';

export {
    getUserAuthData,
    getUserInited,
} from './model/selectors/getUserAuthData/getUserAuthData';

export type {
    User,
    UserSchema,
} from './model/types/userSchema';
