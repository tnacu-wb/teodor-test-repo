import { BartBookingInformationCriteria } from '../models/bart-booking-information-criteria';
import { handleError } from '../../../exception/error-handler';
import { addField, validateDateFormat } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';

export const getBartBookingInformation = async (
  {
    bartBookingInformationCriteria
  }: { bartBookingInformationCriteria: BartBookingInformationCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    // These fields are added because of uncertainty if these fields are required or not to be in
    // the structure, even with empty values
    addField(bartBookingInformationCriteria.bookingReference ?? '', 'bookingReference', finalMap);
    addField(bartBookingInformationCriteria.arrivalDate ?? '', 'arrivalDate', finalMap);
    addField(bartBookingInformationCriteria.bookerLastName ?? '', 'bookerLastName', finalMap);
    return await get(
      endpoints.BART_BOOKING_INFORMATION,
      getBartBookingInformation,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
