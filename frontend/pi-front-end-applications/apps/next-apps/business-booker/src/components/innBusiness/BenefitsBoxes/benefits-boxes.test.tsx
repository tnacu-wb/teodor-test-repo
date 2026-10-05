import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import BenefitsBoxes from './benefits-boxes';

jest.mock('@whitbread-eos/utils/server', () => ({
  appPreCheck: jest.fn(),
  getCountryLanguageByLocale: jest.fn(() => Promise.resolve({ language: 'en' })),
  useTranslation: jest.fn(() => ({
    t: (key: string) => `${key}`,
  })),
  formatIBAssetsUrl: jest.fn((icon: string) => `/assets/${icon}`),
  cn: jest.fn((...classes: string[]) => classes.join(' ')),
}));
jest.mock('next/image', () => {
  const MockImage = (props: any) => <img {...props} />;
  MockImage.displayName = 'Image';
  return MockImage;
});

jest.mock('../LinkAccountButton', () => ({
  LinkAccountButton: ({ 'data-testid': dataTestId, className }: any) => (
    <button data-testid={dataTestId} className={className}>
      cardMgmt.linkAccountBanner.linkAccountButton
    </button>
  ),
}));

describe('BenefitsBoxes', () => {
  const token = 'dummy-token';

  it('renders all benefit boxes with correct titles and subtitles', async () => {
    render(<BenefitsBoxes token={token} locale={LOCALES.EN} />);
    await waitFor(() => {
      expect(screen.getByTestId('BenefitsBoxes-Container')).toBeInTheDocument();
    });

    expect(screen.getAllByAltText(/cardMgmt\..*\.title/)).toHaveLength(3);

    expect(screen.getByText('cardMgmt.creditBox.title')).toBeInTheDocument();
    expect(screen.getByText('cardMgmt.creditBox.subtitle')).toBeInTheDocument();

    expect(screen.getByText('cardMgmt.expenseBox.title')).toBeInTheDocument();
    expect(screen.getByText('cardMgmt.expenseBox.subtitle')).toBeInTheDocument();

    expect(screen.getByText('cardMgmt.invoicesBox.title')).toBeInTheDocument();
    expect(screen.getByText('cardMgmt.invoicesBox.subtitle')).toBeInTheDocument();
  });

  it('renders the link account section with correct texts and button', async () => {
    const { container } = render(<BenefitsBoxes token={token} locale={LOCALES.EN} />);
    expect(container).toBeInTheDocument();
    await waitFor(() => {
      expect(screen.getByTestId('BenefitsBoxes-LinkAccountContainer')).toBeInTheDocument();
    });

    expect(screen.getByText('cardMgmt.linkAccountBanner.title')).toBeInTheDocument();
    expect(screen.getByText('cardMgmt.linkAccountBanner.subtitle')).toBeInTheDocument();
    expect(screen.getByTestId('BenefitsBoxes-Button')).toBeInTheDocument();
    expect(screen.getByText('cardMgmt.linkAccountBanner.linkAccountButton')).toBeInTheDocument();
  });
});
