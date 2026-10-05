import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { put } from '../../../client/rest-client';

/**
 * This method is used to update booking alerts.
 *
 * @param companyId
 * @param bookingAlerts
 * @param context contains the headers and the client
 * @returns a string.
 */
export const updateBookingAlerts = async (
  { companyId, bookingAlerts }: { companyId: string; bookingAlerts: any },
  context: any
): Promise<any> => {
  try {
    const updateBookingAlertsEndpoint = endpoints.UPDATE_BOOKING_ALERTS.endpoint.replace(
      '{companyId}',
      companyId
    );
    const serviceEndpoint = {
      ...endpoints.UPDATE_BOOKING_ALERTS,
      endpoint: updateBookingAlertsEndpoint
    };

    const response = await put(serviceEndpoint, updateBookingAlerts, bookingAlerts, context);
    return response.data;
  } catch (error: Error | any) {
    handleError(error, { companyId: companyId, bookingAlerts });
  }
};
