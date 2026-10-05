import { Box, Text, TextProps } from '@chakra-ui/react';
import { FT_PI_AUTH0_LOGIN } from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms';
import {
  analytics,
  formatDataTestId,
  useAuth0Navigation,
  useFeatureToggle,
  useSemanticTypography,
  useUserData,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

const baseDataTestId = 'GuestDetails-Auth0SignIn';

export default function Auth0SignIn() {
  const { t } = useTranslation();
  const { isLoggedIn } = useUserData();
  const { [FT_PI_AUTH0_LOGIN]: isAuth0Enabled } = useFeatureToggle();
  const { navigateToLogin } = useAuth0Navigation();
  const getTypographyProps = useSemanticTypography();

  if (!isAuth0Enabled || isLoggedIn) return null;

  return (
    <Box {...wrapperStyles} data-testid={formatDataTestId(baseDataTestId, 'Container')}>
      <Text
        {...headingStyles}
        {...getTypographyProps(headingLegacyTypography, headingSemanticTypography)}
        mb="sm"
        data-testid={formatDataTestId(baseDataTestId, 'Heading')}
      >
        {t('booking.login.labelHaveAccount')}
      </Text>
      <Text
        {...descriptionStyles}
        {...getTypographyProps(descriptionLegacyTypography, descriptionSemanticTypography)}
        data-testid={formatDataTestId(baseDataTestId, 'Description')}
      >
        {t('booking.universalLogin.notRegisteredYetLabel')}
      </Text>
      <Button
        variant="primary"
        onClick={() => {
          analytics.track('auth_sign_in_click', { authStep: 'sign_in_click' });
          navigateToLogin();
        }}
        data-testid={formatDataTestId(baseDataTestId, 'Button')}
        {...buttonStyles}
        {...getTypographyProps({}, buttonSemanticTypography)}
      >
        {t('booking.login.labelHaveAccount')}
      </Button>
    </Box>
  );
}

const wrapperStyles = {
  w: 'full',
  mb: '2xl',
  maxWidth: { md: '49rem', xl: '55rem' },
  px: {
    mobile: '0',
    sm: 'md',
    md: '0',
    lg: '0',
  },
};

const headingStyles = {
  color: 'darkGrey1',
} as TextProps;

const headingLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: '2xl',
  lineHeight: '4',
} as TextProps;

const headingSemanticTypography = {
  textStyle: 'heading-m',
} as TextProps;

const descriptionStyles = {
  color: 'darkGrey1',
  mb: 'lg',
} as TextProps;

const descriptionLegacyTypography = {
  fontSize: 'md',
  lineHeight: '3',
} as TextProps;

const descriptionSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const buttonSemanticTypography = {
  textStyle: 'label-xl',
} as TextProps;

const buttonStyles = {
  width: '100%',
  maxWidth: '26.25rem',
};
