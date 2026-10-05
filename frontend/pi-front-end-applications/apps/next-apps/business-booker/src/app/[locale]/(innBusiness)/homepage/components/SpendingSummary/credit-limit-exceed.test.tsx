import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { CreditLimitExceed } from './credit-limit-exceed';

const mockUseTranslationServer = jest.fn<{ t: (key: string) => string }, []>(() => {
  return {
    t: (str: string) => str,
  };
});

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCommonIcons: () => [],
    getTranslations: (...args: Parameters<typeof mockUseTranslationServer>) =>
      mockUseTranslationServer(...args),
  };
});

describe('CreditLimitExceed', () => {
  const locale = LOCALES.EN;

  it('renders the component with alert icon', async () => {
    const baseDataTestId = 'base';
    render(await CreditLimitExceed({ locale, baseDataTestId }));

    expect(screen.getAllByTestId(`${baseDataTestId}-CreditLimitExceed`)).toHaveLength(2);
    expect(screen.getByTestId(`${baseDataTestId}-CreditLimitExceedIcon`)).toBeInTheDocument();
    expect(screen.getByTestId(`${baseDataTestId}-CreditLimitExceedContent`)).toBeInTheDocument();
    expect(screen.getByTestId(`${baseDataTestId}-CreditLimitExceedTitle`)).toHaveTextContent(
      'homepage.home.upcoming.spending.creditExceeded'
    );
    expect(screen.getByTestId(`${baseDataTestId}-CreditLimitExceedDescription`)).toHaveTextContent(
      'homepage.home.upcoming.spending.creditExceededDescription'
    );
  });

  it('hides alert icon when translation missing', async () => {
    mockUseTranslationServer.mockReturnValueOnce({
      t: () => '',
    });

    const baseDataTestId = 'noIcon';
    const { queryByTestId, getAllByTestId } = render(
      await CreditLimitExceed({ locale, baseDataTestId })
    );

    expect(getAllByTestId(`${baseDataTestId}-CreditLimitExceed`)).toHaveLength(2);
    expect(queryByTestId(`${baseDataTestId}-CreditLimitExceedIcon`)).not.toBeInTheDocument();
    expect(mockUseTranslationServer).toHaveBeenCalled();
  });
});
