'use client';

import { QueryClient } from '@tanstack/react-query';
import type {
  QueryBookingInformationArgs,
  BIResponse,
  Channel,
  HotelBrandType,
} from '@whitbread-eos/api';
import {
  GET_HEADER_BOOKING_FLOW_INFORMATION,
  GET_HEADER_BOOKING_INFORMATION,
} from '@whitbread-eos/api';
import { useRouter } from 'next/router';
import { useCallback, useEffect, useState } from 'react';

import { enhanceCheckoutStepList } from '../selectors';
import { analytics } from '../services';
import { isStringValid } from '../validators';
import useCustomLocale from './use-custom-locale';
import { graphQLRequest } from './use-request';

export interface Props {
  basketReference: string;
  channel: Channel;
  queryClient: QueryClient;
}

const BLACKLIST_ROUTES = ['/repeat-booking'];

export default function useHotelBrands({ basketReference, channel, queryClient }: Props) {
  const { country, language } = useCustomLocale();

  const { route } = useRouter() || { query: {}, route: '' };
  const activePage = route?.split('/')[channel === 'BB' ? 3 : 1];

  const [brand, setBrand] = useState<HotelBrandType | null>(null);
  const [stepProgress, setStepProgress] = useState({
    activeStep: 0,
    steps: [],
  });

  const isBlacklistedRoute = BLACKLIST_ROUTES.some((blacklistedElem) => blacklistedElem === route);

  useEffect(() => {
    if (isStringValid(basketReference) && !isBlacklistedRoute) {
      getBookingFlowInformation(basketReference, channel);
    }
  }, [basketReference]);

  useEffect(() => {
    if (stepProgress?.steps?.length > 0) {
      analytics.update({
        bookingStepsTotal: stepProgress.steps.length,
        bookingCurrentStep: stepProgress.activeStep,
      });
    }
  }, [stepProgress]);

  const getBookingFlowInformation = useCallback(
    async (basketReference: string, channel: Channel) => {
      const biQueryInput: QueryBookingInformationArgs = {
        basketReference: basketReference,
        country,
        language,
        bookingChannelCriteria: {
          channel: channel,
          subchannel: 'WEB',
          language: language === 'en' ? 'EN' : 'DE',
        },
      };

      const bookingInformationQuery = queryClient.fetchQuery({
        queryKey: [
          'GetBookingInformation',
          biQueryInput.language,
          biQueryInput.country,
          biQueryInput.basketReference,
        ],
        queryFn: () =>
          graphQLRequest(GET_HEADER_BOOKING_INFORMATION, biQueryInput) as Promise<BIResponse>,
      });
      await bookingInformationQuery.then(async (result) => {
        const bookingInfo = result?.bookingInformation;
        const { bookingFlowId = '', hotelId = '' } = bookingInfo ?? {};

        const bookingFlowInformationRequest = queryClient.fetchQuery({
          queryKey: ['GetBookingFlowInformation', bookingFlowId, hotelId, language, country],
          queryFn: () =>
            graphQLRequest(GET_HEADER_BOOKING_FLOW_INFORMATION, {
              bookingFlowId,
              hotelId,
              language,
              country,
            }) as Promise<any>,
        });

        await bookingFlowInformationRequest.then((bfiDataResp) => {
          const { bookingFlowSteps, brand } = bfiDataResp?.bookingFlowInformation || {
            bookingFlowSteps: [{}],
          };

          setBrand(brand);
          setStepProgress(enhanceCheckoutStepList(bookingFlowSteps, activePage) as any);
        });
      });
    },
    []
  );
  return { brand, stepProgress };
}
