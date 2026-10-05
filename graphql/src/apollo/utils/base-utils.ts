import { readFileSync } from 'fs';
import { basename, join } from 'path';
import createLogger from '../log/logger';

const log = createLogger(basename(__filename));

export const readSchema = (directoryPath: string, filePath: string): string => {
  const fileLocation = join(directoryPath, filePath);
  try {
    return readFileSync(fileLocation, 'utf-8');
  } catch (error) {
    log.error(`Error reading schema file at ${filePath}:`, { error });
    throw error;
  }
};

export const getURL = (url: string, params: { [key: string]: string }): string => {
  const filteredParams: { [key: string]: string } = {};
  for (const key in params) {
    if (params.hasOwnProperty(key) && params[key] !== undefined) {
      filteredParams[key] = params[key];
    }
  }
  const requestParameters = Object.keys(filteredParams)
    .map((key) => `${key}=${filteredParams[key]}`)
    .join('&');
  return requestParameters.toString() ? `${url}?${requestParameters}` : url;
};

export const amendResponseHeaders = (context: any, responseHeaders: any): any => {
  log.debug(`Response Headers from Microservice: ${JSON.stringify(responseHeaders, null, 2)}`);
  if (responseHeaders) {
    Object.keys(responseHeaders).forEach((header) => {
      if (
        header.toLowerCase() == 'company-id' ||
        header.toLowerCase() == 'session-id' ||
        header.toLowerCase() == 'access-control-expose-headers' ||
        header.toLowerCase().startsWith('x-') ||
        header.toLowerCase().includes('trace') ||
        header.toLowerCase().includes('span')
      ) {
        context.res.setHeader(header, responseHeaders[header]);
      }
    });
  }
};

function addCustomHeaders(headers: any) {
  const customHeaders: { [key: string]: string } = {};

  Object.keys(headers).forEach((header) => {
    const headerLowerCase = header.toLowerCase();
    if (
      headerLowerCase.startsWith('x-') ||
      headerLowerCase.includes('trace') ||
      headerLowerCase.includes('span')
    ) {
      customHeaders[header] = headers[header];
    }
  });

  // Add custom headers to the outgoing request or response
  Object.entries(customHeaders).forEach(([key, value]) => {
    headers[key] = value;
  });
}

export const amendHeaders = (headers: any, flowCode: string): any => {
  log.info(`Headers from Apollo Router: ${JSON.stringify(headers, null, 2)}`);

  // Need to clean up headers as these headers are coming from GraphQL consumer and
  // now we want to make a REST call

  // Headers to remove
  const headersToRemove = [
    'Content-Length',
    'Host',
    'Transfer-Encoding',
    'Connection',
    'Keep-Alive',
    'Upgrade',
    'Origin',
    'Referer',
    'Content-Type',
    'Cookie',
    'Accept-Encoding'
  ];
  headersToRemove.forEach((header) => {
    delete headers[header.toLowerCase()];
  });

  headers['content-type'] = 'application/json';
  headers['accept'] = 'application/json';
  headers['accept-encoding'] = 'gzip, deflate, br';
  headers['user-agent'] = 'axios/1.7.7';
  headers['WB-FLOW-CODE'] = flowCode;
  if (headers?.['authorization']) {
    headers['WB-Authorization'] = headers?.['authorization'];
    headers['Authorization'] = headers?.['authorization'];
    headers['Authentication'] = headers?.['authorization'];
  }
  if (headers?.['wb-session-id']) {
    headers['WB-Session-Id'] = headers?.['wb-session-id'];
  }
  addCustomHeaders(headers);

  if (process.env.LOCAL_ENVIRONMENT === 'true') {
    headers.host = process.env.TABLE_RESERVATION_SERVICE;
  }

  return headers;
};

export const addField = (field: any, fieldName: string, finalMap: { [key: string]: any }) => {
  finalMap[fieldName] = field;
};

export const addFieldIfNotUndefined = (
  field: any,
  fieldName: string,
  finalMap: { [key: string]: any }
) => {
  if (field !== undefined && field !== null && field !== '') {
    finalMap[fieldName] = field;
  }
};

export const addFieldIfNotUndefinedAndRequired = (
  field: any,
  fieldName: string,
  finalMap: { [key: string]: any }
) => {
  if (field !== undefined && field !== null) {
    finalMap[fieldName] = field;
  } else {
    throw new Error(`Field ${fieldName} is required`);
  }
};

export const joinCriteria = (
  criteria: any[],
  field: string,
  delimiter: string
): string | undefined => {
  if (!criteria) return undefined;
  return criteria
    .map((item) => (field ? item[field] : item))
    .filter((value) => value !== undefined && value !== null && value !== '')
    .join(delimiter);
};

export const objectIsNullEmptyOrUndefined = (obj: any): boolean => {
  return obj == null || obj === '' || (typeof obj === 'object' && Object.keys(obj).length === 0);
};

