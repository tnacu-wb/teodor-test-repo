import '@testing-library/jest-dom';
import { act, fireEvent, render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { SearchEmployeeInput } from './search-employee';

const mockProps = {
  searchIcon: '',
  searchInputPlaceHolder: '',
  clearIcon: '',
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
    sanitize: (value: string) => value,
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => jest.fn(),
}));

describe('SearchEmployeeInput Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const baseDataTestId = 'SearchEmployeeInput';

  it('should render SearchEmployeeInput component', async () => {
    const { getByTestId } = render(<SearchEmployeeInput {...mockProps} />);

    expect(getByTestId(`${baseDataTestId}-search-input-container`)).toBeInTheDocument();

    await act(async () => {
      fireEvent.change(getByTestId('SearchEmployeeInput-search-employees-Input'), {
        target: { value: 'test' },
      });
    });
  });
});
