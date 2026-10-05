import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import { ContactNumber } from './contact-number';

// Mock useTranslation from @whitbread-eos/utils/server
jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (key: string) => {
      if (key === 'auth.payApp.login.contactNumber') return 'Contact Number';
      return key;
    },
  }),
}));

describe('ContactNumber', () => {
  const baseDataTestId = 'test-id';
  const value = '+441234567890';

  it('renders the wrapper with correct data-testid', () => {
    render(<ContactNumber baseDataTestId={baseDataTestId} value={value} label={'Landline'} />);
    expect(screen.getByTestId(`${baseDataTestId}-Wrapper`)).toBeInTheDocument();
  });

  it('renders the label with correct text and data-testid', () => {
    render(<ContactNumber baseDataTestId={baseDataTestId} value={value} label={'Landline'} />);
    const label = screen.getByTestId(`${baseDataTestId}-Label`);
    expect(label).toBeInTheDocument();
    expect(label).toHaveTextContent('Landline');
  });

  it('renders the value with correct data-testid', () => {
    render(<ContactNumber baseDataTestId={baseDataTestId} value={value} label={'Landline'} />);
    const valueSpan = screen.getByTestId(`${baseDataTestId}-Value`);
    expect(valueSpan).toBeInTheDocument();
    expect(valueSpan).toHaveTextContent(value);
  });

  it('renders with a different value', () => {
    const newValue = '0123456789';
    render(<ContactNumber baseDataTestId={baseDataTestId} value={newValue} label={'Landline'} />);
    expect(screen.getByTestId(`${baseDataTestId}-Value`)).toHaveTextContent(newValue);
  });
});
