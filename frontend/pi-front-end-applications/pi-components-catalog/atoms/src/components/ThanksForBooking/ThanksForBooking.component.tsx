import { Box, BoxProps, Text } from '@chakra-ui/react';
import { FT_PI_BB_NON_GUARANTEED_REMINDER } from '@whitbread-eos/api';
import {
  formatDataTestId,
  renderSanitizedHtml,
  getBookingConfirmationMessage,
  useFeatureToggle,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import React from 'react';

interface ThanksForBookingData {
  title: string;
  lastName: string;
  firstName: string;
  emailAddress: string;
}

interface ThanksForBookingProps {
  thanksForBooking: ThanksForBookingData | null;
}

interface Props extends BoxProps {
  data: ThanksForBookingProps;
  t: (x: string, y?: { [key: string]: string }) => string;
  currentLang: string | undefined;
  sendMail?: boolean;
}

export default function ThanksForBooking({
  data,
  t,
  currentLang,
  sendMail = true,
}: Readonly<Props>) {
  const router = useRouter();
  const getTypographyProps = useSemanticTypography();
  const { [FT_PI_BB_NON_GUARANTEED_REMINDER]: isSecureBookingFeatureEnabled } = useFeatureToggle();

  const { thanksForBooking } = data;
  const baseDataTestId = 'ThanksForBooking';

  if (!thanksForBooking) {
    return null;
  }

  const confirmationMessage = getBookingConfirmationMessage(
    t,
    router?.query,
    isSecureBookingFeatureEnabled
  );

  const { title, lastName, firstName, emailAddress } = thanksForBooking;
  return (
    <Box
      data-testid={formatDataTestId(baseDataTestId, 'Container')}
      {...contentWrapperStyle}
      sx={{ '@media print': { display: 'none' } }}
    >
      <Text
        className="sessioncamhidetext assist-no-show"
        {...titleLayoutStyles}
        {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
        data-testid={formatDataTestId(baseDataTestId, 'Title-Name')}
      >
        {confirmationMessage} {currentLang !== 'en' ? `${title} ${lastName}` : firstName}
      </Text>
      {emailAddress && sendMail && (
        <Text
          className="sessioncamhidetext assist-no-show"
          data-testid={formatDataTestId(baseDataTestId, 'Email')}
          {...getTypographyProps(
            emailConfirmationLegacyTypography,
            emailConfirmationSemanticTypography
          )}
        >
          {renderSanitizedHtml(
            t('booking.confirmation.confirmationEmailMessage').replace(
              '[emailAddress]',
              emailAddress
            )
          )}
        </Text>
      )}
    </Box>
  );
}
const contentWrapperStyle = {
  w: 'full',
};

const titleLayoutStyles = {
  mb: 'md',
};

const titleLegacyTypography = {
  fontSize: '3xxl',
  fontWeight: 'semibold',
};

const titleSemanticTypography = {
  textStyle: 'heading-xl',
};

const emailConfirmationLegacyTypography = {
  fontSize: 'md',
};

const emailConfirmationSemanticTypography = {
  textStyle: 'body-m-regular',
};
