import '@testing-library/jest-dom/extend-expect';
import { render, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { getTranslations } from '@whitbread-eos/utils/server';

import { SpendingTabs, SPENDING_TABS } from './tabs';

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  getCountryLanguageByLocale: jest.fn().mockReturnValue({ language: 'en' }),
  getTranslations: jest.fn(),
  getSearchParams: jest.fn().mockResolvedValue(new URLSearchParams()),
}));

jest.mock('next/headers', () => ({
  headers: () => ({
    get: () => 'test',
  }),
}));

describe('SpendingTabs', () => {
  beforeEach(() => {
    (getTranslations as jest.Mock).mockResolvedValue({
      t: (key: string) => key,
    });
  });

  it('renders company tab as active when selectedTab is COMPANY', async () => {
    render(
      await SpendingTabs({
        selectedTab: SPENDING_TABS.COMPANY,
        locale: LOCALES.EN,
        isTravelManager: true,
        showYourSpendingTab: false,
      })
    );
    const companyTab = await screen.findByTestId('SpendingTabs-InnBusinessTab');
    expect(companyTab).toHaveAttribute('data-state', 'active');
  });

  it('does not render Company spending tab when user is not travel manager', async () => {
    render(
      await SpendingTabs({
        selectedTab: SPENDING_TABS.COMPANY,
        locale: LOCALES.EN,
        isTravelManager: false,
        showYourSpendingTab: false,
      })
    );

    expect(screen.queryByTestId('SpendingTabs-InnBusinessTab')).not.toBeInTheDocument();
  });

  it('renders innbusiness pay tab as active when selectedTab is INN_BUSINESS_PAY', async () => {
    render(
      await SpendingTabs({
        selectedTab: SPENDING_TABS.INN_BUSINESS_PAY,
        locale: LOCALES.EN,
        isTravelManager: true,
        showYourSpendingTab: false,
      })
    );
    const innbusinessPayTab = await screen.findByTestId('SpendingTabs-innbusinessPayTab');
    expect(innbusinessPayTab).toHaveAttribute('data-state', 'active');
  });

  it('renders company tab with correct text', async () => {
    render(
      await SpendingTabs({
        selectedTab: SPENDING_TABS.COMPANY,
        locale: LOCALES.EN,
        isTravelManager: true,
        showYourSpendingTab: false,
      })
    );
    const companyTab = await screen.findByTestId('SpendingTabs-InnBusinessTab');
    expect(companyTab).toHaveTextContent('spending.reporting.tab.company.spending');
  });

  it('renders innbusiness pay tab with correct text', async () => {
    render(
      await SpendingTabs({
        selectedTab: SPENDING_TABS.INN_BUSINESS_PAY,
        locale: LOCALES.EN,
        isTravelManager: true,
        showYourSpendingTab: false,
      })
    );
    const innbusinessPayTab = await screen.findByTestId('SpendingTabs-innbusinessPayTab');
    expect(innbusinessPayTab).toHaveTextContent('spending.reporting.tab.innbusinessPay');
  });

  it('does not render Your spending tab when showYourSpendingTab is false', async () => {
    render(
      await SpendingTabs({
        selectedTab: SPENDING_TABS.COMPANY,
        locale: LOCALES.EN,
        isTravelManager: true,
        showYourSpendingTab: false,
      })
    );

    expect(screen.queryByTestId('SpendingTabs-YourSpendingTab')).not.toBeInTheDocument();
  });

  it('renders Your spending tab when showYourSpendingTab is true', async () => {
    render(
      await SpendingTabs({
        selectedTab: SPENDING_TABS.YOUR_SPENDING,
        locale: LOCALES.EN,
        isTravelManager: true,
        showYourSpendingTab: true,
      })
    );
    const yourSpending = await screen.findByTestId('SpendingTabs-YourSpendingTab');
    expect(yourSpending).toHaveTextContent('spending.reporting.tab.employee.spending');
  });
});
