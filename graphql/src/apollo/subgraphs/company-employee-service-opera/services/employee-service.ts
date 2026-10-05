import {
  addFieldsToMap,
  getServiceEndpoint,
  getURL,
  objectIsNullEmptyOrUndefined
} from '../../../utils/base-utils';
import { get, post, put } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import createLogger from '../../../log/logger';
import { basename } from 'path';
import { endpoints } from './base-service';
import { EmployeesParams } from '../models/employees-params';
import { EmployeeDetailsParams } from '../models/employee-details-params';
import { FilteringOptionsEmployeesParam } from '../models/filtering-options-employees-param';

const log = createLogger(basename(__filename));
const LANGUAGE = 'language';

export const getEmployees = async (params: EmployeesParams, context: any): Promise<any> => {
  const { companyId, searchCriteria, bookingChannel, awaitingApproval, size, page, pageToken } =
    params;

  let finalMap: { [key: string]: any } = {};

  try {
    const getEmployeesPath = endpoints.GET_EMPLOYEES.endpoint.replace('{companyId}', companyId);

    const getEmployeesEndPoint = getServiceEndpoint(getEmployeesPath, endpoints.GET_EMPLOYEES);

    const fieldsToAdd = [
      { key: 'searchCriteria', value: searchCriteria, required: false },
      { key: 'bookingChannel', value: bookingChannel, required: false },
      { key: 'awaitingApproval', value: awaitingApproval, required: false },
      { key: 'size', value: size, required: true },
      { key: 'page', value: page, required: false },
      { key: 'pageToken', value: pageToken, required: false }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    log.info(`Final map to search for get employees: ${JSON.stringify(finalMap)}`);

    return await get(getEmployeesEndPoint, getEmployees, finalMap, context);
  } catch (error) {
    finalMap['companyId'] = companyId;
    handleError(error, finalMap);
  }
};

export const getEmployeeDetails = async (
  params: EmployeeDetailsParams,
  context: any
): Promise<any> => {
  const { companyId, employeeId, activationKey } = params;

  let finalMap: { [key: string]: any } = {};

  try {
    const getEmployeeDetailsPath = endpoints.GET_EMPLOYEE_DETAILS.endpoint
      .replace('{companyId}', companyId)
      .replace('{employeeId}', employeeId);

    const getEmployeeDetailsEndPoint = getServiceEndpoint(
      getEmployeeDetailsPath,
      endpoints.GET_EMPLOYEE_DETAILS
    );

    const fieldsToAdd = [{ key: 'activationKey', value: activationKey, required: false }];

    addFieldsToMap(fieldsToAdd, finalMap);

    log.info(`Final map to search for get employee details: ${JSON.stringify(finalMap)}`);

    return await get(getEmployeeDetailsEndPoint, getEmployeeDetails, finalMap, context);
  } catch (error) {
    handleError(error, { finalMap, companyId: companyId, employeeId: employeeId });
  }
};

export const sendActivationEmail = async (
  {
    companyId,
    languageCode,
    sendActivationEmailCriteria
  }: {
    companyId: string;
    languageCode?: string;
    sendActivationEmailCriteria: any;
  },
  context: any
): Promise<any> => {
  try {
    const sendActivationEmailPath = endpoints.SEND_ACTIVATION_EMAIL.endpoint.replace(
      '{companyId}',
      companyId
    );

    const sendActivationEmailEndPoint = getServiceEndpoint(
      sendActivationEmailPath,
      endpoints.SEND_ACTIVATION_EMAIL
    );

    if (!objectIsNullEmptyOrUndefined(languageCode)) {
      context.headers[LANGUAGE] = languageCode;
    }

    if (objectIsNullEmptyOrUndefined(sendActivationEmailCriteria.innBusiness)) {
      sendActivationEmailCriteria.innBusiness = true;
    }

    return await post(
      sendActivationEmailEndPoint,
      sendActivationEmail,
      sendActivationEmailCriteria,
      context
    );
  } catch (error) {
    handleError(error, {
      companyId: companyId,
      sendActivationEmailCriteria: sendActivationEmailCriteria
    });
  }
};

export const addEmployee = async (
  {
    companyId,
    languageCode,
    employee
  }: {
    companyId: string;
    languageCode?: string;
    employee?: any;
  },
  context: any
): Promise<any> => {
  try {
    const addEmployeePath = endpoints.ADD_EMPLOYEE.endpoint.replace('{companyId}', companyId);

    const addEmployeeEndPoint = getServiceEndpoint(addEmployeePath, endpoints.ADD_EMPLOYEE);

    if (!objectIsNullEmptyOrUndefined(languageCode)) {
      context.headers[LANGUAGE] = languageCode;
    }
    if (objectIsNullEmptyOrUndefined(employee.innBusiness)) {
      employee.innBusiness = true;
    }

    const response = await post(addEmployeeEndPoint, addEmployee, employee, context, true);

    return response?.headers?.location;
  } catch (error) {
    handleError(error, {
      companyId: companyId,
      employee: employee
    });
  }
};

export const updateEmployee = async (
  {
    companyId,
    employeeId,
    languageCode,
    activationKey,
    updateEmployeeCriteria
  }: {
    companyId: string;
    employeeId: string;
    languageCode?: string;
    activationKey?: string;
    updateEmployeeCriteria: any;
  },
  context: any
): Promise<any> => {
  try {
    let updateEmployeePath = endpoints.UPDATE_EMPLOYEE.endpoint
      .replace('{companyId}', companyId)
      .replace('{employeeId}', employeeId);

    if (activationKey) {
      let finalMap: { [key: string]: any } = {};
      const fieldsToAdd = [{ key: 'activation-key', value: activationKey, required: false }];
      addFieldsToMap(fieldsToAdd, finalMap);

      let updateEmployeeActivateEndPoint = getURL(endpoints.UPDATE_EMPLOYEE.endpoint, finalMap);

      updateEmployeePath = updateEmployeeActivateEndPoint
        .replace('{companyId}', companyId)
        .replace('{employeeId}', employeeId);
    }

    const updateEmployeeEndPoint = getServiceEndpoint(
      updateEmployeePath,
      endpoints.UPDATE_EMPLOYEE
    );

    if (!objectIsNullEmptyOrUndefined(languageCode)) {
      context.headers[LANGUAGE] = languageCode;
    }

    const response = await put(
      updateEmployeeEndPoint,
      updateEmployee,
      updateEmployeeCriteria,
      context
    );
    return response.data;
  } catch (error) {
    handleError(error, {
      companyId: companyId,
      employee: employeeId,
      languageCode: languageCode,
      activationKey: activationKey,
      updateEmployeeCriteria: updateEmployeeCriteria
    });
  }
};

export const getActivationDetails = async (
  { activationKey }: { activationKey: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};

  try {
    const fieldsToAdd = [{ key: 'activation-key', value: activationKey, required: true }];
    addFieldsToMap(fieldsToAdd, finalMap);
    log.info(`Final map to search for get activation details: ${JSON.stringify(finalMap)}`);

    return await get(endpoints.GET_ACTIVATION_DETAILS, getActivationDetails, finalMap, context);
  } catch (error) {
    handleError(error, { finalMap });
  }
};

export const getEmployeesWithFilteringOptions = async (
  params: FilteringOptionsEmployeesParam,
  context: any
): Promise<any> => {
  const {
    companyId,
    searchCriteria,
    bookingChannel,
    awaitingApproval,
    size,
    page,
    pageToken,
    shouldFilterEmployees
  } = params;

  let finalMap: { [key: string]: any } = {};

  try {
    const getEmployeesPath = endpoints.GET_EMPLOYEES.endpoint.replace('{companyId}', companyId);

    const getEmployeesEndPoint = getServiceEndpoint(getEmployeesPath, endpoints.GET_EMPLOYEES);

    const fieldsToAdd = [
      { key: 'searchCriteria', value: searchCriteria, required: false },
      { key: 'bookingChannel', value: bookingChannel, required: false },
      { key: 'awaitingApproval', value: awaitingApproval, required: false },
      { key: 'size', value: size, required: true },
      { key: 'page', value: page, required: false },
      { key: 'pageToken', value: pageToken, required: false },
      { key: 'shouldFilterEmployees', value: shouldFilterEmployees, required: false }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    log.info(`Final map for getEmployeesWithFilteringOptions : ${JSON.stringify(finalMap)}`);

    return await get(getEmployeesEndPoint, getEmployeesWithFilteringOptions, finalMap, context);
  } catch (error) {
    finalMap['companyId'] = companyId;
    handleError(error, finalMap);
  }
};
