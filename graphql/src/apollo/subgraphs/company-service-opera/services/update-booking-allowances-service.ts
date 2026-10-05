import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { put } from '../../../client/rest-client';

/**
 * This method is used to update the booking allowances.
 *
 * @param companyId
 * @param bookingAllowances
 * @param context contains the headers and client
 * @returns a string
 */
export const updateBookingAllowances = async (
  { companyId, bookingAllowances }: { companyId: string; bookingAllowances: any },
  context: any
): Promise<any> => {
  try {
    const updateBookingAllowancesEndpoint = endpoints.UPDATE_BOOKING_ALLOWANCES.endpoint.replace(
      '{companyId}',
      companyId
    );
    const serviceEndpoint = {
      ...endpoints.UPDATE_BOOKING_ALLOWANCES,
      endpoint: updateBookingAllowancesEndpoint
    };

    const response = await put(
      serviceEndpoint,
      updateBookingAllowances,
      bookingAllowances,
      context
    );
    return response.data;
  } catch (error: Error | any) {
    handleError(error, { companyId: companyId, bookingAllowances });
  }
};
