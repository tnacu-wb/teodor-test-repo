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
import { AgentMemo, AmendBookingConfirmationContainer } from '@whitbread-eos/organisms';
import { updateAmendPageAnalytics, useSessionStorage } from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import { useEffect } from 'react';

interface Props {
  queryClient: QueryClient;
  confirmationInput: AmendConfInput;
  amendBookingStatus?: CONFIRM_AMEND_STATUS;
  bookingSpinnerConfig: BookingSpinnerConfig[];
  tempBookingReference: string;
}

export default function AmendBookingConfirmationPageCCUI({
  queryClient,
  confirmationInput,
  amendBookingStatus,
  bookingSpinnerConfig,
  tempBookingReference,
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
    if (parsedAnalytics?.originalBookingReference === bookingReference)
      updateAmendPageAnalytics(parsedAnalytics, parsedAnalytics?.originalBookingReference);
  }, []);

  useEffect(() => {
    queryClient.invalidateQueries({ queryKey: ['manageBookingDashBoard'] });
  }, []);

  useEffect(() => {
    const cleanupConfirmAmendErrorValue = () => {
      setConfirmAmendErrorValue(INITIAL_CONFIRM_AMEND_ERROR_VALUE);
    };
    window.addEventListener('beforeunload', cleanupConfirmAmendErrorValue);
    return () => window.removeEventListener('beforeunload', cleanupConfirmAmendErrorValue);
  }, [setConfirmAmendErrorValue]);

  return (
    <>
      <AmendBookingConfirmationContainer
        router={router}
        queryClient={queryClient}
        basketReference={basketReference}
        bookingSpinnerConfig={bookingSpinnerConfig}
        tempBookingReference={tempBookingReference}
        bookingReference={bookingReference}
        variant={Area.CCUI}
        amendBookingStatus={amendBookingStatus}
      />
      <AgentMemo />
    </>
  );
}
