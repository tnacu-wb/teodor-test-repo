import { BUSINESS_BOOKER_USER_ROLES } from '@whitbread-eos/api';
import { getLoggedInUserInfo, ID_TOKEN_COOKIE } from '@whitbread-eos/utils';
import Cookies from 'cookies';

export async function checkIsGuestUser({ ...props }) {
  const cookies = new Cookies(props.req, props.res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  let isGuestUser = false;

  if (idTokenCookie) {
    const { accessLevel } = getLoggedInUserInfo(idTokenCookie);
    isGuestUser = accessLevel === BUSINESS_BOOKER_USER_ROLES.STAYER;
  }

  return isGuestUser;
}
