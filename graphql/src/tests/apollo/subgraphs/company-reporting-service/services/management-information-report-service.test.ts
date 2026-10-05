import { endpoints } from '../../../../../../src/apollo/subgraphs/company-reporting-service/services/base-service';
import { get } from '../../../../../../src/apollo/client/rest-client';

import { managementInformationReport } from '../../../../../apollo/subgraphs/company-reporting-service/services/management_information_report_service';

jest.mock('../../../../../apollo/client/rest-client');

describe('managementInformationReport Resolver', () => {
  const headers = { 'Content-Type': 'application/json' };
  const mockParams = {
    fromDate: '2023-01-01',
    toDate: '2023-12-31',
    showQnAcolumns: true,
    language: 'en'
  };
  const mockResponse = {
    reportName: 'Annual Report',
    fileName: 'report_2023.pdf',
    downloadUrl: 'https://example.com/download/report_2023.pdf'
  };

  it('should return management information report data when parameters are valid', async () => {
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const finalMap = {
      fromDate: mockParams.fromDate,
      toDate: mockParams.toDate,
      showQnAcolumns: mockParams.showQnAcolumns,
      language: mockParams.language
    };

    const response = await managementInformationReport(mockParams, headers);

    expect(get).toHaveBeenCalledWith(
      endpoints.MANAGEMENT_INFORMATION_REPORT,
      managementInformationReport,
      finalMap,
      headers
    );
    expect(response).toEqual(mockResponse);
  });

  it('should handle null response when no data is returned', async () => {
    (get as jest.Mock).mockResolvedValueOnce(null);

    const response = await managementInformationReport(mockParams, headers);

    expect(response).toEqual({
      reportName: undefined,
      fileName: undefined,
      downloadUrl: undefined
    });
  });

  it('should handle errors gracefully when fetching management information report fails', async () => {
    const error = new Error('Failed to fetch report');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(managementInformationReport(mockParams, headers)).rejects.toThrow(
      'Failed to fetch report'
    );
  });
});
