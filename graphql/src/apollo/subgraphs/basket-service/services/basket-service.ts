import { getServiceEndpoint, replaceServiceEndpoint } from '../../../utils/base-utils';
import { get, post, put } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { mapRegistrationRole } from '../../piba-registration-service-opera/utils/map-registration-role';

export const getBasket = async (
  { basketReference }: { basketReference: string },
  context: any
): Promise<any> => {
  try {
    const basketPath = endpoints.BASKET.endpoint.replace(
      '{basketReference}',
      basketReference.toString()
    );

    const basketEndPoint = getServiceEndpoint(basketPath, endpoints.BASKET);

    return await get(basketEndPoint, getBasket, null, context);
  } catch (error: Error | any) {
    throw handleError(error, basketReference);
  }
};

export const getBasketStatus = async (
  { basketReference }: { basketReference: string },
  context: any
): Promise<any> => {
  try {
    const basketStatusPath = endpoints.BASKET_STATUS.endpoint.replace(
      '{basketReference}',
      basketReference.toString()
    );

    const basketStatusEndPoint = getServiceEndpoint(basketStatusPath, endpoints.BASKET_STATUS);

    return await get(basketStatusEndPoint, getBasketStatus, null, context);
  } catch (error: Error | any) {
    throw handleError(error, basketReference);
  }
};

export const confirmPreCheckIn = async (
  { basketReference, isCiol }: { basketReference: string; isCiol?: boolean },
  context: any
): Promise<any> => {
  try {
    let confirmPreCheckinPath = endpoints.CONFIRM_PRE_CHECKIN.endpoint.replace(
      '{basketReference}',
      basketReference.toString()
    );

    if (isCiol !== undefined) {
      confirmPreCheckinPath = `${confirmPreCheckinPath}?isCiol=${isCiol}`;
    }

    const confirmPreCheckinEndPoint = getServiceEndpoint(
      confirmPreCheckinPath,
      endpoints.CONFIRM_PRE_CHECKIN
    );

    const response = await put(confirmPreCheckinEndPoint, confirmPreCheckIn, null, context);
    return response.data;
  } catch (error: Error | any) {
    throw handleError(error, basketReference);
  }
};

export const sendEmailOption = async (
  { basketReference, sendEmailCriteria }: { basketReference: string; sendEmailCriteria: any },
  context: any
): Promise<any> => {
  try {
    const emailNotificationPath = endpoints.EMAIL_NOTIFICATION.endpoint.replace(
      '{basketReference}',
      basketReference.toString()
    );

    const emailNotificationEndPoint = getServiceEndpoint(
      emailNotificationPath,
      endpoints.EMAIL_NOTIFICATION
    );

    const response = await put(
      emailNotificationEndPoint,
      sendEmailOption,
      sendEmailCriteria,
      context
    );
    return response.data;
  } catch (error: Error | any) {
    throw handleError(error, { sendEmailCriteria, basketReference: basketReference });
  }
};

export const updateReservation = async (
  { updateReservationCriteria }: { updateReservationCriteria: any },
  context: any
): Promise<any> => {
  try {
    const response = await put(
      endpoints.UPDATE_RESERVATION,
      updateReservation,
      updateReservationCriteria,
      context
    );
    return response.data;
  } catch (error: Error | any) {
    handleError(error, updateReservationCriteria);
  }
};

export const confirmPreCheckOut = async (
  { basketReference }: { basketReference: string },
  context: any
): Promise<any> => {
  try {
    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.CONFIRM_PRE_CHECK_OUT,
      '{basketReference}',
      basketReference
    );

    const response = await put(serviceEndpoint, confirmPreCheckOut, {}, context);
    return response.data;
  } catch (error: Error | any) {
    throw handleError(error, basketReference);
  }
};

export const backgroundCharge = async (
  { basketReference, token }: { basketReference: string; token: string },
  context: any
): Promise<any> => {
  try {
    const backgroundChargeRequest = {
      basketReference,
      token
    };

    const response = await post(
      endpoints.BACKGROUND_CHARGE,
      backgroundCharge,
      backgroundChargeRequest,
      context
    );

    return {
      basketReference: basketReference
    };
  } catch (error: Error | any) {
    throw handleError(error, { basketReference, token });
  }
};
