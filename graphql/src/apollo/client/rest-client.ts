import createLogger from '../log/logger';
import { basename } from 'path';
import { amendHeaders, amendResponseHeaders, getURL } from '../utils/base-utils';

const log = createLogger(basename(__filename));

// Overload signatures
export function get(serviceEndPoint: any, caller: any): Promise<any>;
export function get(serviceEndPoint: any, caller: any, context: any): Promise<any>;
export function get(serviceEndPoint: any, caller: any, params: any, context: any): Promise<any>;

// Implementation
export async function get(
  serviceEndPoint: any,
  caller: any,
  params: { [key: string]: string } = {},
  context: any = {}
): Promise<any> {
  try {
    let headers = context.headers;
    amendHeaders(headers, serviceEndPoint.flowCode);

    const endPointWithParams = getURL(serviceEndPoint.endpoint, params);
    log.info(
      `Calling the URL for the ${caller.name}: ${serviceEndPoint.axiosClient.defaults.baseURL}${endPointWithParams}`
    );
    const response = await serviceEndPoint.axiosClient.get(endPointWithParams, { headers });

    log.info(`Successful Response for the ${caller.name}`);
    amendResponseHeaders(context, response.headers);
    return response.data;
  } catch (error) {
    log.error(`Failed to call ${caller.name}`);
    throw error;
  }
}

// Overload signatures for post function
export function post(serviceEndPoint: any, caller: any, requestBody: any): Promise<any>;
export function post(
  serviceEndPoint: any,
  caller: any,
  requestBody: any,
  context: any
): Promise<any>;
export function post(
  serviceEndPoint: any,
  caller: any,
  requestBody: any,
  context: any,
  returnHeaders: boolean
): Promise<any>;

// Implementation for post function
export async function post(
  serviceEndPoint: any,
  caller: any,
  requestBody: any = {},
  context: any = {},
  returnHeaders?: boolean
): Promise<any> {
  try {
    let headers = context.headers;
    amendHeaders(headers, serviceEndPoint.flowCode);

    log.info(
      `Calling the URL for the ${caller.name}: ${serviceEndPoint.axiosClient.defaults.baseURL}`
    );
    const response = await serviceEndPoint.axiosClient.post(serviceEndPoint.endpoint, requestBody, {
      headers
    });
    log.info(`Successful Response for the ${caller.name}`);
    amendResponseHeaders(context, response.headers);
    if (returnHeaders) {
      return {
        data: response.data,
        headers: response.headers
      };
    }
    return response.data;
  } catch (error) {
    log.error(`Failed to call ${caller.name}`);
    throw error;
  }
}

// Overload signatures for post function
export function put(serviceEndPoint: any, caller: any, requestBody: any): Promise<any>;
export function put(
  serviceEndPoint: any,
  caller: any,
  requestBody: any,
  context: any
): Promise<any>;

// Implementation for put function
export async function put(
  serviceEndPoint: any,
  caller: any,
  requestBody: any = {},
  context: any = {}
): Promise<any> {
  try {
    let headers = context.headers;
    amendHeaders(headers, serviceEndPoint.flowCode);

    log.info(
      `Calling the URL for the ${caller.name}: ${serviceEndPoint.axiosClient.defaults.baseURL}`
    );
    const response = await serviceEndPoint.axiosClient.put(serviceEndPoint.endpoint, requestBody, {
      headers
    });
    log.info(`Successful Response for the ${caller.name}`);
    amendResponseHeaders(context, response.headers);
    return response;
  } catch (error) {
    log.error(`Failed to call ${caller.name}`);
    throw error;
  }
}

export function patch(serviceEndPoint: any, caller: any): Promise<any>;
export function patch(
  serviceEndPoint: any,
  caller: any,
  requestBody: any,
  context: any
): Promise<any>;

export async function patch(
  serviceEndPoint: any,
  caller: any,
  requestBody: any = {},
  context: any = {}
): Promise<any> {
  try {
    let headers = context.headers;
    amendHeaders(headers, serviceEndPoint.flowCode);

    log.info(
      `Calling the URL for the ${caller.name}: ${serviceEndPoint.axiosClient.defaults.baseURL}`
    );

    const response = await serviceEndPoint.axiosClient.patch(
      serviceEndPoint.endpoint,
      requestBody,
      { headers }
    );

    log.info(`Successful Response for the ${caller.name}`);
    amendResponseHeaders(context, response.headers);
    return response;
  } catch (error) {
    log.error(`Failed to call ${caller.name}`);
    throw error;
  }
}
