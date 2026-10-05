import { endpoints } from './base-service';
import { addFieldsToMap, getURL, objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { ResendInvoiceRequest } from '../models/resend-invoice-request';

/**
 * This method is used to send the invoice email to a user.
 *
 * @param resendInvoiceRequest
 * @param context contains the headers and the client
 * @returns a string.
 */
export const resendInvoiceEmail = async (
  { resendInvoiceRequest }: { resendInvoiceRequest: ResendInvoiceRequest },
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};

    const fieldsToAdd = [
      {
        key: 'channel',
        value: resendInvoiceRequest.bookingChannel?.channel,
        required: !objectIsNullEmptyOrUndefined(resendInvoiceRequest.bookingChannel)
      },
      {
        key: 'subchannel',
        value: resendInvoiceRequest.bookingChannel?.subchannel,
        required: !objectIsNullEmptyOrUndefined(resendInvoiceRequest.bookingChannel)
      }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    const endpointWithParams = getURL(endpoints.RESEND_INVOICE_EMAIL.endpoint, finalMap);
    const serviceEndpoint = { ...endpoints.RESEND_INVOICE_EMAIL, endpoint: endpointWithParams };

    context.headers['WB-Authorization'] = context.headers?.['authorization']
      ? context.headers?.['authorization']
      : '';

    return await post(serviceEndpoint, resendInvoiceEmail, resendInvoiceRequest, context);
  } catch (error: Error | any) {
    handleError(error, resendInvoiceRequest);
  }
};
