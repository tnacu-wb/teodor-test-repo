import { post } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { addFieldsToMap } from '../../../utils/base-utils';
import { handleError } from '../../../exception/error-handler';

export const validatePaymentMethod = async (args: any, context: any): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'basketReference', value: args.basketReference, required: true },
      { key: 'type', value: args.createPaymentCriteria.payment.type, required: true },
      {
        key: 'selectedPaymentOption',
        value: args.createPaymentCriteria.booking.type,
        required: true
      },
      {
        key: 'isCiol',
        value: args.createPaymentCriteria.isCiol ?? false,
        required: false
      }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    return await post(endpoints.PAYMENT_METHODS, validatePaymentMethod, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
