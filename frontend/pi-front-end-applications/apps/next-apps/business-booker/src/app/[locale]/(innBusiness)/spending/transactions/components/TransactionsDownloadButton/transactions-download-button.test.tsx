import '@testing-library/jest-dom';
import { act, fireEvent, render } from '@testing-library/react';
import { getTransactionsXls } from '@whitbread-eos/utils/server';

import { TransactionsDownloadButton } from './transactions-download-button';

const mockGetTransactionsXls = jest.fn();

jest.mock('@whitbread-eos/utils/server', () => ({
  getTransactionsXls: jest.fn(),
  formatIBAssetsUrl: jest.fn((url: string) => url),
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        'spending.download.fileXls.text': 'Download transactions',
      };

      return translations[key] || key;
    },
  }),
  cn: (...classes: Array<string | false | null | undefined>) => classes.filter(Boolean).join(' '),
}));

describe('TransactionsDownloadButton', () => {
  const icons = { 'icon.download-icon': '/download.svg' };

  const mockedGetTransactionsXls = getTransactionsXls as jest.Mock;

  beforeEach(() => {
    jest.clearAllMocks();
    (window as any)._satellite = {
      track: jest.fn(),
    };

    mockedGetTransactionsXls.mockImplementation(mockGetTransactionsXls);
  });

  afterEach(() => {
    delete (window as any)._satellite;
  });

  it('renders null when required identifiers missing', () => {
    const { container } = render(
      <TransactionsDownloadButton icons={icons} token="token" scheme="GB" />
    );

    expect(container).toBeEmptyDOMElement();
  });

  it('calls getTransactionsXls on click with default scheme', async () => {
    mockGetTransactionsXls.mockResolvedValueOnce(undefined);

    const { getByTestId } = render(
      <TransactionsDownloadButton
        icons={icons}
        token="token"
        schemeCustomerId={123}
        tetheredUserGuid="guid"
        testId="TransactionsDownload"
      />
    );

    const downloadButton = getByTestId('TransactionsDownload-DownloadButton');

    await act(async () => {
      fireEvent.click(downloadButton);
    });

    expect(mockGetTransactionsXls).toHaveBeenCalledWith('token', 123, 'guid', 'GB');
    expect((window as any)._satellite.track).toHaveBeenCalledWith('downloadTransactions');
    expect((window as any)._satellite.track).toHaveBeenCalledWith('reportDownloaded');
  });

  it('uses provided scheme value', async () => {
    mockGetTransactionsXls.mockResolvedValueOnce(undefined);

    const { getByTestId } = render(
      <TransactionsDownloadButton
        icons={icons}
        token="token"
        schemeCustomerId={987}
        tetheredUserGuid="guid"
        scheme="DE"
        testId="TransactionsDownload"
      />
    );

    const downloadButton = getByTestId('TransactionsDownload-DownloadButton');

    await act(async () => {
      fireEvent.click(downloadButton);
    });

    expect(mockGetTransactionsXls).toHaveBeenCalledWith('token', 987, 'guid', 'DE');
  });

  it('prevents concurrent downloads while request pending', async () => {
    let resolveDownload: () => void = () => undefined;
    mockGetTransactionsXls.mockImplementationOnce(
      () =>
        new Promise<void>((resolve) => {
          resolveDownload = resolve;
        })
    );

    const { getByTestId } = render(
      <TransactionsDownloadButton
        icons={icons}
        token="token"
        schemeCustomerId={111}
        tetheredUserGuid="guid"
        testId="TransactionsDownload"
      />
    );

    const downloadButton = getByTestId('TransactionsDownload-DownloadButton');

    fireEvent.click(downloadButton);
    fireEvent.click(downloadButton);

    expect(mockGetTransactionsXls).toHaveBeenCalledTimes(1);

    await act(async () => {
      resolveDownload();
    });
  });

  it('tracks error and allows retry when download fails', async () => {
    mockGetTransactionsXls.mockRejectedValueOnce(new Error('fail'));
    mockGetTransactionsXls.mockResolvedValueOnce(undefined);

    const { getByTestId } = render(
      <TransactionsDownloadButton
        icons={icons}
        token="token"
        schemeCustomerId={222}
        tetheredUserGuid="guid"
        testId="TransactionsDownload"
      />
    );

    const downloadButton = getByTestId('TransactionsDownload-DownloadButton');

    await act(async () => {
      fireEvent.click(downloadButton);
    });

    expect(mockGetTransactionsXls).toHaveBeenCalledTimes(1);
    expect((window as any)._satellite.track).toHaveBeenCalledWith('downloadTransactions');
    expect((window as any)._satellite.track).toHaveBeenCalledWith('reportDownloaded');
    expect((window as any)._satellite.track).toHaveBeenCalledWith('error');

    await act(async () => {
      fireEvent.click(downloadButton);
    });

    expect(mockGetTransactionsXls).toHaveBeenCalledTimes(2);
  });
});
