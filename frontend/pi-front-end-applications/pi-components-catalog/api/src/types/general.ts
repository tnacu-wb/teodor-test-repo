export type LANG = 'en' | 'de';
export const CCUI_ROLES = 'https://ccui.opera.whitbread.digital/role';

export const UserRoleBasedAccess = {
  CC_ROLE03: {
    ROLE: 'CC_Role03',
    AGENT: true,
    MANAGER: true,
  },
  CC_ROLE05: {
    ROLE: 'CC_Role05',
    AGENT: false,
    MANAGER: true,
  },
  CC_ROLE12: {
    ROLE: 'CC_Role12',
    AGENT: true,
    MANAGER: true,
  },
};
Object.freeze(UserRoleBasedAccess);

export interface ScreenSizeValues {
  isLessThanMobile: boolean | undefined;
  isLessThanXs: boolean | undefined;
  isLessThanSm: boolean | undefined;
  isLessThanMd: boolean | undefined;
  isLessThanLg: boolean | undefined;
  isLessThanXl: boolean | undefined;
}
