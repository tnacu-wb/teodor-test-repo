import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import { Email } from './email';

// Mock useTranslation
jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => {
      if (key === 'auth.payApp.login.email') return 'Email Address';
      return key;
    },
  }),
}));

describe('Email component', () => {
  const baseDataTestId = 'test-id';
  const value = 'user@example.com';

  it('renders the wrapper with correct data-testid', () => {
    render(<Email baseDataTestId={baseDataTestId} value={value} />);
    expect(screen.getByTestId(`${baseDataTestId}-Email-Wrapper`)).toBeInTheDocument();
  });

  it('renders the label with correct text and data-testid', () => {
    render(<Email baseDataTestId={baseDataTestId} value={value} />);
    const label = screen.getByTestId(`${baseDataTestId}-Email-Label`);
    expect(label).toBeInTheDocument();
    expect(label).toHaveTextContent('Email Address');
  });

  it('renders the value with correct data-testid', () => {
    render(<Email baseDataTestId={baseDataTestId} value={value} />);
    const valueSpan = screen.getByTestId(`${baseDataTestId}-Email-Value`);
    expect(valueSpan).toBeInTheDocument();
    expect(valueSpan).toHaveTextContent(value);
  });

  it('renders the actionsLeftStyle div with correct data-testid', () => {
    render(<Email baseDataTestId={baseDataTestId} value={value} />);
    expect(screen.getByTestId(baseDataTestId)).toBeInTheDocument();
  });

  it('renders empty value if value prop is empty', () => {
    render(<Email baseDataTestId={baseDataTestId} value="" />);
    expect(screen.getByTestId(`${baseDataTestId}-Email-Value`)).toHaveTextContent('');
  });
});
