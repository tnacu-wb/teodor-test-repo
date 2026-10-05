import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import YourProfileSkeleton from './YourProfileSkeleton';

describe('YourProfileSkeleton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render YourProfileSkeleton component', async () => {
    const { getByTestId } = render(<YourProfileSkeleton />);

    expect(getByTestId('YourProfileSkeleton')).toBeInTheDocument();
  });
});
