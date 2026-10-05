import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { PaymentsNoResults } from './payments-no-results';

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

describe('PaymentsNoResults', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('renders the container row with correct data-testid', () => {
    render(<PaymentsNoResults />);
    expect(screen.getByTestId('PaymentsTable-NoResults-container')).toBeInTheDocument();
  });

  it('renders the title with correct text', () => {
    render(<PaymentsNoResults />);
    expect(screen.getByText('statementsInvoicesPayments.payments.empty.title')).toBeInTheDocument();
  });

  it('renders the description text', () => {
    render(<PaymentsNoResults />);
    expect(
      screen.getByText('statementsInvoicesPayments.payments.empty.description')
    ).toBeInTheDocument();
  });

  it('renders the TableCell with colSpan=6', () => {
    render(<PaymentsNoResults />);
    const cell = screen.getByText('statementsInvoicesPayments.payments.empty.title').closest('td');
    expect(cell).toHaveAttribute('colspan', '6');
  });
});
