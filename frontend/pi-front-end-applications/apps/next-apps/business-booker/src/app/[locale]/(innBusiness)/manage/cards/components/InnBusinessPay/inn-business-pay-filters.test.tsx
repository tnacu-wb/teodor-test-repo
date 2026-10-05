import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { getAccessLevel } from '@whitbread-eos/utils/server';

import { InnBusinessPayFilters } from './inn-business-pay-filters';

const mockProps = {
  locale: LOCALES.EN,
  isCardHolderOnly: false,
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    getCountryLanguageByLocale: () => ({ language: 'en' }),
    getSearchParams: () => Promise.resolve(new URLSearchParams()),
    getTranslations: () => ({
      t: (str: string) => str,
    }),
    getAccessLevel: jest.fn().mockResolvedValue({
      accessLevel: 'SUPER',
      isTravelManager: false,
      isAccountHolder: false,
    }),
    cn: (...inputs: any[]) => inputs.join(' '),
  };
});

describe('InnBusinessPayFilters Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render InnBusinessPayFilters component', async () => {
    const component = await InnBusinessPayFilters(mockProps);
    const { getByTestId } = render(component);
    expect(getByTestId('InnBusinessPayFilters')).toBeInTheDocument();
  });

  it('should render all filters for Travel Manager + Account Holder', async () => {
    (getAccessLevel as jest.Mock).mockResolvedValueOnce({
      accessLevel: 'SUPER',
      isTravelManager: true,
      isAccountHolder: true,
    });

    const component = await InnBusinessPayFilters(mockProps);
    const { getByTestId } = render(component);
    expect(getByTestId('InnBusinessPayFilters-onlyMyCards')).toBeInTheDocument();
  });

  it('should hide "Only my cards" filter for Card Holder without Travel Manager role', async () => {
    (getAccessLevel as jest.Mock).mockResolvedValueOnce({
      accessLevel: 'SUPER',
      isTravelManager: false,
      isAccountHolder: false,
    });

    const component = await InnBusinessPayFilters({ ...mockProps, isCardHolderOnly: true });
    const { queryByTestId } = render(component);
    expect(queryByTestId('InnBusinessPayFilters-onlyMyCards')).not.toBeInTheDocument();
  });

  it('should show "Only my cards" filter for Card Holder with Travel Manager role', async () => {
    (getAccessLevel as jest.Mock).mockResolvedValueOnce({
      accessLevel: 'SUPER',
      isTravelManager: true,
      isAccountHolder: false,
    });

    const component = await InnBusinessPayFilters(mockProps);
    const { getByTestId } = render(component);
    expect(getByTestId('InnBusinessPayFilters-onlyMyCards')).toBeInTheDocument();
  });
});
