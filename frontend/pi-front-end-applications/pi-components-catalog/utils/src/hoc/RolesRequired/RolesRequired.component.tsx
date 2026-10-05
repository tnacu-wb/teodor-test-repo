interface Props {
  userRole: string;
  requiredRoles: string[];
  children: React.ReactNode;
}

const RolesRequired = ({ userRole, requiredRoles, children }: Readonly<Props>) => {
  if (requiredRoles.includes(userRole)) return <>{children}</>;
  return null;
};

export default RolesRequired;
