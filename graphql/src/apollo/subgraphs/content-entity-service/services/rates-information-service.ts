import { RatesInformationCriteria } from '../models/rates-information-criteria';
import { handleError } from '../../../exception/error-handler';
import {
  addFieldsToMap,
  joinCriteria,
  objectIsNullEmptyOrUndefined
} from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../booking-information-pipeline/actions/ActionContextKeys';

export const getRatesInformation = async (
  ratesInformationCriteria: RatesInformationCriteria,
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'brand', value: ratesInformationCriteria.brand, required: true },
      { key: 'language', value: ratesInformationCriteria.language, required: true },
      { key: 'country', value: ratesInformationCriteria.country, required: true },
      { key: 'hotelId', value: ratesInformationCriteria.hotelId, required: true },
      { key: 'channel', value: ratesInformationCriteria.channel, required: false },
      {
        key: 'ratePlans',
        value: joinCriteria(ratesInformationCriteria.ratePlans!, '', ','),
        required: false
      }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.RATES_INFORMATION, getRatesInformation, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getRatesInformationV2 = async (
  ratesInformationCriteria: RatesInformationCriteria,
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'brand', value: ratesInformationCriteria.brand, required: true },
      { key: 'language', value: ratesInformationCriteria.language, required: true },
      { key: 'country', value: ratesInformationCriteria.country, required: true },
      { key: 'hotelId', value: ratesInformationCriteria.hotelId, required: true },
      { key: 'channel', value: ratesInformationCriteria.channel, required: false },
      {
        key: 'ratePlans',
        value: joinCriteria(ratesInformationCriteria.ratePlans!, '', ','),
        required: false
      }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.RATES_INFORMATION, getRatesInformationV2, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const fetchRateInformation = async (
  args: any,
  context: any,
  pipelineContext: PipelineContext
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    let bookingInfo = pipelineContext.get(ActionContextKeys.BOOKING_INFORMATION_BY_BASKET);
    let hotelInfo = pipelineContext.get(ActionContextKeys.HOTEL_INFORMATION_FOR_BOOKING);

    const fieldToAdd = [
      { key: 'language', value: args.language, required: true },
      { key: 'country', value: args.country, required: true },
      { key: 'brand', value: hotelInfo.brand.toLowerCase(), required: true },
      {
        key: 'channel',
        value: !objectIsNullEmptyOrUndefined(args.bookingChannelCriteria?.channel)
          ? args.bookingChannelCriteria.channel
          : args.bookingChannel,
        required: false
      },
      {
        key: 'ratePlans',
        value: bookingInfo.reservationByIdList[0].roomStay.ratePlanCode,
        required: true
      },
      { key: 'hotelId', value: hotelInfo.hotelId, required: true }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    return await get(endpoints.GET_RATE_INFORMATION, fetchRateInformation, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
