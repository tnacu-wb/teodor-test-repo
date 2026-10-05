import {
  BUSINESS_TETHER_LOGIN,
  businessTetherQuery,
  LOCALES,
  requestErrors,
  requestStatus,
  SEND_ACTIVATION_EMAIL,
  updateEmployee,
  updateProfileDetailsQuery,
  addEmployee,
  UPDATE_EMAIL_PREFERENCES,
  updateCompanyDetailsQuery,
  SAVE_CARD_MUTATION,
  updateBusinessQuestionsQuery,
  resetMemorableWordQuery,
  updateCompanyUserQuestionQuery,
  createCompanyUserQuestionQuery,
  addCardPIBA,
  updatePIBACardQuery,
  deleteCompanyCustomQuestionQuery,
  initializeApplicationQuery,
  activatePibaCardMutation,
  updateBookingAllowancesQuery,
  deleteApplicationQuery,
  updateAppContactDetailsQuery,
  updateAppCompanyDetailsQuery,
  UpdateAppContactDetailsCriteria,
  Scheme,
  resendActivationEmailQuery,
  InnBRegistrationStepOneQuery,
  REGISTER_USER_MARKETING,
  registrationStepTwoQuery,
  authenticateRegistrationMutation,
  AuthenticationAnswer,
  UpdateAppCompanyDetailsCriteria,
  updateBookingAlertsQuery,
  updatePaymentCard,
  deleteCardCDHMutation,
  cancelAndReplacePIBACardQuery,
  replacePIBACardMutation,
  resendCodeQuery,
  SHARE_APPLICATION,
  REMOVE_PARTICIPANT,
  approveRejectEmployeeQuery,
  ApproveRejectRequest,
  RegistrationDetails,
  submitRegistrationMutation,
  addPayAppCardMutation,
  deletePayAppCardMutation,
  AddApplicationCardDetails,
  forgotPasswordQuery,
  ForgottenPasswordRequest,
  ResetPasswordRequest,
  resetPasswordQuery,
  directDebitMutation,
  ValidateResetKeyRequest,
  validateResetKeyQuery,
  updateResumeUrlMutation,
  submitApplicationMutation,
  viewCustomerInvoicesQuery,
  CountryCode,
  CustomerAccountDetails,
  downloadBookingInvoiceMutation,
} from '@whitbread-eos/api';

import { getRandomTracingId } from '../edge';
import { executeGraphQLMutation } from '../gql';
import { findErrorCode, extractProfileContactErrorCodes } from '../helpers';

export type DownloadBookingInvoiceRequest = {
  bookingRef: string[];
  lang: string;
  channel: string;
  hotelBrand: string;
  subChannel: string;
};

type UpdateProfileDetailsResult = {
  status: requestStatus;
  error?: requestErrors;
  profileErrorCodes?: number[];
};

export const submitApplication = async (
  applicationGuid: string,
  applicationId: string,
  scheme: string,
  hostedPageGuid: string | null,
  registrationQuestion: string,
  registrationAnswer: string,
  termsAndConditionAccepted: boolean,
  isDirectDebit: boolean,
  token: string
) => {
  return await executeGraphQLMutation(
    submitApplicationMutation,
    {
      submitApplicationRequest: {
        applicationGuid,
        applicationId,
        scheme,
        hostedPageGuid: isDirectDebit ? hostedPageGuid : null,
        registrationQuestion,
        registrationAnswer,
        termsAndConditionAccepted: termsAndConditionAccepted ? 'Y' : 'n',
        isDirectDebit,
      },
    },
    (result: any) => {
      const response = result?.data?.submitApplicationV1?.message;
      if (response) {
        return { status: requestStatus.success };
      }
      return { status: requestStatus.fail, errors: result?.errors };
    },
    token
  );
};

export const addPayAppCard = async (
  applicationGuid: string,
  applicationId: string,
  scheme: string,
  cardDetails: AddApplicationCardDetails,
  token: string,
  employeeId?: number
) => {
  return await executeGraphQLMutation(
    addPayAppCardMutation(),
    {
      addApplicationCardCriteria: {
        applicationGuid,
        applicationId,
        employeeId,
        scheme,
        cardDetails,
      },
    },
    (result: any) => {
      const response = result?.data?.addApplicationCard?.cardGuid;
      if (response) {
        return { status: requestStatus.success, cardGuid: response };
      }
      return { status: requestStatus.fail, errors: result?.errors };
    },
    token
  );
};

