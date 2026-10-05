import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { UserNoResults, UserNoResultsProps } from './user-no-results';

const mockProps = {
  locale: LOCALES.EN,
} as UserNoResultsProps;

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getInitials: serverUtils.getInitials,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getSearchParams: () => Promise.resolve(new URLSearchParams()),
    getPathForLocale: () => {
      return '/';
    },
    sanitize: jest.fn((input: string) => input),
  };
});

describe('UserNoResults Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render UserNoResults component', async () => {
    const { getByTestId } = render(await UserNoResults(mockProps));

    expect(getByTestId('UserNoResults')).toBeInTheDocument();
  });
});
