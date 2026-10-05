import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { InnBusinessPayApply } from './inn-business-pay-apply';

const mockProps = {
  locale: LOCALES.EN,
  token: 'dummy-token',
};

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
    getCommonIcons: () => null,
    formatIBAssetsUrl: () => {
      return '/';
    },
    appPreCheck: () => ({ isTetheredUser: false }),
  };
});

jest.mock('~components/innBusiness/NoAccountBanner', () => {
  return { NoAccountBanner: () => <span data-testid="NoAccountBanner">No Account</span> };
});

jest.mock('~components/innBusiness/BenefitsBoxes', () => {
  return { BenefitsBoxes: () => <span data-testid="BenefitsBoxes">BenefitsBoxes</span> };
});

describe('InnBusinessPayApply Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render InnBusinessPay component', async () => {
    const { getByTestId } = render(await InnBusinessPayApply(mockProps));

    expect(getByTestId('Inn-Business-Pay-Apply-Container')).toBeInTheDocument();
  });
});
