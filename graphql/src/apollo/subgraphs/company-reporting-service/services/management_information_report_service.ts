import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import createLogger from '../../../log/logger';
import { basename } from 'path';
import {
  addFieldIfNotUndefined,
  addFieldIfNotUndefinedAndRequired,
  addFieldsToMap
} from '../../../utils/base-utils';
import { ManagementInformationReportCriteria } from '../models/ManagementInformationReportCriteria';

const log = createLogger(basename(__filename));

export const managementInformationReport = async (
  managementInformationReportCriteria: ManagementInformationReportCriteria,
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};

    const fieldsToAdd = [
      { key: 'fromDate', value: managementInformationReportCriteria.fromDate, required: true },
      { key: 'toDate', value: managementInformationReportCriteria.toDate, required: true },
      {
        key: 'showQnAcolumns',
        value: managementInformationReportCriteria.showQnAcolumns,
        required: true
      },
      { key: 'language', value: managementInformationReportCriteria.language, required: true }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    log.info(
      `Final query parameters for management information report: ${JSON.stringify(finalMap)}`
    );

    const response = await get(
      endpoints.MANAGEMENT_INFORMATION_REPORT,
      managementInformationReport,
      finalMap,
      context
    );

    return {
      reportName: response?.reportName,
      fileName: response?.fileName,
      downloadUrl: response?.downloadUrl
    };
  } catch (error) {
    handleError(error, managementInformationReportCriteria);
  }
};
