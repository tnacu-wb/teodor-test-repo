import { Box, Text } from '@chakra-ui/react';
import { FT_PI_AUTH0_LOGIN } from '@whitbread-eos/api';
import {
  analytics,
  encodeToBase64,
  REGISTER_BASKET_REF_COOKIE,
  setCookie,
  useAuth0Navigation,
  useCustomLocale,
  useFeatureToggle,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import React from 'react';

import Button from '../Button';

interface BookerDetails {
  email?: string;
  firstName?: string;
  lastName?: string;
}

interface CreateAccountProps {
  basketReference: string | null;
  bookerDetails?: BookerDetails;
}

export default function CreateAccount({
  basketReference,
  bookerDetails,
}: Readonly<CreateAccountProps>) {
  const { t } = useTranslation(['common']);
  const router = useRouter();
  const { language, country } = useCustomLocale();
  const { [FT_PI_AUTH0_LOGIN]: isAuth0Enabled } = useFeatureToggle();
  const { navigateToSignup } = useAuth0Navigation();
  const getTypographyProps = useSemanticTypography();

  const handleClick = () => {
    if (isAuth0Enabled) {
      analytics.track('auth_sign_up_click', { authStep: 'sign_up_click' });
      navigateToSignup({
        email: bookerDetails?.email,
        firstName: bookerDetails?.firstName,
        lastName: bookerDetails?.lastName,
        basketReference: basketReference || '',
      });
    } else {
      const encodedValue = encodeToBase64(basketReference);
      setCookie(REGISTER_BASKET_REF_COOKIE, encodedValue, undefined);
      router.push(`/${country}/${language}/account/register?reservationId=${basketReference}`);
    }
  };

  return (
    <Box {...wrapperStyle}>
      <Text
        {...headingLayoutStyles}
        {...getTypographyProps(headingLegacyTypography, headingSemanticTypography)}
        data-testid="createAccount-heading"
      >
        {t('booking.confirmation.createAccount.heading')}
      </Text>
      <Text
        {...textLayoutStyles}
        {...getTypographyProps({}, textSemanticTypography)}
        data-testid="createAccount-text"
      >
        {t('booking.confirmation.createAccount.text')}
      </Text>
      <Button
        {...buttonStyles}
        data-testid="createAccount-button"
        variant="secondary"
        onClick={handleClick}
      >
        <Text>{t('booking.confirmation.createAccount.button')}</Text>
      </Button>
    </Box>
  );
}

const wrapperStyle = {
  w: 'full',
  border: '1px solid var(--chakra-colors-lightGrey1)',
  borderRadius: '3px',
  p: 'lg',
  mb: '2.5rem',
};

const headingLayoutStyles = {
  mb: 'lg',
};

const headingLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: '2xl',
  lineHeight: '4',
};

const headingSemanticTypography = {
  textStyle: 'heading-m',
};

const textLayoutStyles = {
  mb: 'lg',
};

const textSemanticTypography = {
  textStyle: 'body-m-regular',
};

const buttonStyles = {
  width: '100%',
  maxWidth: '288px',
};
