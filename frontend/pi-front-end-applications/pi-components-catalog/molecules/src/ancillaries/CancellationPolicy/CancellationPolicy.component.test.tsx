import { render, screen } from '@testing-library/react';
import React from 'react';

import CancellationPolicy from './CancellationPolicy.component';

// Mock dependencies
jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        'booking.cancellation.policy.title': 'Cancellation Policy',
        'booking.cancellation.policy.description': 'You have selected {rate}:',
      };
      return translations[key] || key;
    },
  }),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  CancelPolicy: (props: any) => <div data-testid="cancel-policy-icon" {...props} />,
}));

jest.mock(
  '../../common/BookingSummary/BookingSummaryUpgradeToFlex/BookingSummaryUpgradeToFlex',
  () => ({
    __esModule: true,
    default: (props: any) => <div data-testid="upgrade-to-flex" {...props} />,
  })
);

describe('CancellationPolicy', () => {
  it('renders the cancellation policy with rate description', () => {
    render(<CancellationPolicy rateDescription="Free until 24h before arrival" />);
    expect(screen.getByTestId('cancellationPolicy')).toBeInTheDocument();
    expect(screen.getByTestId('cancel-policy-icon')).toBeInTheDocument();
    expect(screen.getByText('Cancellation Policy')).toBeInTheDocument();
    expect(screen.getByText(/Free until 24h before arrival/)).toBeInTheDocument();
  });

  it('renders the upgrade to flex section when showUpgradeToFlex is true', () => {
    render(
      <CancellationPolicy
        rateDescription="Non-refundable"
        updateToFlex={
          {
            showUpgradeToFlex: true,
            someProp: 'value', // any required props for BookingSummaryUpgradeToFlex
          } as any
        }
      />
    );
    expect(screen.getByTestId('upgrade-to-flex')).toBeInTheDocument();
    expect(screen.getByRole('separator')).toBeInTheDocument();
  });

  it('does not render the upgrade to flex section when showUpgradeToFlex is false', () => {
    render(
      <CancellationPolicy
        rateDescription="Non-refundable"
        updateToFlex={
          {
            showUpgradeToFlex: false,
          } as any
        }
      />
    );
    expect(screen.queryByTestId('upgrade-to-flex')).not.toBeInTheDocument();
    expect(screen.queryByRole('separator')).not.toBeInTheDocument();
  });

  it('does not render the upgrade to flex section when hideUpgradeToFlex prop is true', () => {
    render(
      <CancellationPolicy
        rateDescription="Non-refundable"
        updateToFlex={
          {
            showUpgradeToFlex: true,
          } as any
        }
        hideUpgradeToFlex={true}
      />
    );
    expect(screen.queryByTestId('upgrade-to-flex')).not.toBeInTheDocument();
    expect(screen.queryByRole('separator')).not.toBeInTheDocument();
  });

  it('renders correct rateDescription with rate in bold', () => {
    render(<CancellationPolicy rate="Standard" />);
    const description = screen.getByText('You have selected ', { exact: false });
    expect(description).toBeInTheDocument();
    const strongElement = screen.getByText('Standard', { selector: 'strong' });
    expect(strongElement).toBeInTheDocument();
  });
});
