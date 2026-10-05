import { Area, FT_PI_AUTH0_LOGIN } from '@whitbread-eos/api';

import { getAuthCookie } from '../getters/auth';
import useFeatureToggle from './use-feature-toggle';
import { useAuth0AccessToken } from './useAuth0AccessToken';

/**
 * Unified auth token hook that respects the FT_PI_AUTH0_LOGIN feature flag.
 *
 * - When FT_PI_AUTH0_LOGIN is ON:  returns Auth0 access token
 * - When FT_PI_AUTH0_LOGIN is OFF: returns legacy id_token_cookie (unchanged behavior)
 *
 * BB and CCUI never enable FT_PI_AUTH0_LOGIN, so they are unaffected.
 */
export function useAuthToken() {
  const featureToggles = useFeatureToggle();
  const isAuth0Enabled = featureToggles?.[FT_PI_AUTH0_LOGIN] ?? false;

  const { accessToken: auth0AccessToken, isLoading } = useAuth0AccessToken(isAuth0Enabled);

  // When Auth0 is enabled and we have a token, use it. Otherwise fall back to legacy.
  const token = isAuth0Enabled && auth0AccessToken ? auth0AccessToken : getAuthCookie();

  return {
    token,
    isAuth0Enabled,
    isLoading: isAuth0Enabled ? isLoading : false,
  };
}

/**
 * Returns the derived `loggedOrCCUI` flag (token present OR area is CCUI).
 * Call alongside useAuthToken() to get the token separately.
 */
export function useLoggedOrCCUI(area?: Area): boolean {
  const { token } = useAuthToken();
  return !!(token || area === Area.CCUI);
}
