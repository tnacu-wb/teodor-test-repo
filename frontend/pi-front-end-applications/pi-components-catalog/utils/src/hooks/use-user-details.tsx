'use client';

import { Customer } from '@whitbread-eos/api';
import getConfig from 'next/config';

import { decodeIdToken } from '../getters/auth';
import { useRestQueryRequest } from './use-request';
import { useAuth0User } from './useAuth0User';
import { useAuthToken } from './useAuthToken';

export default function useUserDetails(
  isBusinessBooker: boolean,
  isLoggedIn: boolean,
  userDetails?: Customer
) {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const { token: authToken, isAuth0Enabled, isLoading: isAuthTokenLoading } = useAuthToken();
  const { user: auth0User } = useAuth0User(isAuth0Enabled);
  const email =
    isAuth0Enabled && auth0User?.email ? auth0User.email : decodeIdToken(authToken).email;
  const { data } = useRestQueryRequest(
    ['userDetails', authToken],
    'GET',
    `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/customers/hotels/${email}?business=${isBusinessBooker}`,
    authToken ? { Authorization: `Bearer ${authToken}` } : undefined,
    { enabled: isLoggedIn && !isAuthTokenLoading && !!authToken && !!email && !userDetails }
  );
  return userDetails ?? data;
}
