import { Box, Flex } from '@chakra-ui/react';
import { Button } from '@whitbread-eos/atoms';
import { renderSanitizedHtml } from '@whitbread-eos/utils';

import { CookiePoliciesModalProps } from '../types';

export interface Props extends CookiePoliciesModalProps {
  manageCookies: () => void;
}

export default function CookiePolicies({
  labels,
  cookiePermissions,
  setCookiePolicies,
  manageCookies,
  modalStyles,
}: Readonly<Props>) {
  const cookiePolicies = labels?.cookieConsent?.cookiePolicies;

  const acceptAllCookies = () => {
    const newCookiePermissions = cookiePermissions?.map((cookiePermission) => {
      if (cookiePermission.name !== 'permissionEssential') {
        return {
          ...cookiePermission,
          value: true,
        };
      }
      return cookiePermission;
    });
    setCookiePolicies(newCookiePermissions);
  };

  return (
    <Flex
      {...modalStyles?.wrapperStyles}
      {...cookieIntroStyle}
      data-testid="CookiePoliciesModal-Container"
    >
      <Flex>
        <Box {...modalStyles?.textStyles.description} data-testid="CookiePoliciesModal-Description">
          {renderSanitizedHtml(cookiePolicies?.introView?.description)}
        </Box>
      </Flex>
      <Flex mt="lg" {...modalStyles?.buttonsStyle}>
        <Button
          size="full"
          variant="tertiary"
          data-testid="CookiePoliciesModal-ManageButton"
          onClick={manageCookies}
          padding={'0 5px 0'}
        >
          {cookiePolicies?.introView?.manageButtonText}
        </Button>
        {labels?.cookieConsent?.cookiePolicies?.introView?.necessaryOnlyButtonText && (
          <Button
            size="full"
            variant="primary"
            data-testid="CookiePoliciesModal-NecessaryOnlyButton"
            onClick={() => setCookiePolicies()}
            {...acceptAllBtnStyle}
          >
            {cookiePolicies?.introView?.necessaryOnlyButtonText}
          </Button>
        )}
        <Button
          size="full"
          variant="primary"
          data-testid="CookiePoliciesModal-AcceptAllButton"
          onClick={acceptAllCookies}
          {...acceptAllBtnStyle}
        >
          {cookiePolicies?.introView?.acceptAllButtonText}
        </Button>
      </Flex>
    </Flex>
  );
}

const cookieIntroStyle = {
  width: {
    sm: '33.75rem',
    md: '33.375rem',
    lg: '39rem',
  },
};
const acceptAllBtnStyle = {
  mt: { mobile: 'md', sm: 0 },
  ml: { md: 'xl', sm: 'xl', lg: 'xl' },
  padding: '0 5px 0',
};
