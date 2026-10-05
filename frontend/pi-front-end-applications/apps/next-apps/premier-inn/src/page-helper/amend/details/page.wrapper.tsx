import { Box } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AmendConfInput, Area, PackagesCriteria, Customer, PageName } from '@whitbread-eos/api';
import { Alert, Notification } from '@whitbread-eos/atoms';
import { SEO as Seo } from '@whitbread-eos/molecules';
import { useCustomLocale, useAmendCookieValidation } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React from 'react';

import AmendPagePi from './page.pi';

interface Props {
  confirmationInput: AmendConfInput;
  pcksQueryInput: PackagesCriteria;
  variant: Area;
  queryClient: QueryClient;
  userDetails?: Customer;
  tempBookingReference?: string | null;
  status?: string | null;
}

export default function DetailsPage({
  confirmationInput,
  pcksQueryInput,
  queryClient,
  tempBookingReference,
  status,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { country, language } = useCustomLocale();
  const { isValid, token, basketReference, bookingReference, error } = useAmendCookieValidation();

  // Build confirmationInput from cookie values if not provided, always merge validated token
  const confInput: AmendConfInput = {
    ...(confirmationInput ?? {
      country,
      language,
      bookingReference: bookingReference as string,
      basketReference: basketReference as string,
    }),
    token: token as string,
  };

  // Render error state if validation fails
  if (!isValid) {
    const errorMessage = error === 'missing-token' ? t('amend.error.timeout') : t('errors.sorry');

    return (
      <Box m="lg" data-testid="amend-error-token">
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
      <AmendPagePi
        confirmationInput={confInput}
        pcksQueryInput={pcksQueryInput}
        tempBookingReference={tempBookingReference}
        status={status}
      />
    </QueryClientProvider>
  );
}
