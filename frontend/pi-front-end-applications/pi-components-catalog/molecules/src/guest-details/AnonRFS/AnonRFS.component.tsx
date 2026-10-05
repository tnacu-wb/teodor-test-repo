import { HotelBrand, PurposeOfStay, PurposeOfStayAnalytics } from '@whitbread-eos/api';
import { FormDynamicFieldCompProps } from '@whitbread-eos/atoms';
import { analytics, useAuthToken } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

export const extractFormDetailsFlags = (basketReferenceId?: string, hotelBrand?: string) => {
  const defaultFlags = {
    rfs: HotelBrand.HUB === hotelBrand ? PurposeOfStay.LEISURE : '',
    basketReferenceId: '',
    updated: false,
  };
  if (typeof window === 'undefined') {
    return defaultFlags;
  }
  try {
    const formDetails = window.localStorage.getItem('formDetails');
    const formDetailsData = formDetails ? JSON.parse(formDetails) : {};
    if (formDetailsData?.basketReferenceId === basketReferenceId) {
      return {
        rfs: formDetailsData.reasonForStay,
        basketReferenceId,
        updated: formDetailsData.updated,
      };
    }
    return defaultFlags;
  } catch (error) {
    return defaultFlags;
  }
};

export default function AnonRFS({ formField }: FormDynamicFieldCompProps) {
  const [rfsRequested, setRfsRequested] = useState(false);

  const { token: authToken, isLoading: isAuthTokenLoading } = useAuthToken();

  useEffect(() => {
    if (isAuthTokenLoading) return;

    if (formField?.props?.basketReferenceId && formField?.props?.hotelBrand && !rfsRequested) {
      const formDetailsFlags = extractFormDetailsFlags(
        formField.props.basketReferenceId,
        formField.props.hotelBrand
      );
      if (!authToken || (authToken && formDetailsFlags.updated)) {
        if (formDetailsFlags.rfs === PurposeOfStay.BUSINESS) {
          analytics.update({ bookingReasonForStay: PurposeOfStayAnalytics.BUSINESS });
        } else if (formDetailsFlags.rfs === PurposeOfStay.LEISURE) {
          analytics.update({ bookingReasonForStay: PurposeOfStayAnalytics.LEISURE });
        }
        if (formDetailsFlags.rfs !== '') {
          formField?.props?.updateReasonForStay?.(formDetailsFlags.rfs);
          setRfsRequested(true);
        }
      }
    }
  }, [
    formField?.props?.basketReferenceId,
    formField?.props?.hotelBrand,
    authToken,
    isAuthTokenLoading,
    rfsRequested,
  ]);

  return null;
}
