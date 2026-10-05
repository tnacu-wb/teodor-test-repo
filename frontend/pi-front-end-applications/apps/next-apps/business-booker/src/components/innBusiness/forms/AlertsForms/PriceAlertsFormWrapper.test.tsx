import '@testing-library/jest-dom/extend-expect';
import { render, screen } from '@testing-library/react';
import { useTranslation } from '@whitbread-eos/utils/server';
import { useFormContext } from 'react-hook-form';

import { PriceInputsSection } from '~components/innBusiness/forms/AlertsForms/PriceInputsSection';

import { PriceAlertsFormWrapper } from './PriceAlertsFormWrapper';

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: jest.fn(),
}));

jest.mock('react-hook-form', () => ({
  useFormContext: jest.fn(),
}));

jest.mock('~components/innBusiness/forms/AlertsForms/PriceInputsSection', () => ({
  PriceInputsSection: jest.fn(() => <div data-testid="mocked-price-inputs-section" />),
}));

describe('PriceAlertsFormWrapper', () => {
  const mockIcons = {
    icon1: 'icon1',
    icon2: 'icon2',
  };

  const mockT = jest.fn((key) => key);
  const mockControl = {};
  const mockErrors = {};
  const mockTrigger = jest.fn();
  const mockClearErrors = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();

    (useTranslation as jest.Mock).mockReturnValue({
      t: mockT,
    });

    (useFormContext as jest.Mock).mockReturnValue({
      control: mockControl,
      formState: { errors: mockErrors },
      trigger: mockTrigger,
      clearErrors: mockClearErrors,
    });
  });

  it('renders with the correct data-testid', () => {
    render(<PriceAlertsFormWrapper icons={mockIcons} />);

    expect(screen.getByTestId('PriceAlertsFormWrapper-form')).toBeInTheDocument();
  });

  it('passes the correct props to PriceInputsSection', () => {
    render(<PriceAlertsFormWrapper icons={mockIcons} />);

    const priceInputsSectionProps = (PriceInputsSection as jest.Mock).mock.calls[0][0];

    expect(priceInputsSectionProps).toMatchObject({
      control: mockControl,
      errors: mockErrors,
      trigger: mockTrigger,
      clearErrors: mockClearErrors,
      icons: mockIcons,
      baseTestId: 'PriceAlertsFormWrapper-PriceInputs',
      ukLabelKey: 'company.coMngt.alerts.price.uk.label',
      londonLabelKey: 'company.coMngt.alerts.price.london.label',
      euLabelKey: 'company.coMngt.alerts.price.eu.label',
      placeholderKey: 'company.coMngt.alerts.price.placeholder',
      fieldNamePrefix: 'priceAlerts',
    });
    expect(typeof priceInputsSectionProps.t).toBe('function');
  });
});
