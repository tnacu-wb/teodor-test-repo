import { endpoints } from '../../../../../../src/apollo/subgraphs/company-reporting-service/services/base-service';
import { get } from '../../../../../../src/apollo/client/rest-client';
import { emergencyReport } from '../../../../../apollo/subgraphs/company-reporting-service/services/emergency_report_service';

jest.mock('../../../../../apollo/client/rest-client');

describe('emergencyReport Resolver', () => {
  const mockContext = {};
  const mockParams = { language: 'en' };
  const mockResponse = {
    reportName: 'Emergency Report',
    fileName: 'emergency_report.pdf',
    downloadUrl: 'https://example.com/download/emergency_report.pdf'
  };

  it('should return emergency report data when parameters are valid', async () => {
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const finalMap = { language: mockParams.language };

    const response = await emergencyReport(mockParams, mockContext);

    expect(get).toHaveBeenCalledWith(
      endpoints.EMERGENCY_REPORT,
      emergencyReport,
      finalMap,
      mockContext
    );
    expect(response).toEqual(mockResponse);
  });

  it('should handle null response when no data is returned', async () => {
    (get as jest.Mock).mockResolvedValueOnce(null);

    const response = await emergencyReport(mockParams, mockContext);

    expect(response).toEqual({
      reportName: undefined,
      fileName: undefined,
      downloadUrl: undefined
    });
  });

  it('should handle errors gracefully when fetching emergency report fails', async () => {
    const error = new Error('Failed to fetch report');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(emergencyReport(mockParams, mockContext)).rejects.toThrow(
      'Failed to fetch report'
    );
  });
});
