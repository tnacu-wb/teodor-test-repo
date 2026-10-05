import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { InnBusinessSkeleton } from './inn-business-skeleton';

describe('InnBusinessSkeleton Component', () => {
  it('should render InnBusinessSkeleton component', () => {
    const { getByTestId } = render(<InnBusinessSkeleton />);
    expect(getByTestId('InnBusinessTab-Skeleton')).toBeInTheDocument();
  });
});
