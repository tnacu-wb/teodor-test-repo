import { GraphQLFormattedError } from 'graphql';
import createLogger from '../log/logger';
import { basename } from 'path';

const log = createLogger(basename(__filename));

class CustomError extends Error {
  constructor(
    message: string,
    public statusCode: number
  ) {
    super(message);
    this.name = 'CustomError';
  }
}

export const handleError = (error: any, args: any): never => {
  const REQUEST_NOT_FOUND = 404;
  const REQUEST_TIMEOUT = 504;

  if (error.response) {
    const statusCode = error.response.data?.status || error.status;
    log.error(
      `Error occurred:\n` +
        `  Error object: ${error}\n` +
        `  Error arguments: ${args ? JSON.stringify(args) : 'No Arguments'}\n` +
        `  Error Status code: ${statusCode}\n` +
        `  Error Response data: ${JSON.stringify(error.response?.data)}`
    );
    const jsonError = error.response.data
      ? JSON.parse(JSON.stringify(error.response.data))
      : { message: 'Unknown Error' };
    jsonError.errorType = statusCode;
    throw new CustomError(JSON.stringify(jsonError), statusCode);
  } else {
    //If the downstream call fails with 404, the error object will not have a response object
    log.error(
      `Error occurred:\n` +
        `  Error object: ${error ? JSON.stringify(error) : 'No Error Object'}\n` +
        `  Error arguments: ${args ? JSON.stringify(args) : 'No Arguments'}`
    );
    const errorMsg =
      error.code === 'ECONNABORTED'
        ? 'Endpoint request timed out'
        : error.message || 'Internal Server Error';
    const statusCode = error.code === 'ECONNABORTED' ? REQUEST_TIMEOUT : REQUEST_NOT_FOUND;
    const jsonError = {
      message: errorMsg,
      errors: [{ field: 'Error', message: errorMsg }],
      errorType: statusCode
    };
    throw new CustomError(JSON.stringify(jsonError), statusCode);
  }
};

export const formatError = (err: GraphQLFormattedError & { originalError?: any }): any => {
  let errorResponse;
  let errorType;
  try {
    const parsedMessage = JSON.parse(err.message);
    errorResponse = JSON.stringify(parsedMessage);
    errorType = parsedMessage.errorType;
  } catch {
    errorResponse = err.message;
  }
  return {
    message: errorResponse,
    locations: err.locations,
    path: err.path,
    extensions: {
      errorType: errorType || '500'
    }
  };
};
