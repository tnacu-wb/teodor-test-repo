import { RolesList } from '@whitbread-eos/api';

type Props = {
  children: React.ReactNode;
  excludedForRoles?: RolesList;
  userRoles?: RolesList;
};

export function RolesCheck({ children, userRoles, excludedForRoles }: Props) {
  const noUserRolesPassed = !userRoles || (!userRoles.wl && !userRoles.bb);
  if (noUserRolesPassed) {
    return null;
  }

  const isExcludedWl =
    (!!excludedForRoles?.wl?.length &&
      !!userRoles?.wl?.length &&
      userRoles?.wl?.every((role) => excludedForRoles?.wl?.includes(role))) ??
    false;
  if (isExcludedWl) {
    return null;
  }

  const isExcludedBb =
    (!!excludedForRoles?.bb?.length &&
      !!userRoles?.bb?.length &&
      excludedForRoles?.bb?.includes(userRoles?.bb?.[0])) ??
    false;
  if (isExcludedBb) {
    return null;
  }

  return <>{children}</>;
}