export const deletePayAppCard = async (
  applicationGuid: string,
  applicationId: string,
  cardGuid: string,
  token: string
) => {
  return await executeGraphQLMutation(
    deletePayAppCardMutation(),
    {
      deleteApplicationCardRequest: {
        applicationGuid,
        applicationId,
        cardGuid,
      },
    },
    (result: any) => {
      const response = result?.data?.deleteApplicationCard;
      if (response === '') {
        return { status: requestStatus.success };
      }
      return { status: requestStatus.fail, errors: result?.errors };
    },
    token
  );
};

export const activatePibaCard = async (
  tetheredUserId: string,
  cardId: string,
  countryCode: string,
  token: string
) => {
  return await executeGraphQLMutation(
    activatePibaCardMutation(),
    {
      tetheredUserId,
      cardId,
      countryCode,
    },
    (result: any) => {
      const response = result?.data?.activateInnBPIBACard;
      if (response) {
        return { status: requestStatus.success };
      }
      return { status: requestStatus.fail, error: requestErrors.generic };
    },
    token
  );
};

export const addNewEmployee = async (
  companyId: string,
  languageCode: string,
  data: any,
  token: string
) => {
  return await executeGraphQLMutation(
    addEmployee(),
    {
      companyId,
      languageCode,
      employee: data,
    },
    (result: any) => {
      const successResponse = result?.data?.addEmployee;
      if (successResponse) {
        const splitResponse = successResponse.split('/');
        const employeeId = splitResponse[splitResponse.length - 1];
        return { status: requestStatus.success, employeeId };
      }
      return { status: requestStatus.fail, error: requestErrors.generic };
    },
    token
  );
};

export const addCardPIBAMutation = async (data: any, token: string) => {
  return await executeGraphQLMutation(
    addCardPIBA(),
    data,
    (result: any) => {
      const response = result?.data?.addInnBPIBACard;
      if (response?.cardNumber) {
        return { status: requestStatus.success, data: response };
      }
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };
      if (result?.errors?.length > 0) {
        const phoneNumberInvalid = findErrorCode(requestErrors.phoneNumberInvalid, result.errors);
        if (phoneNumberInvalid) {
          return { ...genericError, errorCode: requestErrors.phoneNumberInvalid };
        }
      }
      return genericError;
    },
    token
  );
};

export const updatePIBACardMutation = async (data: any, token: string) => {
  return await executeGraphQLMutation(
    updatePIBACardQuery(),
    data,
    (result: any) => {
      const response = result?.data?.updateInnBPIBACard;
      if (response) {
        return { status: requestStatus.success, data: response };
      }
      return { status: requestStatus.fail, error: requestErrors.generic };
    },
    token
  );
};

export const cancelAndReplacePIBACardMutation = async (data: any, token: string) => {
  return await executeGraphQLMutation(
    cancelAndReplacePIBACardQuery(),
    data,
    (result: any) => {
      const response = result?.data?.cancelAndReplaceInnBPIBACard;
      if (response) {
        return { status: requestStatus.success, data: response };
      }
      return { status: requestStatus.fail, error: requestErrors.generic };
    },
    token
  );
};

export const replaceCardMutation = async (data: any, token: string) => {
  return await executeGraphQLMutation(
    replacePIBACardMutation(),
    data,
    (result: any) => {
      const response = result?.data?.replaceCard;
      if (response) {
        return { status: requestStatus.success, data: response };
      }
      return { status: requestStatus.fail, error: requestErrors.generic };
    },
    token
  );
};

export const resendCodeMutation = async (data: any, token: string) => {
  return await executeGraphQLMutation(
    resendCodeQuery(),
    data,
    (result: any) => {
      const response = result?.data?.inviteCardHolder;
      if (response) {
        return { status: requestStatus.success, data: response };
      }
      return { status: requestStatus.fail, error: requestErrors.generic };
    },
    token
  );
};

