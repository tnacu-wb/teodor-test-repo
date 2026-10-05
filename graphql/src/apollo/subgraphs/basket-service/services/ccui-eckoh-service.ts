import { getServiceEndpoint } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get, post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

export const getEckohRecordingStatus = async (
  { basketReference }: { basketReference: string },
  context: any
): Promise<any> => {
  try {
    const eckohRecordingStatusPath = endpoints.ECKOH_RECORDING_STATUS.endpoint.replace(
      '{basketReference}',
      basketReference.toString()
    );

    const eckohRecordingStatusPathEndPoint = getServiceEndpoint(
      eckohRecordingStatusPath,
      endpoints.ECKOH_RECORDING_STATUS
    );

    return await get(eckohRecordingStatusPathEndPoint, getEckohRecordingStatus, null, context);
  } catch (error: Error | any) {
    throw handleError(error, basketReference);
  }
};

export const initiateEckohPayment = async (
  { basketReference, eckohPaymentRequest }: { basketReference: string; eckohPaymentRequest?: any },
  context: any
) => {
  try {
    const initiateEckohPaymentPath = endpoints.INITIATE_ECKOH_PAYMENT.endpoint.replace(
      '{basketReference}',
      basketReference.toString()
    );

    const initiateEckohPaymentEndPoint = getServiceEndpoint(
      initiateEckohPaymentPath,
      endpoints.ECKOH_RECORDING_STATUS
    );

    return await post(
      initiateEckohPaymentEndPoint,
      initiateEckohPayment,
      eckohPaymentRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, eckohPaymentRequest);
  }
};
