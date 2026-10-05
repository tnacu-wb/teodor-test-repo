import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { FormInput } from './form-input';

jest.mock('next/image', () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
}));

const baseProps = {
  id: 'firstName',
  name: 'firstName',
  errorIcon: 'error.png',
  showErrorTooltip: false,
  onChange: () => undefined,
};

describe('FormInput required accessibility', () => {
  it('marks the input as aria-required when the placeholder ends with "*"', () => {
    const { getByTestId } = render(<FormInput {...baseProps} placeholder="First name *" />);

    expect(getByTestId('firstName-Form-Input')).toHaveAttribute('aria-required', 'true');
  });

  it('does not set aria-required for a non-required placeholder', () => {
    const { getByTestId } = render(<FormInput {...baseProps} placeholder="First name" />);

    expect(getByTestId('firstName-Form-Input')).not.toHaveAttribute('aria-required');
  });

  it('honours an explicit aria-required prop even without a "*"', () => {
    const { getByTestId } = render(<FormInput {...baseProps} placeholder="Answer" aria-required />);

    expect(getByTestId('firstName-Form-Input')).toHaveAttribute('aria-required', 'true');
  });

  it('renders the input with the provided aria-label', () => {
    const { getByTestId } = render(
      <FormInput {...baseProps} showLabel={false} ariaLabel="Unit price" />
    );

    expect(getByTestId('firstName-Form-Input')).toHaveAttribute('aria-label', 'Unit price');
  });
});