export const updateEmployeeDetails = async (
  companyId: string,
  employeeId: string,
  languageCode: string,
  data: any,
  token: string,
  activationKey?: string
) => {
  return await executeGraphQLMutation(
    updateEmployee(),
    {
      companyId,
      employeeId,
      languageCode,
      updateEmployeeCriteria: data,
      activationKey,
    },
    (result: any) => {
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };

      if (result?.errors?.length > 0) {
        const lastTMError = findErrorCode(requestErrors.lastTravelManager, result.errors);

        if (lastTMError) {
          return { ...genericError, error: requestErrors.lastTravelManager };
        }
        return genericError;
      }
      if (result?.data?.updateEmployee === '') {
        return { status: requestStatus.success };
      }

      return genericError;
    },
    token
  );
};

export const updateProfileDetails = async (
  customerId: string,
  data: any,
  token: string
): Promise<UpdateProfileDetailsResult> => {
  return await executeGraphQLMutation(
    updateProfileDetailsQuery(),
    {
      customerId,
      business: true,
      payload: data,
      innBusiness: true,
    },
    (result: any) => {
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };

      if (result?.errors?.length > 0) {
        const currentPasswordNotMatch = findErrorCode(
          requestErrors.currentPasswordNotMatch,
          result.errors
        );
        if (currentPasswordNotMatch) {
          return { ...genericError, error: requestErrors.currentPasswordNotMatch };
        }
        const profileErrorCodes = extractProfileContactErrorCodes(result.errors);
        if (profileErrorCodes.length > 0) {
          return { ...genericError, profileErrorCodes };
        }
        return genericError;
      }

      if (result?.data?.updateProfileDetails.success) {
        return { status: requestStatus.success };
      }

      return genericError;
    },
    token
  );
};

export const updateCompanyDetails = async (companyId: string, data: any, token: string) => {
  return await executeGraphQLMutation(
    updateCompanyDetailsQuery(),
    {
      companyId,
      companySummary: data,
    },
    (result: any) => {
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };

      if (result?.data?.updateCompanyDetails === '') {
        return { status: requestStatus.success };
      }

      return genericError;
    },
    token
  );
};
export const updateBusinessQuestions = async (
  companyId: string,
  questionId: string,
  data: any,
  token: string
) => {
  return await executeGraphQLMutation(
    updateBusinessQuestionsQuery(),
    {
      companyId,
      questionId,
      userQuestion: data,
    },
    (result: any) => {
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };

      if (result?.data?.updateBusinessQuestions === '') {
        return { status: requestStatus.success };
      }

      return genericError;
    },
    token
  );
};

export const updateCompanyUserQuestion = async (
  companyId: string,
  questionId: string,
  data: any,
  token: string
) => {
  return await executeGraphQLMutation(
    updateCompanyUserQuestionQuery(),
    {
      companyId,
      questionId,
      userQuestion: data,
    },
    (result: any) => {
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };

      if (result?.data?.updateCompanyUserQuestion === '') {
        return { status: requestStatus.success };
      }

      return genericError;
    },
    token
  );
};

export const updateBookingAllowances = async (companyId: string, data: any, token: string) => {
  return await executeGraphQLMutation(
    updateBookingAllowancesQuery(),
    {
      companyId,
      bookingAllowances: data,
    },
    (result: any) => {
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };

      if (result?.data?.updateBookingAllowances === '') {
        return { status: requestStatus.success };
      }

      return genericError;
    },
    token
  );
};
export const createCompanyUserQuestion = async (companyId: string, data: any, token: string) => {
  return await executeGraphQLMutation(
    createCompanyUserQuestionQuery(),
    {
      companyId,
      userQuestion: data,
    },
    (result: any) => {
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };

      if (result?.data?.createCompanyUserQuestion === '') {
        return { status: requestStatus.success };
      }

      return genericError;
    },
    token
  );
};
export const deleteCompanyUserQuestion = async (
  companyId: string,
  questionId: string,
  token: string
) => {
  return await executeGraphQLMutation(
    deleteCompanyCustomQuestionQuery(),
    {
      companyId,
      questionId,
    },
    (result: any) => {
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };

      if (result?.data?.deleteCustomQuestion === '') {
        return { status: requestStatus.success };
      }

      return genericError;
    },
    token
  );
};

