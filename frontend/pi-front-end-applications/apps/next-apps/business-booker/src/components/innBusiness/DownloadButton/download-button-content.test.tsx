import '@testing-library/jest-dom';
import { act, fireEvent, render } from '@testing-library/react';

import { DownloadButtonContent } from './download-button-content';

const mockProps = {
  token: 'abc',
  companyId: '123',
  testId: '',
  className: 'test',
  icons: {},
  altText: 'Download button alt text',
  buttonText: 'Download',
};

let mockCardManagementLabels: any = null;

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCommonIcons: () => ({}),
    getCardManagementLabels: () => mockCardManagementLabels,
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getEmployeesCSV: jest.fn(),
  };
});

describe('DownloadButtonContent Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render DownloadButtonContent component', async () => {
    const { getByTestId } = render(<DownloadButtonContent {...mockProps} />);

    expect(getByTestId('DownloadButton')).toBeInTheDocument();
  });

  it('should render with images', async () => {
    mockCardManagementLabels = { download: { icon: '/' } };
    const { getByTestId } = render(<DownloadButtonContent {...mockProps} />);

    expect(getByTestId('DownloadButton')).toBeInTheDocument();
  });

  it('should click DownloadButtonContent', async () => {
    const { getByTestId } = render(<DownloadButtonContent {...mockProps} />);

    const downloadButton = getByTestId('DownloadButton');

    act(() => {
      fireEvent.click(downloadButton);
    });
  });

  it('should render DownloadButtonContent component with testId', async () => {
    mockProps.testId = 'test';
    const { getByTestId } = render(<DownloadButtonContent {...mockProps} />);

    expect(getByTestId('test-DownloadButton')).toBeInTheDocument();
  });
});
