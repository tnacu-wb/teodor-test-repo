import '@testing-library/jest-dom/extend-expect';
import { render, screen, waitFor } from '@testing-library/react';
import { FT_IB_OUT_OF_POLICY_REPORT } from '@whitbread-eos/api';

const mockProps = {
  icons: {
    'icon.meeting-rooms-icon': 'meeting-rooms-icon-url',
    'icon.file.icon.purple': 'file-icon-purple-url',
    'icon.bookings-icon': 'bookings-icon-url',
  },
  locale: 'en',
};

const utilsServerMock = {
  cn: jest.fn(),
  getCountryLanguageByLocale: jest.fn().mockReturnValue({ language: 'en' }),
  formatIBAssetsUrl: jest.fn((key: string) => `/${key}`),
  getPathForLocale: jest.fn((locale: string, url: string) => `/${locale}/${url}`),
  getTranslations: () => ({ t: (key: string) => key }),
  getServerUnleashToggles: jest.fn().mockResolvedValue({
    [FT_IB_OUT_OF_POLICY_REPORT]: true,
  }),
};
jest.mock('@whitbread-eos/utils/server', () => utilsServerMock);

jest.mock('next/headers', () => ({
  headers: jest.fn(() => ({ get: jest.fn() })),
}));

describe('CompanyCardList', () => {
  let CardList: typeof import('./card-list').default;

  beforeAll(async () => {
    // Dynamically import after the mock is set up
    const mod = await import('./card-list');
    CardList = mod.default;
    jest.mock('@whitbread-eos/utils/server', () => utilsServerMock);
  });

  it('renders the CardList component', async () => {
    const { getByText } = render(await CardList({ ...mockProps }));
    expect(getByText('spending.reporting.card.management.reporting.title')).toBeInTheDocument();
    expect(getByText('spending.reporting.card.management.reporting.subtitle')).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByTestId('OutOfPolicyReportCard-CardWrapper')).toBeInTheDocument();
    });

    expect(getByText('spending.reporting.card.emergency.report.title')).toBeInTheDocument();
    expect(getByText('spending.reporting.card.emergency.report.subtitle')).toBeInTheDocument();
  });

  it('renders the correct icons', async () => {
    const { getByAltText } = render(await CardList({ ...mockProps }));

    expect(getByAltText('spending.reporting.card.management.reporting.title icon')).toHaveAttribute(
      'src'
    );
    expect(getByAltText('spending.reporting.card.policy.reporting.title icon')).toHaveAttribute(
      'src'
    );
    expect(getByAltText('spending.reporting.card.emergency.report.title icon')).toHaveAttribute(
      'src'
    );
  });

  it('renders the correct hrefs', async () => {
    const { getByTestId } = render(await CardList({ ...mockProps }));

    expect(getByTestId('ManagementInformationReportCard')).toHaveAttribute(
      'href',
      '/en/spending/management-information-report'
    );
    expect(await screen.findByTestId('OutOfPolicyReportCard')).toHaveAttribute(
      'href',
      '/en/spending/out-of-policy-report'
    );
    expect(getByTestId('EmergencyReportCard')).toHaveAttribute(
      'href',
      '/en/spending/emergency-report'
    );
  });
});
