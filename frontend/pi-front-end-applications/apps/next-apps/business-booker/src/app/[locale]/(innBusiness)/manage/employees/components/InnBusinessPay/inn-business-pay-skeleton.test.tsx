import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { InnBusinessPaySkeleton } from './inn-business-pay-skeleton';

describe('InnBusinessPaySkeleton Component', () => {
  it('should render InnBusinessPaySkeleton component', () => {
    const { getByTestId } = render(<InnBusinessPaySkeleton />);
    expect(getByTestId('InnBusinessPayTab-Skeleton')).toBeInTheDocument();
  });
});
