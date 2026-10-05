import { UserAccessLevels } from '@whitbread-eos/api';

import { isStringValid } from '../validators';

export const isGuestDetailsPageAllowed = (accessLevel: UserAccessLevels | string | null) => {
  if (!isStringValid(accessLevel)) {
    return false;
  }

  return [UserAccessLevels.SUPER, UserAccessLevels.BOOKER, UserAccessLevels.SELF].includes(
    accessLevel as UserAccessLevels
  );
};

export const isDataCollectionMsgAndFormVisible = (
  accessLevel: UserAccessLevels | string | null
) => {
  if (!isStringValid(accessLevel)) {
    return false;
  }

  return [UserAccessLevels.SUPER, UserAccessLevels.BOOKER].includes(
    accessLevel as UserAccessLevels
  );
};
