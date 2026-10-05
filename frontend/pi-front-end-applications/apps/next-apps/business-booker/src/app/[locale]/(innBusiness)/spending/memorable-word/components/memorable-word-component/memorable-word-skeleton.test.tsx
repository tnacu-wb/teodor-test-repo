import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import MemorableWordSkeleton from './memorable-word-skeleton';

describe('MemorableWordSkeleton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render MemorableWordSkeleton component', async () => {
    const { getByTestId } = render(<MemorableWordSkeleton />);

    expect(getByTestId('MemorableWordSkeleton')).toBeInTheDocument();
  });
});
