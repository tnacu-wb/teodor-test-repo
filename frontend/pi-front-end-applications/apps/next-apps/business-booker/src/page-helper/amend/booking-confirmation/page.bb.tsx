import { QueryClient } from '@tanstack/react-query';
import {
  AmendConfInput,
  Area,
  BookingSpinnerConfig,
  CONFIRM_AMEND_STATUS,
  INITIAL_CONFIRM_AMEND_ERROR_KEY,
  INITIAL_CONFIRM_AMEND_ERROR_VALUE,
  type AmendConfirmationErrorLS,
} from '@whitbread-eos/api';
import { AmendBookingConfirmationContainer } from '@whitbread-eos/organisms';
import { updateAmendPageAnalytics, useSessionStorage } from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import { useEffect } from 'react';

interface Props {
  queryClient: QueryClient;
  confirmationInput: AmendConfInput;
  amendBookingStatus?: CONFIRM_AMEND_STATUS;
  email?: string;
  bookingSpinnerConfig: BookingSpinnerConfig[];
  tempBookingReference: string;
  showSearch?: boolean;
}

export default function AmendBookingConfirmationPageBb({
  queryClient,
  confirmationInput,
  amendBookingStatus,
  email,
  bookingSpinnerConfig,
  tempBookingReference,
  showSearch = true,
}: Readonly<Props>) {
  const router = useRouter();
  const [, setConfirmAmendErrorValue] = useSessionStorage<AmendConfirmationErrorLS>(
    INITIAL_CONFIRM_AMEND_ERROR_KEY,
    INITIAL_CONFIRM_AMEND_ERROR_VALUE
  );
  const { basketReference, bookingReference } = confirmationInput;
  const amendAnalytics = sessionStorage.getItem('AmendAnalytics');
  const parsedAnalytics = amendAnalytics && JSON.parse(amendAnalytics);

  useEffect(() => {
    queryClient.invalidateQueries({ queryKey: ['manageBookingDashBoard'] });
  }, []);

  useEffect(() => {
    if (parsedAnalytics?.originalBookingReference === bookingReference)
      updateAmendPageAnalytics(parsedAnalytics, parsedAnalytics?.originalBookingReference);
  }, []);

  useEffect(() => {
    const cleanupConfirmAmendErrorValue = () => {
      setConfirmAmendErrorValue(INITIAL_CONFIRM_AMEND_ERROR_VALUE);
    };
    window.addEventListener('beforeunload', cleanupConfirmAmendErrorValue);
    return () => window.removeEventListener('beforeunload', cleanupConfirmAmendErrorValue);
  }, [setConfirmAmendErrorValue]);

  return (
    <AmendBookingConfirmationContainer
      router={router}
      queryClient={queryClient}
      basketReference={basketReference}
      bookingReference={bookingReference}
      tempBookingReference={tempBookingReference}
      variant={Area.BB}
      amendBookingStatus={amendBookingStatus}
      email={email}
      bookingSpinnerConfig={bookingSpinnerConfig}
      showSearch={showSearch}
    />
  );
}
