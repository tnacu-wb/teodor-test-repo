import { endpoints } from './base-service';
import { AvailabilitySearchCriteria } from '../models/availability-search-criteria';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import {
  addField,
  addFieldsToMap,
  getServiceEndpoint,
  joinCriteria
} from '../../../utils/base-utils';
import { PipelineAvailabilitySearchCriteria } from '../../hotel-availabilities-pipeline/models/availability-search-criteria';
import { promotionsInformation } from '../../content-entity-service/services/global-config-service';

/**
 * This method is used to fetch the hotel availabilities by ids
 *
 * @param availabilityByIdsSearchCriteria
 * @param context
 */
export const getHotelAvailabilities = async (
  { availabilitySearchCriteria }: { availabilitySearchCriteria: AvailabilitySearchCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    let promotionInformationResponse = null;
    if (availabilitySearchCriteria?.isPromoBox !== undefined && availabilitySearchCriteria.brand) {
      const promotionArgs = {
        promotionsInformationCriteria: {
          promotionCode: availabilitySearchCriteria.promotionCode,
          country: availabilitySearchCriteria.country,
          language: availabilitySearchCriteria.bookingChannel?.language,
          channel: availabilitySearchCriteria.bookingChannel?.channel,
          brand: availabilitySearchCriteria.brand,
          stayStartDate: availabilitySearchCriteria.arrival,
          stayEndDate: availabilitySearchCriteria.departure,
          isPromoBox: availabilitySearchCriteria.isPromoBox,
          basketReference: availabilitySearchCriteria.originalBasketReference,
          rateName: availabilitySearchCriteria.rateName,
          roomClass: availabilitySearchCriteria.roomClass,
          subChannel: availabilitySearchCriteria.bookingChannel?.subchannel,
          noOfRooms: availabilitySearchCriteria.rooms?.length
        }
      };
      promotionInformationResponse = await promotionsInformation(promotionArgs, context);

      const promo = promotionInformationResponse;

      const isValidPromotion = !!promo.showPromo && !!promo.isWithinPromoWindow;

      if (isValidPromotion) {
        availabilitySearchCriteria.promoKind = promo.promoKind;
      } else {
        availabilitySearchCriteria.promotionCode = undefined;
      }
    }

    const hotelAvailabilityEndpoint = endpoints.HOTEL_AVAILABILITY.endpoint.replace(
      '{hotelId}',
      availabilitySearchCriteria.hotel.identifier
    );
    const singleAvailabilityEndPoint = getServiceEndpoint(
      hotelAvailabilityEndpoint,
      endpoints.HOTEL_AVAILABILITY
    );

    const comma = ',';
    const fieldsToAdd = [
      { key: 'hotelId', value: availabilitySearchCriteria.hotel.identifier, required: true },
      { key: 'arrivalDate', value: availabilitySearchCriteria.arrival, required: true },
      { key: 'departureDate', value: availabilitySearchCriteria.departure, required: true },
      {
        key: 'roomTypes',
        value: joinCriteria(availabilitySearchCriteria.rooms, 'roomType', comma),
        required: true
      },
      {
        key: 'adultsNumber',
        value: joinCriteria(availabilitySearchCriteria.rooms, 'adultsNumber', comma),
        required: true
      },
      {
        key: 'childrenNumber',
        value: joinCriteria(availabilitySearchCriteria.rooms, 'childrenNumber', comma),
        required: true
      },
      {
        key: 'cotsRequired',
        value: joinCriteria(availabilitySearchCriteria.rooms, 'cotRequired', comma),
        required: false
      },
      { key: 'companyId', value: availabilitySearchCriteria.companyId, required: false },
      {
        key: 'ratePlanCodes',
        value: joinCriteria(availabilitySearchCriteria.ratePlanCodes!, '', comma),
        required: false
      },
      {
        key: 'channel',
        value: availabilitySearchCriteria.bookingChannel?.channel,
        required: false
      },
      {
        key: 'subchannel',
        value: availabilitySearchCriteria.bookingChannel?.subchannel,
        required: false
      },
      {
        key: 'language',
        value: availabilitySearchCriteria.bookingChannel?.language,
        required: false
      },
      {
        key: 'country',
        value: availabilitySearchCriteria.country?.toLowerCase(),
        required: false
      },
      {
        key: 'softBundle',
        value: availabilitySearchCriteria.softBundle,
        required: false
      },
      {
        key: 'promotionCode',
        value: availabilitySearchCriteria.promotionCode ?? '',
        required: false
      },
      {
        key: 'originalBasketReference',
        value: availabilitySearchCriteria.originalBasketReference ?? '',
        required: false
      },
      {
        key: 'promoKind',
        value: availabilitySearchCriteria.promoKind ?? null,
        required: false
      },
      {
        key: 'isPromoBox',
        value: availabilitySearchCriteria.isPromoBox ?? null,
        required: false
      },
      {
        key: 'brand',
        value: availabilitySearchCriteria.brand ?? null,
        required: false
      },
      {
        key: 'rateName',
        value: availabilitySearchCriteria.rateName ?? null,
        required: false
      },
      {
        key: 'roomClass',
        value: availabilitySearchCriteria.roomClass ?? null,
        required: false
      }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    const hotelAvailabilityResponse = await get(
      singleAvailabilityEndPoint,
      getHotelAvailabilities,
      finalMap,
      context
    );
    return {
      ...hotelAvailabilityResponse,
      promotionsInformation: promotionInformationResponse
    };
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getMultiHotelAvailabilities = async (args: any, context: any): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const comma = ',';
    const criteria: PipelineAvailabilitySearchCriteria = args.availabilitiesSearchCriteria;
    const {
      rooms,
      country,
      language,
      oldWorldChannel,
      channel,
      subChannel,
      filters,
      ratePlanCodes,
      sortOption,
      startDate,
      endDate,
      place,
      companyId,
      page,
      initialPageSize,
      lazyLoadPageSize,
      sort
    } = criteria;
    const fieldsToAdd = [
      { key: 'roomTypes', value: joinCriteria(rooms, 'type', comma), required: true },
      {
        key: 'adultsNumber',
        value: joinCriteria(rooms, 'adultsNumber', comma),
        required: true
      },
      {
        key: 'childrenNumber',
        value: joinCriteria(rooms, 'childrenNumber', comma),
        required: true
      },
      { key: 'country', value: country, required: true },
      { key: 'language', value: language, required: true },
      { key: 'oldWorldChannel', value: oldWorldChannel, required: true },
      { key: 'channel', value: channel, required: true },
      { key: 'subChannel', value: subChannel, required: true },
      { key: 'filters', value: joinCriteria(filters!, '', comma), required: false },
      {
        key: 'ratePlanCodes',
        value: joinCriteria(ratePlanCodes!, '', comma),
        required: false
      },
      { key: 'rcPriceModifier', value: sortOption?.rcPriceModifier, required: false },
      { key: 'rcDistanceModifier', value: sortOption?.rcDistanceModifier, required: false },
      { key: 'rcHubModifier', value: sortOption?.rcHubModifier, required: false }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);
    addField(startDate ?? '', 'arrivalDate', finalMap);
    addField(endDate ?? '', 'departureDate', finalMap);
    addField(place?.location ?? '', 'location', finalMap);
    addField(place?.locationFormat ?? '', 'locationFormat', finalMap);
    addField(place?.radius ?? '', 'radius', finalMap);
    addField(place?.radiusUnit ?? '', 'radiusUnit', finalMap);
    addField(companyId ?? '', 'companyId', finalMap);
    addField(page ?? '', 'page', finalMap);
    addField(initialPageSize ?? '', 'initialPageSize', finalMap);
    addField(lazyLoadPageSize ?? '', 'lazyLoadPageSize', finalMap);
    addField(sort ?? '', 'sort', finalMap);

    return await get(
      endpoints.HOTEL_AVAILABILITIES,
      getMultiHotelAvailabilities,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    throw handleError(error, finalMap);
  }
};

export const getMultiHotelAvailabilitiesV2 = async (args: any, context: any): Promise<any> => {
  // Defensive: ensure criteria and rooms are valid
  const criteria: PipelineAvailabilitySearchCriteria = args?.availabilitiesSearchCriteria;
  const {
    rooms = [{ adultsNumber: 2, childrenNumber: 0, type: 'DB' }],
    country,
    language,
    oldWorldChannel,
    channel,
    subChannel,
    startDate,
    endDate,
    sortOption,
    filters,
    ratePlanCodes,
    place,
    companyId,
    page,
    initialPageSize,
    lazyLoadPageSize,
    sort
  } = criteria || {};
  if (!criteria) {
    return [];
  }
  let finalMap: { [key: string]: any } = {};
  try {
    const comma = ',';
    const fieldsToAdd = [
      { key: 'roomTypes', value: joinCriteria(rooms, 'type', comma), required: false },
      { key: 'adultsNumber', value: joinCriteria(rooms, 'adultsNumber', comma), required: true },
      {
        key: 'childrenNumber',
        value: joinCriteria(rooms, 'childrenNumber', comma),
        required: true
      },
      { key: 'country', value: country, required: true },
      { key: 'language', value: language, required: true },
      { key: 'oldWorldChannel', value: oldWorldChannel, required: true },
      { key: 'channel', value: channel, required: true },
      { key: 'subChannel', value: subChannel, required: true },
      { key: 'arrivalDate', value: startDate, required: false },
      { key: 'departureDate', value: endDate, required: false },
      { key: 'rcPriceModifier', value: sortOption?.rcPriceModifier, required: false },
      {
        key: 'rcDistanceModifier',
        value: sortOption?.rcDistanceModifier,
        required: false
      },
      {
        key: 'rcHubModifier',
        value: sortOption?.rcHubModifier,
        required: false
      },
      { key: 'filters', value: joinCriteria(filters!, '', comma), required: false },
      {
        key: 'ratePlanCodes',
        value: joinCriteria(ratePlanCodes!, '', comma),
        required: false
      }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    addField(place?.location ?? '', 'location', finalMap);
    addField(place?.locationFormat ?? '', 'locationFormat', finalMap);
    addField(place?.radius?.toString() ?? '', 'radius', finalMap);
    addField(place?.radiusUnit ?? '', 'radiusUnit', finalMap);
    addField(companyId ?? '', 'companyId', finalMap);
    addField(page ?? '', 'page', finalMap);
    addField(initialPageSize ?? '', 'initialPageSize', finalMap);
    addField(lazyLoadPageSize ?? '', 'lazyLoadPageSize', finalMap);
    addField(sort ?? '', 'sort', finalMap);
    return await get(
      endpoints.HOTEL_AVAILABILITIES,
      getMultiHotelAvailabilitiesV2,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    throw handleError(error, finalMap);
  }
};
