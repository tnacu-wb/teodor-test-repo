import { PipelineContext } from '../../../pipeline/context/PipelineContext';
import { handleError } from '../../../exception/error-handler';
import { ActionContextKeys } from '../../donations-pipeline/actions/ActionContextKeys';
import {
  addFieldsToMap,
  joinCriteria,
  objectIsNullEmptyOrUndefined,
  replaceServiceEndpoint
} from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';

export const fetchDonationPackagesOpera = async (
  args: any,
  context: any,
  pipelineContext: PipelineContext
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const bookingInfo = pipelineContext.get(ActionContextKeys.BOOKING_INFORMATION);
    if (objectIsNullEmptyOrUndefined(bookingInfo.donation?.charityCodes)) {
      return;
    }

    const hotelInfo = pipelineContext.get(ActionContextKeys.HOTEL_INFORMATION);
    const fieldsToAdd = [
      {
        key: 'packageCodes',
        value: joinCriteria(bookingInfo.donation.charityCodes, '', ','),
        required: true
      }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.DONATION_PACKAGES_OPERA,
      '{hotelId}',
      hotelInfo.hotelId
    );

    return await get(serviceEndpoint, fetchDonationPackagesOpera, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
