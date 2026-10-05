import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import YourSpendingSkeleton from './your-spending-skeleton';

describe('YourSpendingSkeleton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render YourSpendingSkeleton component', async () => {
    const { getByTestId } = render(<YourSpendingSkeleton />);

    expect(getByTestId('YourSpendingSkeleton')).toBeInTheDocument();
  });
});
