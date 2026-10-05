import '@testing-library/jest-dom';
import { render, fireEvent, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { downloadFromS3PreSignedUrl } from '@whitbread-eos/utils';

import EmergencyReport from './emergency-report';

const mockProps = {
  icons: {},
  token: 'mock-token',
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  downloadFromS3PreSignedUrl: jest.fn(),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getCountriesList: () => {
      return;
    },
    getCountryName: () => {
      return 'United Kingdom';
    },
    getEmergencyReport: jest.fn().mockResolvedValue({
      data: {
        emergencyReport: {
          downloadUrl: 'https://mock-url.com/report.pdf',
          fileName: 'emergency-report.pdf',
        },
      },
    }),
  };
});

describe('EmergencyReport Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render EmergencyReport component', async () => {
    const { getByTestId, getByText } = render(
      <EmergencyReport language={'en'} baseDataTestId={'EmergencyReportPage'} {...mockProps} />
    );

    expect(getByTestId('EmergencyReportPage-container')).toBeInTheDocument();
    expect(getByText('emergency.report.description')).toBeInTheDocument();
    expect(getByTestId('IB-Generate-Emergency-Report')).toBeInTheDocument();
  });

  it('should generate report if url is valid', async () => {
    const { getByTestId } = render(
      <EmergencyReport language={'en'} baseDataTestId={'EmergencyReportPage'} {...mockProps} />
    );
    fireEvent.click(getByTestId('IB-Generate-Emergency-Report'));
    await waitFor(() => {
      expect(downloadFromS3PreSignedUrl).toHaveBeenCalled();
    });
  });
});
