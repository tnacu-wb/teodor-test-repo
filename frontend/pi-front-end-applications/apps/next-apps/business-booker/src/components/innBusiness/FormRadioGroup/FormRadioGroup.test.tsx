import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import * as React from 'react';

import { FormRadioGroup } from './FormRadioGroup';

jest.mock('@whitbread-eos/atoms/ui', () => ({
  RadioGroup: function RadioGroup(props: {
    onValueChange: (value: string) => void;
    children: React.ReactNode;
  }) {
    return (
      <div
        role="radiogroup"
        onChange={(e) => props.onValueChange?.((e.target as HTMLInputElement).value)}
      >
        {props.children}
      </div>
    );
  },
  RadioGroupItem: function RadioGroupItem(props: {
    value: string;
    id: string;
    dataTestId: string;
  }) {
    return (
      <input type="radio" value={props.value} id={props.id} data-testid={`radio-${props.value}`} />
    );
  },
  Label: function Label(props: { htmlFor: string; className: string; children: React.ReactNode }) {
    return (
      <label
        htmlFor={props.htmlFor}
        data-testid={`label-${props.htmlFor}`}
        className={props.className}
      >
        {props.children}
      </label>
    );
  },
  ErrorTooltip: function ErrorTooltip(props: { testId: string; content: React.ReactNode }) {
    return (
      <div data-testid={props.testId} role="alert">
        {props.content}
      </div>
    );
  },
}));

describe('FormRadioGroup', () => {
  const mockItems = [
    { value: 'option1', label: 'Option 1' },
    { value: 'option2', label: 'Option 2', description: 'Description 2' },
  ];

  const mockPaymentItems = [
    {
      value: 'card',
      label: 'Credit Card',
      icons: [<span key="visa">VISA</span>],
    },
    {
      value: 'piba',
      label: 'InnBusiness Pay',
      icons: [<span key="piba">PIBA</span>],
    },
  ];

  it('renders default variant correctly', () => {
    render(<FormRadioGroup items={mockItems} name="test-radio" />);

    expect(screen.getByText('Option 1')).toBeInTheDocument();
    expect(screen.getByText('Option 2')).toBeInTheDocument();
    expect(screen.getByTestId('radio-option1')).toBeInTheDocument();
    expect(screen.getByTestId('radio-option2')).toBeInTheDocument();
  });

  it('renders payment variant with icons', () => {
    render(
      <FormRadioGroup
        items={mockPaymentItems}
        variant="payment"
        name="payment-radio"
        selectedValue="card"
      />
    );

    expect(screen.getByText('Credit Card')).toBeInTheDocument();
    expect(screen.getByText('VISA')).toBeInTheDocument();
    expect(screen.getByTestId('label-card')).toBeInTheDocument();
  });

  it('renders address variant with description', () => {
    render(
      <FormRadioGroup
        items={[
          {
            value: 'address1',
            label: 'Home Address',
            description: '123 Main St',
          },
        ]}
        variant="address"
        name="address-radio"
      />
    );

    expect(screen.getByText('Home Address')).toBeInTheDocument();
    expect(screen.getByText('123 Main St')).toBeInTheDocument();
    expect(screen.getByTestId('label-address1')).toBeInTheDocument();
  });

  it('shows error tooltip when error is present', () => {
    const errors = {
      'test-radio': { message: 'This field is required' },
    };

    render(
      <FormRadioGroup
        items={mockItems}
        name="test-radio"
        errors={errors}
        errorIcon="error-icon.svg"
      />
    );

    expect(screen.getByRole('alert')).toBeInTheDocument();
    expect(screen.getByText('This field is required')).toBeInTheDocument();
  });

  it('calls onChange when selection changes', () => {
    const handleChange = jest.fn();
    render(<FormRadioGroup items={mockItems} name="test-radio" onChange={handleChange} />);

    const radio = screen.getByTestId('radio-option2');
    fireEvent.click(radio);

    expect(handleChange).toHaveBeenCalledWith('option2');
  });
});
