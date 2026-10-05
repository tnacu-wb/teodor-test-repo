import { Box } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  AmendRoomAvailabilityLabels,
  Channel,
  GET_PROMO_INFORMATION,
  PageName,
  GET_HOTEL_AVAILABILITY_QUERY,
  BOOKING_SUBCHANNEL,
} from '@whitbread-eos/api';
import { PromotionsNotification } from '@whitbread-eos/atoms';
import { graphQLRequest, isSameDate, PromotionsInformation } from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { GraphQLClient } from 'graphql-request';

export function getGuestsPlaceholderString(
  adultsNumber: number,
  childrenNumber: number,
  labels: AmendRoomAvailabilityLabels
) {
  const { adult, adults, child, children } = labels;
  const adultsLabel = adultsNumber === 1 ? adult : adults;
  let childrenLabel = children;

  if (childrenNumber === 0) {
    childrenLabel = '';
  } else if (childrenNumber === 1) {
    childrenLabel = child;
  }

  return childrenNumber === 0
    ? `${adultsNumber} ${adultsLabel},`
    : `${adultsNumber} ${adultsLabel}, ${childrenNumber} ${childrenLabel},`;
}

export async function getPromotionsInformation(
  arrival: string | Date,
  departure: string | Date,
  country: string,
  language: string,
  brand: string,
  channel: Channel,
  basketReference: string,
  queryClient: QueryClient,
  client: GraphQLClient | undefined,
  isPromoCodeLandingPageEnabled: boolean,
  promotionCode?: string,
  isPromoBox?: boolean,
  rateName?: string,
  roomClass?: string
): Promise<PromotionsInformation | null> {
  if (!isPromoCodeLandingPageEnabled) {
    return null;
  }

  const startDate = format(new Date(arrival), 'yyyy-MM-dd');
  const endDate = format(new Date(departure), 'yyyy-MM-dd');

  const PromoResponse = await queryClient.fetchQuery({
    queryKey: [
      'promotionsInformation',
      country,
      language,
      brand as string,
      channel,
      startDate,
      endDate,
      basketReference,
      promotionCode,
      isPromoBox,
      rateName,
      roomClass,
    ],
    queryFn: () =>
      graphQLRequest(
        GET_PROMO_INFORMATION,
        {
          country: country,
          language: language,
          brand: brand as string,
          channel: channel,
          stayStartDate: startDate,
          stayEndDate: endDate,
          basketReference: basketReference,
          promotionCode: promotionCode ?? '',
          isPromoBox: isPromoBox ?? false,
          rateName,
          roomClass,
        },
        undefined,
        undefined,
        client
      ),
  });
  return PromoResponse?.promotionsInformation;
}

export function handleCheckDateIsSame(
  originalArrivalDate: Date,
  originalDepartureDate: Date,
  newArrivalDate: Date,
  newDepartureDate: Date
) {
  const isSameArrival = isSameDate(originalArrivalDate, newArrivalDate);
  const isSameDeparture = isSameDate(originalDepartureDate, newDepartureDate);
  if (isSameArrival && isSameDeparture) return;
}

export const renderPromoNotification = (promoRoomsData?: PromotionsInformation | null) => {
  if (!promoRoomsData?.showPromo) return null;

  return (
    <Box px="var(--chakra-space-lg)" py="var(--chakra-space-xl)">
      <PromotionsNotification
        page={PageName.AMEND}
        promotionBannerData={promoRoomsData as PromotionsInformation}
        elementName={PageName.ROOMS_AND_GUESTS}
      />
    </Box>
  );
};

export type GetAmendPromotionsInfoParams = {
  isPromotionsInHotelAvailabilityEnabled: boolean;
  isPromoCodeLandingPageEnabled: boolean;
  channel: Channel;
  hotelId: string;
  arrival: string | Date;
  departure: string | Date;
  country: string;
  language: string;
  brand: string;
  rooms: any[];
  originalBasketReference: string;
  queryClient: any;
  client: any;
  ratePlanCodes?: string[];
  isPromoBox?: boolean;
};

export async function getAmendPromotionsInfo({
  isPromotionsInHotelAvailabilityEnabled,
  isPromoCodeLandingPageEnabled,
  channel,
  hotelId,
  arrival,
  departure,
  country,
  language,
  brand,
  rooms,
  originalBasketReference,
  queryClient,
  client,
  ratePlanCodes,
  isPromoBox,
}: GetAmendPromotionsInfoParams): Promise<PromotionsInformation | null> {
  if (isPromotionsInHotelAvailabilityEnabled) {
    // hotelAvailability requires at least one room with adultsNumber populated.
    // At the "Add a room" stage the user hasn't picked room details yet, so
    // default to a single-adult placeholder room to satisfy the backend's
    // NotEmpty validation — this call is only used to check promo eligibility
    // before the modal opens, not to reserve an actual room configuration.
    const roomsPayload =
      rooms && rooms.length > 0
        ? rooms
        : [
            {
              adultsNumber: 1,
              childrenNumber: 0,
              cotRequired: false,
              roomType: '',
            },
          ];

    try {
      const response = await graphQLRequest(
        GET_HOTEL_AVAILABILITY_QUERY,
        {
          hotelId,
          arrival: typeof arrival === 'string' ? arrival : arrival.toISOString().split('T')[0],
          departure:
            typeof departure === 'string' ? departure : departure.toISOString().split('T')[0],
          rooms: roomsPayload,
          brand: brand.toLowerCase(),
          country,
          bookingChannel: {
            channel: channel.toUpperCase(),
            language: language.toUpperCase(),
            subchannel: BOOKING_SUBCHANNEL.WEB,
          },
          ...(ratePlanCodes?.length ? { ratePlanCodes } : {}),
          ...(originalBasketReference ? { originalBasketReference } : {}),
          ...(isPromoBox !== undefined ? { isPromoBox } : {}),
        },
        undefined,
        undefined,
        client
      );

      return (response?.hotelAvailability?.promotionsInformation as PromotionsInformation) ?? null;
    } catch (e) {
      console.error('[getAmendPromotionsInfo] hotelAvailability call failed:', e);
      return null;
    }
  }

  // Flag OFF — legacy path, unchanged
  return getPromotionsInformation(
    arrival,
    departure,
    country,
    language,
    brand,
    channel,
    originalBasketReference,
    queryClient,
    client,
    isPromoCodeLandingPageEnabled
  );
}