export const deleteCDHCard = async (companyId: string, cardId: string, token: string) => {
  return await executeGraphQLMutation(
    deleteCardCDHMutation(),
    {
      companyId,
      cardId,
    },
    (result: any) => {
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };

      if (result?.data?.deleteCompanyCard === '') {
        return { status: requestStatus.success };
      }

      return genericError;
    },
    token
  );
};

export const businessTetherLogin = async (token: string, tetheredGuid: string, scheme: Scheme) => {
  return await executeGraphQLMutation(
    BUSINESS_TETHER_LOGIN,
    {
      loginCriteria: {
        guid: tetheredGuid,
        scheme: scheme,
      },
    },
    (result: any) => result?.data?.businessTetherLogin,
    token
  );
};

export const businessTether = async (
  linkCode: string,
  linkId: string,
  memorableWord: string,
  saveInCdh: boolean,
  token: string
) => {
  return await executeGraphQLMutation(
    businessTetherQuery,
    {
      linkCode: linkCode,
      linkId: linkId,
      memorableWord: memorableWord,
      saveInCdh: saveInCdh,
    },
    (result: any) => result?.data?.businessTether,
    token
  );
};

export const sendActivationEmail = async (
  token: string,
  companyId: string,
  languageCode: string,
  emailAddress: string,
  centralCardId: string
) => {
  return await executeGraphQLMutation(
    SEND_ACTIVATION_EMAIL,
    {
      companyId,
      languageCode,
      sendActivationEmailCriteria: {
        emailAddress,
        centralCardId,
      },
    },
    (result: any) => result?.data?.sendActivationEmail,
    token
  );
};

export const bulkUploadEmployees = async (
  token: string,
  companyId: string,
  file: File,
  locale: LOCALES
) => {
  try {
    const data = new FormData();
    data.append('upFile', file);

    const res = await fetch(
      `${process.env.NEXT_PUBLIC_REST_API}/companies/${companyId}/employees/bulk?innBusiness=true`,
      {
        method: 'POST',
        headers: {
          Origin: process.env.NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC!,
          Authorization: `Bearer ${token}`,
          Language: locale === LOCALES.EN ? 'en' : 'de',
        },
        cache: 'no-cache',
        body: data,
      }
    );

    return res;
  } catch (error) {
    return null;
  }
};

export const updateContactPreferences = async (
  token: string,
  locale: string,
  preferences: {
    optIn?: boolean;
    secondPartyOptIn?: boolean;
    thirdPartyVendorsOptIn?: boolean;
  }
) => {
  try {
    const response = await executeGraphQLMutation(
      UPDATE_EMAIL_PREFERENCES,
      {
        request: {
          brandCodes: ['PINN'],
          doubleOptIn: locale === LOCALES.DE,
          customer: {
            language: locale === LOCALES.EN ? 'en' : 'de',
            countryOfResidence: locale === LOCALES.EN ? 'GB' : 'DE',
            firstName: '',
            lastName: '',
            title: '',
            userId: '',
          },
          optIn: preferences.optIn ?? false,
          secondPartyOptIn: preferences.secondPartyOptIn ?? false,
          thirdPartyVendorsOptIn: preferences.thirdPartyVendorsOptIn ?? false,
          sourceDetails: {
            channel: 'BB',
            journey: 'PERMISSIONCENTRE',
            locale: locale === LOCALES.EN ? 'UK' : 'DE',
          },
        },
      },
      (result: unknown) => result,
      token
    );

    if (response?.data?.updateEmailPreferences === '') {
      return { status: requestStatus.success };
    }

    if (response?.errors) {
      return { status: requestStatus.fail, error: requestErrors.generic };
    }

    return { status: requestStatus.fail, error: requestErrors.generic };
  } catch (error) {
    return { status: requestStatus.fail, error: requestErrors.generic };
  }
};

type SaveCardResult = {
  paymentRedirect: string;
  providerUrl: string;
  template: string;
  sessionId: string;
};

