import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import React from 'react';

import { DownloadButton } from './download-button';

const mockProps = {
  locale: LOCALES.EN,
  className: 'test',
  mobile: false,
  testId: '',
  token: '123',
  companyId: '1',
  icons: {
    download: 'download-icon',
    arrow: 'arrow-icon',
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => {
    return {
      t: (str: string) => str,
    };
  },
  cn: jest.fn((...classes: string[]) => classes.filter(Boolean).join(' ')),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCommonIcons: () => ({}),
    getEmployeesCSV: jest.fn(),
    TranslationProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>,
  };
});

jest.mock('./download-button-content', () => {
  return {
    DownloadButtonContent: ({ children }: { children: React.ReactNode }) => (
      <div data-testid="DownloadButtonContent">{children}</div>
    ),
  };
});

describe('DownloadButton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render DownloadButton mobile component', async () => {
    mockProps.mobile = true;
    const { getByTestId } = render(await DownloadButton(mockProps));

    expect(getByTestId('DownloadButtonContent')).toBeInTheDocument();
  });
});
