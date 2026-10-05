import { get, post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import {
  addFieldsToMap,
  objectIsNullEmptyOrUndefined,
  transformToJsonResponse
} from '../../../utils/base-utils';

export const getPayApplications = async (args: any, context: any): Promise<any> => {
  try {
    return await get(endpoints.PAY_APP, getPayApplications, {}, context);
  } catch (error) {
    handleError(error, {});
  }
};

export const initializeApplication = async (
  { initializeApplicationRequest }: { initializeApplicationRequest: any },
  context: any
): Promise<any> => {
  try {
    if (!objectIsNullEmptyOrUndefined(context.headers?.['X-Forwarded-For'])) {
      context.headers['X-Forwarded-For'] = context.headers?.['X-Forwarded-For'];
    }

    return await post(
      endpoints.INITIALIZE_APPLICATION,
      initializeApplication,
      initializeApplicationRequest,
      context
    );
  } catch (error) {
    handleError(error, initializeApplicationRequest);
  }
};

export const deleteApplication = async (
  { deleteApplicationRequest }: { deleteApplicationRequest: any },
  context: any
): Promise<any> => {
  try {
    if (!objectIsNullEmptyOrUndefined(context.headers?.['X-Forwarded-For'])) {
      context.headers['X-Forwarded-For'] = context.headers?.['X-Forwarded-For'];
    }

    return await post(
      endpoints.DELETE_APPLICATION,
      deleteApplication,
      deleteApplicationRequest,
      context
    );
  } catch (error) {
    handleError(error, deleteApplicationRequest);
  }
};

export const getAppLookupData = async (
  { scheme }: { scheme: string },
  context: any,
  info: any
): Promise<any> => {
  try {
    const lookupNameMap: { [key: string]: string } = {
      title: 'TITLE',
      tradingStyle: 'TRADING_STYLE',
      estimatedMonthlySpend: 'ESTIMATED_MONTHLY_SPEND',
      hotelBrandPolicies: 'HOTEL_BRAND_POLICIES',
      hotelBookingRoles: 'HOTEL_BOOKING_ROLES',
      isoCountryCodes: 'ISO_COUNTRY_CODES',
      timeTrading: 'TIME_TRADING',
      industrySector: 'INDUSTRY_SECTOR',
      registrationQuestions: 'REGISTRATION_QUESTIONS',
      numberOfEmployees: 'NUMBER_OF_EMPLOYEES',
      cancellationReason: 'CANCELLATION_REASON'
    };

    const selectionSetList: string[] = (info.fieldNodes?.[0]?.selectionSet?.selections ?? []).map(
      (selection: any) => selection.name.value
    );

    const names = selectionSetList
      .map((field) => lookupNameMap[field])
      .filter((field) => field)
      .join(', ');

    const params = {
      lookupNames: names,
      scheme: scheme
    };

    const response = await get(endpoints.APP_LOOKUP_DATA, getAppLookupData, params, context);

    return transformToJsonResponse(response);
  } catch (error) {
    handleError(error, scheme);
  }
};

export const getCompanyDetailsLookup = async (
  { companyRegistrationNumber, scheme }: { companyRegistrationNumber: string; scheme?: string },
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};
    if (!objectIsNullEmptyOrUndefined(context.headers?.['X-Forwarded-For'])) {
      context.headers['X-Forwarded-For'] = context.headers?.['X-Forwarded-For'];
    }

    const fieldsToAdd = [
      { key: 'companyRegistrationNumber', value: companyRegistrationNumber, required: true },
      { key: 'scheme', value: scheme, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.COMPANY_DETAILS_LOOKUP, getCompanyDetailsLookup, finalMap, context);
  } catch (error) {
    handleError(error, companyRegistrationNumber);
  }
};

export const getApplicationDetails = async (
  {
    applicationId,
    applicationGuid,
    scheme
  }: { applicationId: string; applicationGuid: string; scheme: string },
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};

    if (!objectIsNullEmptyOrUndefined(context.headers?.['X-Forwarded-For'])) {
      context.headers['X-Forwarded-For'] = context.headers?.['X-Forwarded-For'];
    }
    const fieldsToAdd = [
      { key: 'applicationId', value: applicationId, required: true },
      { key: 'applicationGuid', value: applicationGuid, required: true },
      { key: 'scheme', value: scheme, required: true }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.APPLICATION_DETAILS, getApplicationDetails, finalMap, context);
  } catch (error) {
    handleError(error, {
      applicationId: applicationId,
      applicationGuid: applicationGuid,
      scheme: scheme
    });
  }
};

