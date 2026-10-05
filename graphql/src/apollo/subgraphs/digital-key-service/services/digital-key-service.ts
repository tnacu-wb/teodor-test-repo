import { post } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';

export const digitalKeyGenerateOtp = async ({ email }: { email: string }, context: any) => {
  try {
    return await post(endpoints.GENERATE_OTP, digitalKeyGenerateOtp, { email }, context);
  } catch (error: Error | any) {
    handleError(error, email);
  }
};

export const digitalKeyProvision = async (
  { digitalkeyProvisionRequest }: { digitalkeyProvisionRequest: any },
  context: any
) => {
  try {
    return await post(
      endpoints.PASS_PROVISIONING_WITH_OTP,
      digitalKeyProvision,
      digitalkeyProvisionRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, digitalkeyProvisionRequest);
  }
};

export const digitalKeyCheckIn = async (
  { digitalKeyCheckInCriteria }: { digitalKeyCheckInCriteria: any },
  context: any
) => {
  try {
    return await post(endpoints.CHECKIN, digitalKeyCheckIn, digitalKeyCheckInCriteria, context);
  } catch (error: Error | any) {
    handleError(error, digitalKeyCheckInCriteria);
  }
};

export const registerMobileDevice = async (
  { registerMobileDeviceRequest }: { registerMobileDeviceRequest: any },
  context: any
) => {
  try {
    return await post(
      endpoints.REGISTER_MOBILE_DEVICE,
      registerMobileDevice,
      registerMobileDeviceRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, registerMobileDeviceRequest);
  }
};

export const googleWalletProvisioning = async (
  { googleWalletProvisioningRequest }: { googleWalletProvisioningRequest: any },
  context: any
) => {
  try {
    return await post(
      endpoints.GOOGLE_WALLET_PROVISIONING,
      googleWalletProvisioning,
      googleWalletProvisioningRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, googleWalletProvisioningRequest);
  }
};
