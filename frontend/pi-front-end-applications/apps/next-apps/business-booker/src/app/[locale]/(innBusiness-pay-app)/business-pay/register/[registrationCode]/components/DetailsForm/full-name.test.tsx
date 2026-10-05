import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import { FullName } from './full-name';

// Mock useTranslation from @whitbread-eos/utils
jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => {
      if (key === 'auth.payApp.login.fullName') return 'Full Name';
      return key;
    },
  }),
}));

describe('FullName', () => {
  const baseDataTestId = 'test-id';
  const value = 'John Doe';

  it('renders the wrapper with correct data-testid', () => {
    render(<FullName baseDataTestId={baseDataTestId} value={value} />);
    expect(screen.getByTestId(`${baseDataTestId}-FullName-Wrapper`)).toBeInTheDocument();
  });

  it('renders the label with correct text and data-testid', () => {
    render(<FullName baseDataTestId={baseDataTestId} value={value} />);
    const label = screen.getByTestId(`${baseDataTestId}-FullName-Label`);
    expect(label).toBeInTheDocument();
    expect(label).toHaveTextContent('Full Name');
  });

  it('renders the value with correct data-testid', () => {
    render(<FullName baseDataTestId={baseDataTestId} value={value} />);
    const valueSpan = screen.getByTestId(`${baseDataTestId}-FullName-Value`);
    expect(valueSpan).toBeInTheDocument();
    expect(valueSpan).toHaveTextContent(value);
  });

  it('renders the actionsLeftStyle div with correct data-testid', () => {
    render(<FullName baseDataTestId={baseDataTestId} value={value} />);
    expect(screen.getByTestId(baseDataTestId)).toBeInTheDocument();
  });

  it('renders the correct class names', () => {
    render(<FullName baseDataTestId={baseDataTestId} value={value} />);
    expect(screen.getByTestId(`${baseDataTestId}-FullName-Wrapper`).className).toContain(
      'flex flex-col'
    );
    expect(screen.getByTestId(`${baseDataTestId}-FullName-Label`).className).toContain(
      'whitespace-nowrap'
    );
  });
});
