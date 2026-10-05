import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { handleError } from '../../../exception/error-handler';
import { ActionContextKeys } from '../../booking-information-pipeline/actions/ActionContextKeys';
import {
  addFieldsToMap,
  joinCriteriaNestedField,
  replaceServiceEndpoint
} from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';

export const fetchRateCodePricing = async (
  args: any,
  context: any,
  pipelineContext: PipelineContext
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    let bookingInfo = pipelineContext.get(ActionContextKeys.BOOKING_INFORMATION_BY_BASKET);
    if (
      bookingInfo.reservationByIdList[0].roomStay.ratePlanCode === 'FLEXRATE' ||
      bookingInfo.reservationByIdList[0].roomStay.ratePlanCode === 'EMPLOYEE' ||
      args.bookingChannelCriteria.channel === 'BB'
    ) {
      return 'SKIPPED';
    }

    const comma = ',';
    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.RATE_CODE_PRICING,
      '{hotelId}',
      bookingInfo.hotelId
    );
    const fieldsToAdd = [
      {
        key: 'arrivalDate',
        value: bookingInfo.reservationByIdList[0].roomStay.arrivalDate,
        required: true
      },
      {
        key: 'departureDate',
        value: bookingInfo.reservationByIdList[0].roomStay.departureDate,
        required: true
      },
      {
        key: 'ratePlanCode',
        value: args.upgradeToEmployeeRate ? 'EMPLOYEE' : 'FLEXRATE',
        required: true
      },
      {
        key: 'roomTypes',
        value: joinCriteriaNestedField(
          bookingInfo.reservationByIdList,
          'roomStay',
          'roomType',
          comma
        ),
        required: true
      },
      {
        key: 'adultsNo',
        value: joinCriteriaNestedField(
          bookingInfo.reservationByIdList,
          'roomStay',
          'adultsNumber',
          comma
        ),
        required: true
      },
      {
        key: 'childrenNo',
        value: joinCriteriaNestedField(
          bookingInfo.reservationByIdList,
          'roomStay',
          'childrenNumber',
          comma
        ),
        required: true
      },
      {
        key: 'reservationRatePlanCode',
        value: bookingInfo.reservationByIdList[0].roomStay.ratePlanCode,
        required: true
      }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(serviceEndpoint, fetchRateCodePricing, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
