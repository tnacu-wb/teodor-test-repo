export type LANG = 'en' | 'de';
export const CCUI_ROLES = 'https://ccui.opera.whitbread.digital/role';

// Auth0 user claims type (Auth0 v4 doesn't export Claims)
export interface Claims {
  [key: string]: any;
  name?: string;
  email?: string;
  sub?: string;
  nickname?: string;
  picture?: string;
  updated_at?: string;
}

// Auth0 session type (Auth0 v4 doesn't export Session)
export interface Session {
  user: Claims;
  accessToken?: string;
  idToken?: string;
}
