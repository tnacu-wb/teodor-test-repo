import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import CompanySpendingSkeleton from './company-skeleton';

describe('CompanySpendingSkeleton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanySpendingSkeleton component', async () => {
    const { getByTestId } = render(<CompanySpendingSkeleton />);

    expect(getByTestId('CompanySpendingSkeleton')).toBeInTheDocument();
  });
});
