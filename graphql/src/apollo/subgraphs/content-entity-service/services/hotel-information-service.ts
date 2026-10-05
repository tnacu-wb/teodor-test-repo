import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { HotelInformationRequest, HotelInformationResponse } from '../models/content-entity-models';
import { HotelsInformationCriteria } from '../models/hotels-information-criteria';
import {
  addFieldsToMap,
  getURL,
  joinCriteria,
  replaceServiceEndpoint
} from '../../../utils/base-utils';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../booking-information-pipeline/actions/ActionContextKeys';
import { PipelineAvailabilitySearchCriteria } from '../../hotel-availabilities-pipeline/models/availability-search-criteria';

/**
 * This method is used to fetch the hotel information
 *
 * @param args
 * @param context
 */
export const fetchHotelInformation = async (
  args: any,
  context: any
): Promise<HotelInformationResponse> => {
  try {
    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.HOTEL_INFORMATION,
      '{hotelId}',
      args.hotelId
    );

    const hotelInformationParams = {
      country: args.country,
      language: args.language
    };

    return await get(serviceEndpoint, fetchHotelInformation, hotelInformationParams, context);
  } catch (error) {
    throw handleError(error, args);
  }
};

export const getAllHotelsShortInformation = async (args: any, context: any): Promise<any> => {
  try {
    return await get(
      endpoints.ALL_HOTEL_SHORT_INFORMATION,
      getAllHotelsShortInformation,
      args,
      context
    );
  } catch (error) {
    handleError(error, args);
  }
};

export const retrieveHotelsInformation = async (
  hotelsInformationCriteria: HotelsInformationCriteria,
  context: any
): Promise<any> => {
  let queryParams: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      {
        key: 'hotelIds',
        value: joinCriteria(hotelsInformationCriteria.hotelIds, '', ','),
        required: true
      },
      { key: 'country', value: hotelsInformationCriteria.country, required: true },
      { key: 'language', value: hotelsInformationCriteria.language, required: true },
      { key: 'latitudeRef', value: hotelsInformationCriteria.latitudeRef, required: false },
      { key: 'longitudeRef', value: hotelsInformationCriteria.longitudeRef, required: false },
      { key: 'stayStartDate', value: hotelsInformationCriteria.stayStartDate, required: false },
      { key: 'stayEndDate', value: hotelsInformationCriteria.stayEndDate, required: false },
      {
        key: 'tripAdvisorDataRequired',
        value: hotelsInformationCriteria.tripAdvisorDataRequired,
        required: false
      }
    ];
    addFieldsToMap(fieldsToAdd, queryParams);

    return await get(
      endpoints.GET_HOTELS_INFORMATION,
      retrieveHotelsInformation,
      queryParams,
      context
    );
  } catch (error: Error | any) {
    throw handleError(error, queryParams);
  }
};

/**
 * This method is used to fetch the hotel information
 * @param language The language of the countries
 * @param country country name(gb,de)
 * @param site site name(PI,BB)
 * @returns The response hotel information
 */
export const getHotelInformation = async (
  hotelInformationRequest: HotelInformationRequest,
  context: any
): Promise<any> => {
  let queryParams: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'language', value: hotelInformationRequest.language, required: true },
      { key: 'country', value: hotelInformationRequest.country, required: true },
      { key: 'channel', value: hotelInformationRequest.bookingChannel?.channel, required: false },
      {
        key: 'subchannel',
        value: hotelInformationRequest.bookingChannel?.subchannel,
        required: false
      },
      { key: 'stayStartDate', value: hotelInformationRequest.stayStartDate, required: false },
      { key: 'stayEndDate', value: hotelInformationRequest.stayEndDate, required: false }
    ];
    addFieldsToMap(fieldsToAdd, queryParams);

    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.HOTELS_INFORMATION,
      '{hotel}',
      hotelInformationRequest.hotelId
    );

    return await get(serviceEndpoint, getHotelInformation, queryParams, context);
  } catch (error) {
    handleError(error, hotelInformationRequest);
  }
};

/**
 * This method is used to fetch the hotel information by slug
 * @param language The language of the countries
 * @param country country name(gb,de)
 * @param businessBooker as boolean
 * @returns The response hotel information
 */
export const getHotelInformationBySlug = async (args: any, context: any): Promise<any> => {
  try {
    return await get(
      endpoints.HOTELS_INFORMATION_BY_SLUG,
      getHotelInformationBySlug,
      args,
      context
    );
  } catch (error) {
    handleError(error, args);
  }
};

export const fetchHotelInformationForBooking = async (
  args: any,
  context: any,
  pipelineContext: PipelineContext
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    let bookingInfo = pipelineContext.get(ActionContextKeys.BOOKING_INFORMATION_BY_BASKET);
    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.HOTEL_INFORMATION_FOR_BOOKING,
      '{hotelId}',
      bookingInfo.hotelId
    );

    const fieldsToAdd = [
      { key: 'country', value: args.country, required: true },
      { key: 'language', value: args.language, required: true }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(serviceEndpoint, fetchHotelInformationForBooking, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getMultiHotelInformation = async (
  args: any,
  hotelAvailabilities: any,
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  const criteria: PipelineAvailabilitySearchCriteria = args.availabilitiesSearchCriteria;
  try {
    const comma = ',';
    const fieldsToAdd = [
      {
        key: 'hotelIds',
        value: joinCriteria(hotelAvailabilities, 'hotelId', comma),
        required: true
      },
      { key: 'country', value: criteria.country, required: true },
      { key: 'language', value: criteria.language, required: true },
      { key: 'stayStartDate', value: criteria.startDate, required: false },
      { key: 'stayEndDate', value: criteria.endDate, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    const endpointWithParams = getURL(endpoints.GET_HOTELS_INFORMATION.endpoint, finalMap);
    const serviceEndpoint = { ...endpoints.GET_HOTELS_INFORMATION, endpoint: endpointWithParams };
    return await get(serviceEndpoint, getMultiHotelInformation, null, context);
  } catch (error: Error | any) {
    throw handleError(error, finalMap);
  }
};
