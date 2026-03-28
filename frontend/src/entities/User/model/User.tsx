import { UserPermission } from "./UserPermission";
import { UserRole } from "./UserRole";

interface User {
    username: string;
    userId: string;
    tenantId: string;

    permissions: UserPermission[];
    role: UserRole;

    entityId: number;

}

export type { User }