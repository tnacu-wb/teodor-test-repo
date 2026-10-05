import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { SpendingProgress, SpendingProgressSkeleton } from './spending-progress';

jest.mock('~components/innBusiness/ProgressBar', () => ({
  ProgressBar: ({
    solidFillPercentage,
    patternFillPercentage,
    dataTestId,
    hasTransparency,
  }: any) => (
    <div data-testid={dataTestId}>
      <span data-testid="solid-fill">{solidFillPercentage}</span>
      <span data-testid="pattern-fill">{patternFillPercentage}</span>
      <span data-testid="has-transparency">{String(hasTransparency)}</span>
    </div>
  ),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Skeleton: ({ className }: any) => <div data-testid="skeleton" className={className} />,
}));

describe('SpendingProgress', () => {
  const baseDataTestId = 'test';
  const defaultBalance = {
    creditLimit: { amount: 1000 },
    outstanding: { amount: 200 },
    newTransactions: { amount: 100 },
  } as any;

  it('renders ProgressBar with correct percentages', () => {
    const { getByTestId } = render(
      <SpendingProgress
        currentBalance={defaultBalance}
        baseDataTestId={baseDataTestId}
        isAccountSuspended={false}
      />
    );
    expect(getByTestId('test-ProgressBar')).toBeInTheDocument();
    expect(getByTestId('solid-fill').textContent).toBe('20');
    expect(getByTestId('pattern-fill').textContent).toBe('10');
    expect(getByTestId('has-transparency').textContent).toBe('false');
  });

  it('renders ProgressBar with 0 percentages if creditLimit is missing', () => {
    const balance = {
      outstanding: { amount: 200 },
      newTransactions: { amount: 100 },
    } as any;
    const { getByTestId } = render(
      <SpendingProgress
        currentBalance={balance}
        baseDataTestId={baseDataTestId}
        isAccountSuspended={false}
      />
    );
    expect(getByTestId('solid-fill').textContent).toBe('0');
    expect(getByTestId('pattern-fill').textContent).toBe('0');
  });

  it('renders ProgressBar with 0 percentages if amounts are missing', () => {
    const balance = {
      creditLimit: { amount: 1000 },
    } as any;
    const { getByTestId } = render(
      <SpendingProgress
        currentBalance={balance}
        baseDataTestId={baseDataTestId}
        isAccountSuspended={false}
      />
    );
    expect(getByTestId('solid-fill').textContent).toBe('0');
    expect(getByTestId('pattern-fill').textContent).toBe('0');
  });

  it('passes hasTransparency as true when isAccountSuspended is true', () => {
    const { getByTestId } = render(
      <SpendingProgress
        currentBalance={defaultBalance}
        baseDataTestId={baseDataTestId}
        isAccountSuspended={true}
      />
    );
    expect(getByTestId('has-transparency').textContent).toBe('true');
  });
});

describe('SpendingProgressSkeleton', () => {
  it('renders Skeleton with correct className', () => {
    const { getByTestId } = render(<SpendingProgressSkeleton />);
    const skeleton = getByTestId('skeleton');
    expect(skeleton).toBeInTheDocument();
    expect(skeleton).toHaveClass('h-[25px] w-full mb-4');
  });
});
