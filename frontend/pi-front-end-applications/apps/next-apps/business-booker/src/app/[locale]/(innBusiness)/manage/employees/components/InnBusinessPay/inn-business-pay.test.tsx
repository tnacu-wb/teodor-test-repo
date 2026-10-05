import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { InnBusinessPay } from './inn-business-pay';

const mockProps = {
  locale: LOCALES.EN,
};

const mockedCommonIBIcons = {};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getSearchParams: () => new URLSearchParams(),
    getCardManagementLabels: () => null,
    getCommonIcons: () => {
      return mockedCommonIBIcons;
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
  };
});

jest.mock('~components/innBusiness/AccountHolder/account-holder', () => {
  return { AccountHolder: () => null };
});

const baseDataTestId = 'InnBusinessPayTab';

describe('InnBusinessPay Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render InnBusinessPay component', async () => {
    const { getByTestId } = render(await InnBusinessPay(mockProps));

    expect(getByTestId(`${baseDataTestId}-container`)).toBeInTheDocument();
  });
});