export const saveCard = async ({
  token,
  profileDetails,
  onSuccess,
  onError,
  t,
  isPiba = false,
  language,
  country = CountryCode.GB,
  memorableWord,
  cnpEnabled = false,
  isPreferenceCard,
  cardLabel,
  cardId = '',
}: {
  token: string;
  employeeId: string;
  profileDetails: { contactDetail?: { address?: Record<string, any> } };
  onSuccess: (data: SaveCardResult) => void;
  onError: (message: string) => void;
  t: (key: string) => string;
  isPiba?: boolean;
  language: string;
  country?: CountryCode;
  memorableWord?: string;
  cnpEnabled?: boolean;
  isPreferenceCard: boolean;
  cardLabel?: string;
  cardId?: string;
}): Promise<SaveCardResult | null> => {
  try {
    const requestId = getRandomTracingId();
    const address = profileDetails?.contactDetail?.address || {};

    const baseInitiateSaveCardRequest = {
      requestId,
      cardDetails: {
        cardLabel: cardLabel ?? undefined,
        cardType: isPiba ? 'PIBA' : 'CARD',
        business: true,
        cardId: isPreferenceCard ? '1' : cardId,
        cnpRequired: cnpEnabled,
        personalCard: isPreferenceCard,
        ...(isPiba && memorableWord ? { memorableWord } : {}),
      },
      billingAddress: {
        line1: address.line1 || '',
        line2: address.line2 || '',
        line3: address.line3 || '',
        line4: address.line4 || '',
        line5: address.line5 || '',
        countryCode: address.countryCode || 'GB',
        postCode: address.postCode || '',
        companyName: address.companyName || '',
        type: address.type || 'BUSINESS',
      },
      environment:
        process.env.NODE_ENV === 'development'
          ? `https://${process.env.NEXT_PUBLIC_ASSETS_URL}`
          : window.location.origin,
      language,
      country: country,
    };

    const executeQuery = async (variables: any) =>
      executeGraphQLMutation(
        SAVE_CARD_MUTATION,
        variables,
        (data: any) => {
          if (data?.errors?.length > 0) return null;
          return data?.data?.saveCard;
        },
        token
      );

    const result = await executeQuery({
      initiateSaveCardRequest: baseInitiateSaveCardRequest,
    });

    if (!result) {
      throw new Error(t('Failed to save card'));
    }

    onSuccess(result);
    return result;
  } catch (error: any) {
    onError(error.message || 'Error saving card');
    throw error;
  }
};

export const resetMemorableWord = async (
  tetheredUserGuid: string,
  memorableWord: string,
  token: string,
  scheme: string
) => {
  return await executeGraphQLMutation(
    resetMemorableWordQuery(),
    {
      tetheredUserGuid,
      memorableWord,
      scheme,
    },
    (result: any) => {
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };

      if (result?.data?.resetMemorableWord?.resultCode === 'OK') {
        return { status: requestStatus.success };
      }

      return genericError;
    },
    token
  );
};

export const initializePayApplication = async (token: string, email: string, scheme: Scheme) => {
  return await executeGraphQLMutation(
    initializeApplicationQuery(),
    {
      email,
      scheme,
    },
    (result: any) => result?.data?.initializeApplication,
    token
  );
};

export const deleteApplication = async (
  applicationId: string,
  applicationGuid: string,
  scheme: Scheme,
  token: string
) => {
  return await executeGraphQLMutation(
    deleteApplicationQuery(),
    {
      applicationId,
      applicationGuid,
      scheme,
    },
    (result: any) => result.data.deleteApplication,
    token
  );
};

export const updateAppContactDetails = async (
  token: string,
  criteria: UpdateAppContactDetailsCriteria
) => {
  return await executeGraphQLMutation(
    updateAppContactDetailsQuery(),
    {
      updateAppContactDetailsCriteria: criteria,
    },
    (result: any) => {
      const response = result?.data?.updateAppContactDetails;

      if (response) {
        return { status: requestStatus.success };
      }

      return { status: requestStatus.fail, errors: result?.errors };
    },
    token
  );
};

export const updateAppCompanyDetails = async (
  token: string,
  criteria: UpdateAppCompanyDetailsCriteria
) => {
  return await executeGraphQLMutation(
    updateAppCompanyDetailsQuery(),
    {
      updateAppCompanyDetailsCriteria: criteria,
    },
    (result: any) => {
      const response = result?.data?.updateAppCompanyDetails;

      if (response) {
        return { status: requestStatus.success };
      }

      return { status: requestStatus.fail, errors: result?.errors };
    },
    token
  );
};

