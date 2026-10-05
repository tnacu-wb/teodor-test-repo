import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { NoResultsStatements } from './no-results-statements';

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
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

describe('NoResultsStatements Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render NoResultsStatements component', () => {
    const { getByTestId } = render(<NoResultsStatements />);

    expect(getByTestId('NoResultsStatements-container')).toBeInTheDocument();
  });
});
