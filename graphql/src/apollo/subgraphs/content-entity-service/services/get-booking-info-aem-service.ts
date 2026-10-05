import { addFieldsToMap } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { ActionContextKeys } from '../../packages-pipeline/actions/ActionContextKeys';

export const getBookingInfoAem = async (
  args: any,
  context: any,
  pipelineContext: PipelineContext
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const savedPackagesOpera = pipelineContext.get(ActionContextKeys.GET_SAVED_PACKAGES_OPERA);
    const fieldsToAdd = [
      { key: 'country', value: args.country, required: false },
      { key: 'language', value: args.language, required: false },
      { key: 'hotelId', value: args.hotelId, required: false },
      { key: 'bookingFlowId', value: args.bookingFlowId, required: false },
      { key: 'reservationRatePlanCode', value: savedPackagesOpera.ratePlanCode, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(endpoints.GET_BOOKING_INFO_AEM, getBookingInfoAem, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};
