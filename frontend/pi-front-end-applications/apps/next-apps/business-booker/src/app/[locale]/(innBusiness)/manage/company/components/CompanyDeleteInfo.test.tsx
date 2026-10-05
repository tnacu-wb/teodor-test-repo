import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { CompanyDeleteInfo } from './CompanyDeleteInfo';

const mockProps = {
  icons: {},
  locale: LOCALES.EN,
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getPathForLocale: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

describe('CompanyDeleteInfo Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyDeleteInfo component', async () => {
    const { getByTestId } = render(<CompanyDeleteInfo {...mockProps} />);

    expect(getByTestId('CompanyDeleteInfo-container')).toBeInTheDocument();
  });
});
