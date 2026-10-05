import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import createLogger from '../../../log/logger';
import { basename } from 'path';
import { addFieldIfNotUndefinedAndRequired } from '../../../utils/base-utils';

const log = createLogger(basename(__filename));

export const emergencyReport = async (request: any, context: any): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};

    addFieldIfNotUndefinedAndRequired(request.language, 'language', finalMap);

    log.info(`Final query parameters for emergency report: ${JSON.stringify(finalMap)}`);

    const response = await get(endpoints.EMERGENCY_REPORT, emergencyReport, finalMap, context);

    return {
      reportName: response?.reportName,
      fileName: response?.fileName,
      downloadUrl: response?.downloadUrl
    };
  } catch (error) {
    handleError(error, request);
  }
};
