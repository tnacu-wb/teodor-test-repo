import { handleError } from '../../../exception/error-handler';
import { get, post } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { addFieldsToMap } from '../../../utils/base-utils';

export const resendActivationEmail = async (
  { sendActivationRequest }: { sendActivationRequest: any },
  context: any
): Promise<any> => {
  try {
    return post(
      endpoints.RESEND_ACTIVATION_EMAIL,
      resendActivationEmail,
      sendActivationRequest,
      context
    );
  } catch (error) {
    handleError(error, sendActivationRequest);
  }
};

export const approveRejectEmployee = async (
  { approveRejectRequest }: { approveRejectRequest: any },
  context: any
): Promise<any> => {
  try {
    return post(
      endpoints.APPROVE_REJECT_EMPLOYEE,
      approveRejectEmployee,
      approveRejectRequest,
      context
    );
  } catch (error) {
    handleError(error, approveRejectRequest);
  }
};

export const getInnBusinessActivationDetails = async (
  { activationKey }: { activationKey: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};

  try {
    const fieldsToAdd = [{ key: 'activation-key', value: activationKey, required: true }];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(
      endpoints.GET_INN_BUSINESS_ACTIVATION_DETAILS,
      getInnBusinessActivationDetails,
      finalMap,
      context
    );
  } catch (error) {
    handleError(error, { finalMap });
  }
};

export const getInnBusinessActivationDetailsV2 = async (
  { activationKey }: { activationKey: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};

  try {
    const fieldsToAdd = [{ key: 'activation-key', value: activationKey, required: true }];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(
      endpoints.GET_INN_BUSINESS_ACTIVATION_DETAILS,
      getInnBusinessActivationDetailsV2,
      finalMap,
      context
    );
  } catch (error) {
    handleError(error, { finalMap });
  }
};
