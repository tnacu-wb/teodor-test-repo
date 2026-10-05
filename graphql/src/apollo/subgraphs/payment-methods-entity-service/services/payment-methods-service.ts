import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap, getServiceEndpoint } from '../../../utils/base-utils';

/**
 * This method is used to get payment methods
 * @param paymentMethodsCriteria The payment methods criteria object
 * @param context The context object
 * @returns The response containing payment methods
 */
export const getPaymentMethods = async (args: any, context: any): Promise<any> => {
  try {
    const paymentMethodsCriteria = {
      basketReference: args.paymentMethodsCriteria.basketReference,
      language: args.paymentMethodsCriteria.language,
      country: args.paymentMethodsCriteria.country,
      userType: args.paymentMethodsCriteria.userType,
      clientChannel: args.paymentMethodsCriteria.clientChannel,
      flow: args.paymentMethodsCriteria.flowType
    };
    return await get(endpoints.PAYMENT_METHODS, getPaymentMethods, paymentMethodsCriteria, context);
  } catch (error) {
    handleError(error, args);
  }
};

/**
 * This method is used to get CCUI payment methods
 * @param paymentCcuiMethodsCriteria The payment methods criteria object
 * @param context The context object
 * @returns The response containing CCUI payment methods
 */
export const getCcuiPaymentMethods = async (args: any, context: any): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      {
        key: 'basketReference',
        value: args.paymentCcuiMethodsCriteria.basketReference,
        required: true
      },
      { key: 'language', value: args.paymentCcuiMethodsCriteria.language, required: true },
      { key: 'country', value: args.paymentCcuiMethodsCriteria.country, required: true },
      { key: 'userId', value: args.paymentCcuiMethodsCriteria.userId, required: false },
      { key: 'userType', value: args.paymentCcuiMethodsCriteria.userType, required: false },
      {
        key: 'changePaymentBIC',
        value: args.paymentCcuiMethodsCriteria.changePaymentBIC,
        required: false
      }
    ];
    addFieldsToMap(fieldToAdd, finalMap);
    return await get(endpoints.CCUI_PAYMENT_METHODS, getCcuiPaymentMethods, finalMap, context);
  } catch (error) {
    handleError(error, args);
  }
};

/**
 * This method is used to get payment actions
 * @param basketReference basket reference
 * @param context The context object
 * @returns The response containing payment actions
 */
export const getCheckInOnlinePaymentActions = async (
  { basketReference }: { basketReference: string },
  context: any
): Promise<any> => {
  try {
    const paymentActionsPath = endpoints.PAYMENT_ACTIONS.endpoint.replace(
      '{basketReference}',
      basketReference.toString()
    );

    const paymentActionsEndPoint = getServiceEndpoint(
      paymentActionsPath,
      endpoints.PAYMENT_ACTIONS
    );

    return await get(paymentActionsEndPoint, getCheckInOnlinePaymentActions, null, context);
  } catch (error: Error | any) {
    throw handleError(error, basketReference);
  }
};
