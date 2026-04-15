enum UserRole {
  Employee = "ROLE_EMPLOYEE",
  Owner = "ROLE_OWNER",
}

const getUserRole = (role: string): UserRole | null => {
  if (role === UserRole.Employee) return UserRole.Employee;
  if (role === UserRole.Owner) return UserRole.Owner;
  return null;
};

export { UserRole, getUserRole };