export const addFieldsToMap = (fields: any[], map: { [key: string]: any }) => {
  fields.forEach(({ key, value, required }) => {
    if (required) {
      addFieldIfNotUndefinedAndRequired(value, key, map);
    } else {
      addFieldIfNotUndefined(value, key, map);
    }
  });
};

export const validateInteger = (value: string): boolean => {
  return /^\d+$/.test(value);
};

export const maskSensitiveFields = (
  obj: { [key: string]: any },
  fieldsToMask: string[]
): { [key: string]: any } => {
  const sanitizedObj = { ...obj };

  fieldsToMask.forEach((field) => {
    if (sanitizedObj[field]) {
      sanitizedObj[field] = '[REDACTED]';
    }
  });

  return sanitizedObj;
};

export const validateDateRange = (dateRangeStart: string, dateRangeEnd: string) => {
  if (objectIsNullEmptyOrUndefined(dateRangeStart)) {
    throw new Error('Date range start is required and cannot be empty');
  }
  if (objectIsNullEmptyOrUndefined(dateRangeEnd)) {
    throw new Error('Date range end is required and cannot be empty');
  }

  const startDate = Date.parse(dateRangeStart);
  const endDate = Date.parse(dateRangeEnd);

  if (isNaN(startDate) || isNaN(endDate)) {
    throw new Error('Invalid date format');
  }
  if (startDate > endDate) {
    throw new Error('Date range start cannot be greater than date range end');
  }
};

// @Deprecated please use replaceServiceEndpoint
export const getServiceEndpoint = (endPoint: string, serviceEndPoint: any) => {
  return {
    endpoint: endPoint,
    flowCode: serviceEndPoint.flowCode,
    axiosClient: serviceEndPoint.axiosClient
  };
};

export const validateDateFormat = (dateString: string | undefined, fieldName: string): void => {
  if (!objectIsNullEmptyOrUndefined(dateString)) {
    const date = Date.parse(dateString ?? '');
    if (isNaN(date)) {
      throw new Error(`Invalid ${fieldName} format`);
    }
  }
};

export const replaceServiceEndpoint = (
  serviceEndPoint: any,
  toReplace: string,
  replaceBy: string
): any => {
  const endPoint = serviceEndPoint.endpoint.replace(toReplace, replaceBy);

  return {
    ...serviceEndPoint,
    endpoint: endPoint
  };
};

export const transformToJsonResponse = (response: any) => {
  for (const key in response) {
    if (response[key] !== undefined && response[key] !== null) {
      response[key] = JSON.stringify(response[key]);
    }
  }
  return response;
};

export const joinCriteriaNestedField = (
  criteria: any[],
  field: string,
  nestedField: string,
  delimiter: string
): string | undefined => {
  if (!criteria) return undefined;
  return criteria
    .map((item) => (field ? item[field][nestedField] : item))
    .filter((value) => value !== undefined && value !== null && value !== '')
    .join(delimiter);
};

export const filterAndJoinStrings = (inputList: string[]): string => {
  return inputList
    .filter((input) => input !== '' && input !== null && input !== undefined)
    .join(',');
};

export const isFieldRequested = (fieldName: string, info: any): boolean => {
  // Traverse the selection set to see if the requested field exists
  const requestedFields: string[] = info.fieldNodes[0].selectionSet.selections.map(
    (selection: any) => selection.name.value
  );

  return requestedFields?.includes(fieldName) || false;
};

export const logPipelineError = (error: any, args: any, caller: any) => {
  log.info(`Error found in pipeline resolver named: ${caller.name}`);
  if (error.response) {
    const statusCode = error.response.data?.status || error.status;
    log.error(
      `Error occurred:\n` +
        `  Error object: ${error}\n` +
        `  Error arguments: ${args ? JSON.stringify(args) : 'No Arguments'}\n` +
        `  Error Status code: ${statusCode}\n` +
        `  Error Response data: ${JSON.stringify(error.response?.data)}`
    );
  } else {
    log.error(
      `Error occurred:\n` +
        `  Error object: ${error ? JSON.stringify(error) : 'No Error Object'}\n` +
        `  Error arguments: ${args ? JSON.stringify(args) : 'No Arguments'}`
    );
  }
};

export const addDefaultValuesFieldsToMap = (fields: any[], map: { [key: string]: any }) => {
  fields.forEach(({ key, value }) => {
    addFieldIfNotUndefinedButEmpty(value, key, map);
  });
};

export const addFieldIfNotUndefinedButEmpty = (
  field: any,
  fieldName: string,
  finalMap: { [key: string]: any }
) => {
  if (field !== undefined && field !== null) {
    finalMap[fieldName] = field;
  }
};

export const toIsoDate = (date: string): string => {
  if (!objectIsNullEmptyOrUndefined(date)) {
    return date.split('/').reverse().join('/');
  } else {
    log.error(
      `Error converting date to ISO format string: date is null, empty or undefined. date: ${date}`
    );
    throw new Error('Date cannot be null, empty or undefined');
  }
};

export const getTodayIsoDate = (): string => {
  return new Date().toISOString().split('T')[0];
};
