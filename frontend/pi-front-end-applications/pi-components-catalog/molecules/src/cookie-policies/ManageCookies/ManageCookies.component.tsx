import {
  Box,
  BoxProps,
  Divider,
  DividerProps,
  Flex,
  FlexProps,
  Text,
  TextProps,
} from '@chakra-ui/react';
import { Button, Switcher } from '@whitbread-eos/atoms';
import { renderSanitizedHtml, formatDataTestId } from '@whitbread-eos/utils';

import { CookiePoliciesModalProps } from '../types';

export default function ManageCookies({
  labels,
  cookiePermissions,
  setCookiePermissions,
  setCookiePolicies,
  modalStyles,
}: Readonly<CookiePoliciesModalProps>) {
  const manageCookiesLabels = labels?.cookieConsent.cookiePolicies.manageView;
  const setCookiePermissionValue = (cookieName: string, status: boolean) => {
    const newCookiePermissions = cookiePermissions?.map((cookiePermission) => {
      if (cookiePermission.name === cookieName) {
        return {
          ...cookiePermission,
          value: status,
        };
      }
      return cookiePermission;
    });
    if (setCookiePermissions) {
      setCookiePermissions(newCookiePermissions);
    }
  };

  return (
    <Box
      {...modalStyles?.wrapperStyles}
      {...manageContainerStyle}
      data-testid="ManageCookieModal-Container"
    >
      <Box {...modalStyles?.textStyles.description} data-testid="ManageCookieModal-Description">
        {renderSanitizedHtml(manageCookiesLabels?.description)}
      </Box>

      <Divider {...dividerStyle} data-testid="ManageCookieModal-Divider" />

      <Flex flexDirection="column" data-testid="ManageCookieModal-CookieGroup">
        {manageCookiesLabels?.cookieGroup.map((cookie) => {
          return (
            <Flex
              flexDirection="column"
              data-testid={formatDataTestId('ManageCookieModal', cookie?.cookieName)}
              mb="lg"
              key={cookie.cookieName}
            >
              <Flex flexDirection="row" {...cookieGroupTitleStyle.container}>
                <Text
                  data-testid={formatDataTestId('ManageCookieModal', cookie?.title)}
                  {...cookieGroupTitleStyle.text}
                >
                  {cookie.title}
                </Text>
                {cookie.cookieName === 'permissionEssential' ? (
                  <Text
                    {...alwaysActiveStyle}
                    data-testid={`ManageCookieModal-${cookie.cookieName}-Text`}
                  >
                    {manageCookiesLabels.alwaysActiveText}{' '}
                  </Text>
                ) : (
                  <Switcher
                    size="lg"
                    isChecked={cookie.isAlwaysActive}
                    onChange={(status) =>
                      setCookiePermissionValue(cookie.cookieName, status.isChecked)
                    }
                    data-testid={`ManageCookieModal-${cookie.cookieName}-Switcher`}
                  />
                )}
              </Flex>
              <Box
                {...modalStyles?.textStyles.description}
                data-testid={formatDataTestId('ManageCookieModal', `${cookie?.title}-Description`)}
              >
                {renderSanitizedHtml(cookie?.description)}
              </Box>
            </Flex>
          );
        })}
      </Flex>

      <Flex {...modalStyles?.buttonsStyle} {...confirmBtnStyle}>
        <Button
          size="full"
          mb="lg"
          variant="primary"
          data-testid={formatDataTestId(
            'ManageCookieModal',
            manageCookiesLabels?.saveSettingsButtonText
          )}
          onClick={() => setCookiePolicies()}
        >
          {manageCookiesLabels?.saveSettingsButtonText}
        </Button>
      </Flex>
    </Box>
  );
}

const manageContainerStyle = {
  display: 'block',
  height: {
    sm: '35rem',
    md: '40rem',
    lg: '45rem',
  },
  width: {
    sm: '30.75rem',
    lg: '30.75rem',
  },
} as BoxProps;

const dividerStyle = {
  mt: 'lg',
  mb: 'lg',
} as DividerProps;

const cookieGroupTitleStyle = {
  container: { justifyContent: 'space-between', mb: 'md' } as FlexProps,
  text: {
    color: 'darkGrey1',
    fontWeight: 'semibold',
    fontSize: '2xl',
    lineHeight: '4',
  } as TextProps,
};

const alwaysActiveStyle = {
  fontWeight: 'medium',
  fontSize: 'xl',
  lineHeight: '3',
} as TextProps;

const confirmBtnStyle = {
  width: 'full',
};
