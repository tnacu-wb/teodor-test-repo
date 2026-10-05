import { FlexProps, TextProps } from '@chakra-ui/react';

export interface CookiePoliciesLabels {
  cookieConsent: {
    cookiePolicies: {
      brand: string;
      introView: {
        acceptAllButtonText: string;
        description: string;
        manageButtonText: string;
        title: string;
        necessaryOnlyButtonText: string;
      };
      manageView: ManageViewProps;
    };
  };
}

export interface ManageViewProps {
  alwaysActiveText: string;
  description: string;
  saveSettingsButtonText: string;
  title: string;
  cookieGroup: CookieGroup[];
}

export interface CookieGroup {
  cookieName: string;
  description: string;
  isAlwaysActive: boolean;
  title: string;
  toggleLabel: string;
}

export interface CookiePermission {
  id: number;
  name: string;
  value: boolean;
}
export interface CookiePoliciesModalProps {
  labels: CookiePoliciesLabels;
  cookiePermissions: CookiePermission[];
  setCookiePolicies: (items?: CookiePermission[]) => void;
  setCookiePermissions?: (value: CookiePermission[]) => void;
  modalStyles?: CookieModalStyles;
}

export interface CookieModalStyles {
  wrapperStyles: FlexProps;
  buttonsStyle: FlexProps;
  textStyles: {
    title: TextProps;
    description: TextProps;
  };
}