export const updateCDHCard = async (token: string, data: Record<string, any>) => {
  return await executeGraphQLMutation(
    updatePaymentCard(),
    {
      companyId: data.companyId,
      cardId: data.cardId,
      cardLabel: data.cardLabel,
      cardType: data.cardType,
      cardNumber: data.cardNumber,
      expiryDate: data.expiryDate,
      cardHolderName: data.cardHolderName,
      cardToken: data.cardToken,
      billingAddress: data.billingAddress,
      cnpRequired: data.cnpRequired,
      cnpBusinessAccountPassword: data.memorableWord,
    },
    (result: any) => {
      const response = result?.data?.updatePaymentCard;

      if (response === '{success=true}') {
        return { status: requestStatus.success };
      }

      return { status: requestStatus.fail, errors: result?.errors };
    },
    token
  );
};

export const resendActivationEmail = async (
  email: string,
  companyName: string,
  language: string
) => {
  return await executeGraphQLMutation(
    resendActivationEmailQuery(),
    {
      sendActivationRequest: {
        email,
        companyName,
        language,
      },
    },
    (result: any) => result.data.resendActivationEmail
  );
};

export const InnBRegistrationStepOne = async (innBRegistrationStepOneRequest: any) => {
  const response = await executeGraphQLMutation(
    InnBRegistrationStepOneQuery(),
    {
      innBRegistrationStepOneRequest: {
        ...innBRegistrationStepOneRequest,
      },
    },
    (result: any) => {
      return result.data;
    }
  );
  return response;
};

export const updateMarketingPreferences = async (updateMarketingPreferencesRequest: any) => {
  const response = await executeGraphQLMutation(
    REGISTER_USER_MARKETING,
    { ...updateMarketingPreferencesRequest },
    (result: unknown) => result,
    ''
  );
  return response;
};

export const registrationStepTwo = async (innBRegistrationStepTwoRequest: any) => {
  return await executeGraphQLMutation(
    registrationStepTwoQuery(),
    {
      innBRegistrationStepTwoRequest: {
        ...innBRegistrationStepTwoRequest,
      },
    },
    (result: any) => result?.data?.innBRegistrationStepTwo
  );
};

export const authenticateRegistration = async (
  token: string,
  registrationCode: string,
  authenticationAnswers: AuthenticationAnswer[] = []
) => {
  return await executeGraphQLMutation(
    authenticateRegistrationMutation(),
    {
      registrationCode,
      authenticationAnswers,
    },
    (result: any) => result.data.authenticateRegistration,
    token
  );
};

export const updateBookingAlerts = async (
  companyId: string,
  bookingAlerts: {
    unitedKingdom: string | number;
    greaterLondon: string | number;
    germanyIreland: string | number;
  },
  token: string
): Promise<{ status: string; error?: string }> => {
  return await executeGraphQLMutation(
    updateBookingAlertsQuery,
    {
      companyId,
      bookingAlerts,
    },
    (result: { errors?: Array<{ message: string }>; data?: { updateBookingAlerts: string } }) => {
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };

      if (result?.errors && result.errors.length > 0) {
        return genericError;
      }

      if (result?.data?.updateBookingAlerts === '') {
        return { status: requestStatus.success };
      }

      return genericError;
    },
    token
  );
};

export const shareApplication = async (
  applicationId: string,
  applicationGuid: string,
  employeeId: number,
  token: string,
  email?: string,
  fullName?: string
) => {
  if (!applicationId || !applicationGuid) {
    throw new Error('Application ID and GUID are required');
  }

  if (!email || !fullName) {
    throw new Error('Email and full name are required for sharing application');
  }

  const shareRequest = {
    shareApplicationRequest: {
      applicationId,
      applicationGuid,
      employeeId,
      email,
      fullName,
    },
  };

  return await executeGraphQLMutation(
    SHARE_APPLICATION,
    shareRequest,
    (result) => {
      return result?.data?.shareApplication;
    },
    token
  );
};

export const removeParticipant = async (
  applicationId: string,
  applicationGuid: string,
  participantId: string | number,
  token: string
) => {
  const employeeId =
    typeof participantId === 'string' ? parseInt(participantId, 10) : participantId;

  return await executeGraphQLMutation(
    REMOVE_PARTICIPANT,
    {
      removeParticipantRequest: {
        applicationId,
        applicationGuid,
        employeeId,
      },
    },
    (result) => result?.data?.removeParticipant,
    token
  );
};

