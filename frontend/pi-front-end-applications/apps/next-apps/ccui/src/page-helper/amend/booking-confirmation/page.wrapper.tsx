import { Box } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type {
  CONFIRM_AMEND_STATUS,
  AmendConfInput,
  BookingSpinnerConfig,
} from '@whitbread-eos/api';
import { PageName } from '@whitbread-eos/api';
import { Alert, Notification } from '@whitbread-eos/atoms';
import { SEO as Seo } from '@whitbread-eos/molecules';
import { useCustomLocale, useAmendCookieValidation } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import AmendBookingConfirmationPageCcui from './page.ccui';

export interface BookingConfirmationWrapperProps {
  confirmationInput: AmendConfInput;
  queryClient: QueryClient;
  amendBookingStatus?: CONFIRM_AMEND_STATUS;
  bookingSpinnerConfig: BookingSpinnerConfig[];
  tempBookingReference: string;
}

const BookingConfirmationWrapper = ({
  confirmationInput,
  queryClient,
  amendBookingStatus,
  bookingSpinnerConfig,
  tempBookingReference,
}: BookingConfirmationWrapperProps) => {
  const { t } = useTranslation();
  const { country, language } = useCustomLocale();
  const { isValid, token, basketReference, bookingReference, error } = useAmendCookieValidation();

  // Build confirmationInput from cookie values if not provided
  const confInput: AmendConfInput = confirmationInput ?? {
    country,
    language,
    bookingReference: bookingReference as string,
    basketReference: basketReference as string,
    token: token as string,
  };

  // Render error state if validation fails
  if (!isValid) {
    const errorMessage = error === 'missing-token' ? t('amend.error.timeout') : t('errors.sorry');

    return (
      <Box m="lg" data-testid="amend-confirmation-error-token">
        <Notification
          status="warning"
          description={errorMessage}
          variant="alert"
          maxW="full"
          svg={<Alert />}
        />
      </Box>
    );
  }

  return (
    <QueryClientProvider client={queryClient}>
      <Seo page={PageName.AMEND} noIndexNoFollow={true} />
      <AmendBookingConfirmationPageCcui
        queryClient={queryClient}
        confirmationInput={confInput}
        amendBookingStatus={amendBookingStatus}
        bookingSpinnerConfig={bookingSpinnerConfig}
        tempBookingReference={tempBookingReference}
      />
    </QueryClientProvider>
  );
};

export default BookingConfirmationWrapper;
