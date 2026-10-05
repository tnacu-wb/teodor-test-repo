import { FT_PI_DATATRANS_INTEGRATION, GET_HOTEL_INFORMATION } from '@whitbread-eos/api';
import { useFeatureToggle, useQueryRequest } from '@whitbread-eos/utils';
import React from 'react';

import createPaymentPiDataLoaderFn from './data.pi';
import PaymentPagePiLegacy from './page.pi';
import PaymentPagePiDatatrans from './page.pi.datatrans';

function PaymentPagePiGate(
  props: React.ComponentProps<typeof PaymentPagePiLegacy> & { isDatatransReturn?: boolean }
) {
  const { [FT_PI_DATATRANS_INTEGRATION]: isDatatransEnabled } = useFeatureToggle();

  // On a 3DS return, getServerSideProps sets hiQueryInput to null and isDatatransReturn
  // to true. Skip the hotel-information query entirely — the Datatrans wrapper only needs
  // basketReference to call authorize and redirect; it never renders the payment UI.
  const { isLoading: isLoadingHotelInformation, data: hiData } = useQueryRequest(
    [
      'GetHotelInformation',
      props.hiQueryInput?.hotelId,
      props.hiQueryInput?.country,
      props.hiQueryInput?.language,
    ],
    GET_HOTEL_INFORMATION,
    { ...props.hiQueryInput },
    { enabled: !props.isDatatransReturn && !!props.hiQueryInput }
  );

  // Fast-path: render the Datatrans wrapper directly so it can authorize and redirect.
  // No hotel-information query result is needed for this path.
  if (props.isDatatransReturn) {
    return <PaymentPagePiDatatrans {...props} />;
  }

  if (isLoadingHotelInformation) {
    return <></>;
  }

  if (isDatatransEnabled && hiData?.hotelInformation?.isDataTransEnabled) {
    return <PaymentPagePiDatatrans {...props} />;
  }

  return <PaymentPagePiLegacy {...props} />;
}

const PaymentPagePi = PaymentPagePiGate;

export { createPaymentPiDataLoaderFn, PaymentPagePi };