export const approveRejectEmployee = async (
  approveRejectRequest: ApproveRejectRequest,
  token: string
): Promise<{ status: string; error?: string }> => {
  return await executeGraphQLMutation(
    approveRejectEmployeeQuery(),
    {
      approveRejectRequest,
    },
    (result: { errors?: Array<{ message: string }>; data?: { approveRejectEmployee: string } }) => {
      const genericError = { status: requestStatus.fail, error: requestErrors.generic };

      if (result?.errors && result.errors.length > 0) {
        return genericError;
      }

      if (result?.data?.approveRejectEmployee === '') {
        return { status: requestStatus.success };
      }

      return genericError;
    },
    token
  );
};

export const submitRegistration = async (
  token: string,
  registrationCode: string,
  registrationDetails: RegistrationDetails,
  authenticationAnswers: AuthenticationAnswer[] = []
) => {
  return await executeGraphQLMutation(
    submitRegistrationMutation(),
    {
      registrationSubmitRequest: {
        innBusiness: true,
        registrationCode,
        registrationDetails,
        authenticationAnswers,
      },
    },
    (result: any) => result.data.submitRegistration,
    token
  );
};

export const forgotPassword = async (
  language: string,
  innBusiness: boolean,
  forgottenPasswordRequest: ForgottenPasswordRequest
) => {
  return await executeGraphQLMutation(
    forgotPasswordQuery(),
    {
      language,
      innBusiness,
      forgottenPasswordRequest,
    },
    (result: any) => result.data.forgotPassword
  );
};

export const resetPassword = async (
  passwordToken: string,
  resetPasswordRequest: ResetPasswordRequest
) => {
  return await executeGraphQLMutation(
    resetPasswordQuery(),
    {
      payload: resetPasswordRequest,
    },
    (result: any) => result.data.resetPassword.passwordChanged,
    undefined,
    { 'password-token': passwordToken }
  );
};

export const directDebit = async (
  token: string,
  applicationId: string,
  applicationGuid: string,
  directDebitOption: string,
  resumeUrl: string
) => {
  return await executeGraphQLMutation(
    directDebitMutation(),
    {
      directDebitRequest: {
        applicationGuid,
        applicationId,
        directDebitOption,
        resumeUrl,
      },
    },
    (result: any) => {
      const response = result?.data?.directDebit;

      if (response) {
        return { status: requestStatus.success, data: response };
      }

      return { status: requestStatus.fail, errors: result?.errors };
    },
    token
  );
};

export const validateResetKey = async (validateResetKeyRequest: ValidateResetKeyRequest) => {
  return await executeGraphQLMutation(
    validateResetKeyQuery(),
    {
      validateResetKeyRequest,
    },
    (result: any) => result.data.validateResetKey
  );
};

export const updateResumeUrl = async (
  token: string,
  applicationId: string,
  applicationGuid: string,
  resumeUrl: string
) => {
  return await executeGraphQLMutation(
    updateResumeUrlMutation(),
    {
      updateResumeUrlRequest: {
        applicationId,
        applicationGuid,
        resumeUrl,
      },
    },
    (result: any) => result.data.updateResumeUrl,
    token
  );
};

export const viewCustomerInvoices = async (
  token: string,
  account: CustomerAccountDetails,
  dateFrom: string,
  dateTo: string,
  page: number,
  maximumDisplayRows: number
) => {
  return await executeGraphQLMutation(
    viewCustomerInvoicesQuery(),
    {
      payload: {
        tetheredUserGuid: account.tetheredGuid,
        schemeCustomerId: account.schemeCustomerId,
        scheme: account.scheme,
        searchCriteria: {
          dateFrom,
          dateTo,
        },
        pagingRequest: {
          page,
          maximumDisplayRows,
        },
      },
    },
    (result: any) => result.data.viewCustomerInvoicesV2,
    token
  );
};

export const downloadBookingInvoice = async (
  request: DownloadBookingInvoiceRequest,
  token: string
) => {
  return await executeGraphQLMutation(
    downloadBookingInvoiceMutation(),
    {
      downloadBookingInvoiceRequest: request,
    },
    (result: any) => ({
      data: result.data?.downloadBookingInvoice,
      errors: result.errors,
    }),
    token
  );
};
