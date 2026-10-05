import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../payment-info-messages-pipeline/actions/ActionContextKeys';
import * as ActionContextKeysBookingInformation from '../../booking-information-pipeline/actions/ActionContextKeys';
import { RateInformationResponse } from '../models/content-entity-models';
import { addFieldsToMap } from '../../../utils/base-utils';

/**
 * This method is used to fetch the hotel information
 *
 * @param args
 * @param context
 * @param pipelineContext
 */
export const fetchBookingRateInformation = async (
  args: any,
  context: any,
  pipelineContext: PipelineContext
): Promise<RateInformationResponse> => {
  try {
    let hotelInformation = pipelineContext.get(ActionContextKeys.HOTEL_INFORMATION);

    const rateInformationParams = {
      country: args.country,
      language: args.language,
      channel: args.bookingChannel,
      ratePlans: args.rateCode,
      hotelId: args.hotelId,
      brand: hotelInformation.brand.toLowerCase()
    };

    return await get(
      endpoints.RATE_INFORMATION,
      fetchBookingRateInformation,
      rateInformationParams,
      context
    );
  } catch (error) {
    throw handleError(error, args);
  }
};

export const fetchBookingInformation = async (
  args: any,
  context: any,
  pipelineContext: PipelineContext
): Promise<any> => {
  try {
    let bookingFlowId = pipelineContext.get(ActionContextKeys.BOOKING_FLOW_ID);
    const bookingInformationParams = {
      country: args.country,
      language: args.language,
      ratePlanCodes: args.rateCode,
      hotelId: args.hotelId,
      bookingFlowId: bookingFlowId
    };

    return await get(
      endpoints.BOOKING_INFORMATION,
      fetchBookingInformation,
      bookingInformationParams,
      context
    );
  } catch (error) {
    handleError(error, args);
  }
};

export const fetchBookingInfoMessages = async (
  args: any,
  context: any,
  pipelineContext: PipelineContext
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    let bookingFlowId = pipelineContext.get(
      ActionContextKeysBookingInformation.ActionContextKeys.BOOKING_FLOW_ID
    );
    let hotelInfo = pipelineContext.get(
      ActionContextKeysBookingInformation.ActionContextKeys.HOTEL_INFORMATION_FOR_BOOKING
    );
    const fieldToAdd = [
      { key: 'language', value: args.language, required: true },
      { key: 'country', value: args.country, required: true },
      { key: 'hotelId', value: hotelInfo.hotelId, required: true },
      { key: 'bookingFlowId', value: bookingFlowId, required: true }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    return await get(endpoints.BOOKING_INFO_MESSAGES, fetchBookingInfoMessages, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
