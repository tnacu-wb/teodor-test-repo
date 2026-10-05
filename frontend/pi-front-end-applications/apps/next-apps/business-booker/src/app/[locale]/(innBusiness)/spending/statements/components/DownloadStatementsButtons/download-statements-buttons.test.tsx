import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';
import { LOCALES, CustomerAccountDetails } from '@whitbread-eos/api';
import { act } from 'react-dom/test-utils';

import { DownloadStatementsButtons } from './download-statements-buttons';

const mockAccount = {
  tetheredGuid: '123',
  schemeCustomerId: 123,
  scheme: 'GB',
} as CustomerAccountDetails;

const mockProps = {
  icons: { icon: 'test' },
  token: 'test-token',
  account: mockAccount,
  fileAutoID: 123,
  statementDate: '20231001',
  invoiceNo: '123',
};

const mockGetStatementsPdf = jest.fn((...args: any[]) => Promise.resolve(args));
const mockGetStatementsXls = jest.fn((...args: any[]) => Promise.resolve(args));

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    getCountryLanguageByLocale: jest.fn(() => ({ language: 'en' })),
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getCommonIcons: () => ({}),
    formatIBAssetsUrl: (url: string) => url,
    getStatementsPdf: (...args: any[]) => mockGetStatementsPdf(...args),
    getStatementsXls: (...args: any[]) => mockGetStatementsXls(...args),
  };
});

describe('DownloadStatementsButtons Component', () => {
  window._satellite = { track: jest.fn() };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render DownloadStatementsButtons component', async () => {
    const { getByTestId } = render(<DownloadStatementsButtons {...mockProps} />);

    expect(getByTestId('Download-Statements-Buttons-Container')).toBeInTheDocument();
  });

  it('should click on buttons', async () => {
    const { getByTestId } = render(<DownloadStatementsButtons {...mockProps} />);

    const pdfButton = getByTestId('Download-Statements-Pdf-Button');
    const excelButton = getByTestId('Download-Statements-Excel-Button');

    await act(async () => {
      pdfButton.click();
      excelButton.click();
    });

    await waitFor(() => {
      expect(getByTestId('Download-Statements-Buttons-Container')).toBeInTheDocument();
    });
  });

  it('should call getStatementsPdf and getStatementsXls when buttons are clicked', async () => {
    const { getByTestId } = render(<DownloadStatementsButtons {...mockProps} />);

    const pdfButton = getByTestId('Download-Statements-Pdf-Button');
    const excelButton = getByTestId('Download-Statements-Excel-Button');

    await act(async () => {
      pdfButton.click();
      excelButton.click();
    });

    await waitFor(() => {
      expect(mockGetStatementsPdf).toHaveBeenCalledWith(
        mockProps.token,
        mockAccount.tetheredGuid,
        mockAccount.schemeCustomerId,
        mockProps.fileAutoID,
        mockProps.statementDate,
        mockProps.invoiceNo,
        mockAccount.scheme
      );
      expect(mockGetStatementsXls).toHaveBeenCalledWith(
        mockProps.token,
        mockAccount.tetheredGuid,
        mockAccount.schemeCustomerId,
        mockProps.fileAutoID,
        mockProps.statementDate,
        mockProps.invoiceNo,
        mockAccount.scheme
      );
    });
  });

  it('should call satellite track functions when buttons are clicked', async () => {
    const { getByTestId } = render(<DownloadStatementsButtons {...mockProps} />);

    const pdfButton = getByTestId('Download-Statements-Pdf-Button');
    const excelButton = getByTestId('Download-Statements-Excel-Button');

    expect(window._satellite.track).not.toHaveBeenCalled();

    await act(async () => {
      pdfButton.click();
      excelButton.click();
    });

    await waitFor(() => {
      expect(window._satellite.track).toHaveBeenCalledWith('downloadInvoice');
      expect(window._satellite.track).toHaveBeenCalledWith('reportDownloaded');
    });
  });
});
