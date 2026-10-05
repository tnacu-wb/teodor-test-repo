'use client';

import { GET_PACKAGES, PackagesResponse } from '@whitbread-eos/api';
import { useMemo } from 'react';

import useCustomLocale from './use-custom-locale';
import { useQueryRequest } from './use-request';

interface ReturnProps extends PackagesResponse {
  isError: boolean;
  isLoading: boolean;
  error: any;
}
export interface Props {
  hotelId: string;
  bookingFlowId: string;
  startDate: string;
  endDate: string;
  nightsNumber: number;
  adultsNumber: number;
  childrenNumber: number;
  basketReferenceId: string;
  options?: any;
  upsellItemsAllowed?: string[];
  channel?: string;
}
export default function usePackages({
  adultsNumber,
  childrenNumber,
  nightsNumber,
  basketReferenceId,
  hotelId,
  startDate,
  endDate,
  bookingFlowId,
  options,
  upsellItemsAllowed,
  channel,
}: Props): ReturnProps {
  const { country, language } = useCustomLocale();

  const pckQuery = {
    adultsNumber: adultsNumber,
    childrenNumber: childrenNumber,
    nightsNumber: nightsNumber,
    basketReferenceId: basketReferenceId,
    hotelId: hotelId,
    startDate: startDate,
    endDate: endDate,
    bookingFlowId: bookingFlowId,
    language: language,
    country: country,
    channel,
  };

  const { data, isError, isLoading, error } = useQueryRequest(
    [
      'GetPackages',
      language,
      country,
      hotelId,
      bookingFlowId,
      startDate,
      endDate,
      nightsNumber,
      adultsNumber,
      childrenNumber,
      basketReferenceId,
      channel,
    ],
    GET_PACKAGES,
    {
      ...pckQuery,
    },
    { ...options }
  );

  return useMemo(() => {
    const packageResponse: PackagesResponse = data
      ? {
          ...data.packages,
          packages: {
            ...data.packages.packages,
            meals: upsellItemsAllowed
              ? data.packages.packages.meals.filter((meal: any) =>
                  upsellItemsAllowed.includes(meal.bartId)
                )
              : data.packages.packages.meals,
          },
        }
      : {
          packages: {},
          restaurant: {},
          privacyPolicy: {},
          hotelHasCityTaxForBusiness: false,
          hotelHasCityTaxForLeisure: false,
        };

    return { isLoading, isError, ...packageResponse, error };
  }, [data, isError, isLoading, error, upsellItemsAllowed]);
}