export const shareApplication = async (
  { shareApplicationRequest }: { shareApplicationRequest: any },
  context: any
): Promise<any> => {
  try {
    return await post(
      endpoints.SHARE_APPLICATION,
      shareApplication,
      shareApplicationRequest,
      context
    );
  } catch (error) {
    handleError(error, shareApplicationRequest);
  }
};

export const deleteApplicationCard = async (
  { deleteApplicationCardRequest }: { deleteApplicationCardRequest: any },
  context: any
): Promise<any> => {
  try {
    return await post(
      endpoints.DELETE_APPLICATION_CARD,
      deleteApplicationCard,
      deleteApplicationCardRequest,
      context
    );
  } catch (error) {
    handleError(error, deleteApplicationCardRequest);
  }
};

export const getApplicationCards = async (
  {
    applicationGuid,
    scheme,
    page,
    maxDisplayRows
  }: { applicationGuid: string; scheme: string; page: number; maxDisplayRows: number },
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};

    if (!objectIsNullEmptyOrUndefined(context.headers?.['X-Forwarded-For'])) {
      context.headers['X-Forwarded-For'] = context.headers?.['X-Forwarded-For'];
    }
    const fieldsToAdd = [
      { key: 'applicationGuid', value: applicationGuid, required: true },
      { key: 'scheme', value: scheme, required: true },
      { key: 'page', value: page, required: true },
      { key: 'maxDisplayRows', value: maxDisplayRows, required: true }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.GET_APPLICATION_CARDS, getApplicationCards, finalMap, context);
  } catch (error) {
    handleError(error, {
      applicationGuid: applicationGuid,
      scheme: scheme,
      page: page,
      maxDisplayRows: maxDisplayRows
    });
  }
};

export const removeParticipant = async (
  { removeParticipantRequest }: { removeParticipantRequest: any },
  context: any
): Promise<any> => {
  try {
    return await post(
      endpoints.REMOVE_PARTICIPANT,
      removeParticipant,
      removeParticipantRequest,
      context
    );
  } catch (error) {
    handleError(error, removeParticipantRequest);
  }
};

export const submitApplication = async (
  { submitApplicationRequest }: { submitApplicationRequest: any },
  context: any
): Promise<any> => {
  try {
    if (!objectIsNullEmptyOrUndefined(context.headers?.['X-Forwarded-For'])) {
      context.headers['X-Forwarded-For'] = context.headers?.['X-Forwarded-For'];
    }

    return await post(
      endpoints.SUBMIT_APPLICATION,
      submitApplication,
      submitApplicationRequest,
      context
    );
  } catch (error) {
    handleError(error, submitApplicationRequest);
  }
};

export const appPreCheck = async ({ scheme }: { scheme: string }, context: any): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};

    const fieldsToAdd = [{ key: 'scheme', value: scheme, required: true }];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.APP_PRE_CHECK, appPreCheck, finalMap, context);
  } catch (error) {
    handleError(error, {
      scheme: scheme
    });
  }
};

export const directDebit = async (
  { directDebitRequest }: { directDebitRequest: any },
  context: any
): Promise<any> => {
  try {
    if (!objectIsNullEmptyOrUndefined(context.headers?.['X-Forwarded-For'])) {
      context.headers['X-Forwarded-For'] = context.headers?.['X-Forwarded-For'];
    }

    return await post(endpoints.DIRECT_DEBIT, directDebit, directDebitRequest, context);
  } catch (error) {
    handleError(error, directDebitRequest);
  }
};

export const updateResumeUrl = async (
  { updateResumeUrlRequest }: { updateResumeUrlRequest: any },
  context: any
): Promise<any> => {
  try {
    return await post(
      endpoints.UPDATE_RESUME_URL,
      updateResumeUrl,
      updateResumeUrlRequest,
      context
    );
  } catch (error) {
    handleError(error, updateResumeUrlRequest);
  }
};

export const getDdSepaFormStatus = async (
  { hostedPageGuid, scheme }: { hostedPageGuid: string; scheme: string },
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};
    if (!objectIsNullEmptyOrUndefined(context.headers?.['X-Forwarded-For'])) {
      context.headers['X-Forwarded-For'] = context.headers?.['X-Forwarded-For'];
    }

    const fieldsToAdd = [
      { key: 'hostedPageGuid', value: hostedPageGuid, required: true },
      { key: 'scheme', value: scheme, required: true }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.GET_DD_SEPA_FORM_STATUS, getDdSepaFormStatus, finalMap, context);
  } catch (error) {
    handleError(error, hostedPageGuid);
  }
};
