import { Entity } from "./Entity";
import { User } from "./User";

interface UserState {
  isAuth: boolean;
  authData: User | null;
  entity: Entity | null;
}

export type { UserState };
