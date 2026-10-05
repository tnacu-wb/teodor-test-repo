import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { CompanyEmployees } from './company-employees';

const mockProps = {
  numberOfEmployees: 12,
  companyName: 'Company test',
  locale: LOCALES.EN,
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
    getCommonIcons: () => null,
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
  };
});

describe('CompanyEmployees Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const baseDataTestId = 'CompanyEmployees';

  it('should render CompanyEmployees component', async () => {
    const { getByTestId } = render(await CompanyEmployees(mockProps));

    expect(getByTestId(`${baseDataTestId}-container`)).toBeInTheDocument();
    expect(getByTestId(`${baseDataTestId}-text`)).toBeInTheDocument();
  });
});
