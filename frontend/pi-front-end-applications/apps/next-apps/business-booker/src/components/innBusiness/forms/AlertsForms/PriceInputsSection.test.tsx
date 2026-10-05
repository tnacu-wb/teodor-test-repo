import '@testing-library/jest-dom/extend-expect';
import { render, screen } from '@testing-library/react';
import { useTranslation } from '@whitbread-eos/utils/server';
import { useFormContext } from 'react-hook-form';

import { PriceInputsSection } from '~components/innBusiness/forms/AlertsForms/PriceInputsSection';

import { PriceAlertsFormWrapper } from './PriceAlertsFormWrapper';

jest.mock('@whitbread-eos/utils/server', () => ({
  formatIBAssetsUrl: jest.fn((url) => `formatted-${url}`),
  useTranslation: jest.fn(),
}));

jest.mock('class-variance-authority', () => ({
  cva: jest.fn(() => jest.fn()),
  VariantProps: jest.fn(),
}));

jest.mock('react-hook-form', () => ({
  useFormContext: jest.fn(),
  Controller: ({ render, ...props }: any) => (
    <div data-testid={`controller-${props.name}`}>
      {render({ field: { name: props.name, value: '', onChange: jest.fn(), onBlur: jest.fn() } })}
    </div>
  ),
}));

jest.mock('~components/innBusiness/forms/AlertsForms/PriceInputsSection', () => ({
  PriceInputsSection: jest.fn((props) => {
    return (
      <div data-testid={`${props.baseTestId}-inputs-container`}>
        <h4 data-testid={`${props.baseTestId}-uk-title`}>{props.t(props.ukLabelKey)}</h4>
        <div data-testid={`${props.baseTestId}-uk-input`}></div>

        <h4 data-testid={`${props.baseTestId}-london-title`}>{props.t(props.londonLabelKey)}</h4>
        <div data-testid={`${props.baseTestId}-london-input`}></div>

        <h4 data-testid={`${props.baseTestId}-eu-title`}>{props.t(props.euLabelKey)}</h4>
        <div data-testid={`${props.baseTestId}-eu-input`}></div>
      </div>
    );
  }),
  PriceFormValues: {},
}));

describe('PriceAlertsFormWrapper', () => {
  const mockIcons = {
    'icon.input.pound': 'pound-icon.svg',
    'icon.input.euro': 'euro-icon.svg',
    'icon.notification.error': 'error-icon.svg',
  };

  const mockT = jest.fn((key) => `Translated: ${key}`);
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
    expect(
      screen.getByTestId('PriceAlertsFormWrapper-PriceInputs-inputs-container')
    ).toBeInTheDocument();
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
    expect(priceInputsSectionProps.t).toBeInstanceOf(Function);
  });

  it('uses the correct fieldNamePrefix for form fields', () => {
    render(<PriceAlertsFormWrapper icons={mockIcons} />);

    const passedProps = (PriceInputsSection as jest.Mock).mock.calls[0][0];
    expect(passedProps.fieldNamePrefix).toBe('priceAlerts');
  });
});
