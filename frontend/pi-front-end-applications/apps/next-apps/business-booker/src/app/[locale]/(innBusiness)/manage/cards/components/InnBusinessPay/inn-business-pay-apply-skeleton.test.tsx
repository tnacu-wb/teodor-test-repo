import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { InnBusinessPayApplySkeleton } from './inn-business-pay-apply-skeleton';

describe('InnBusinessPayApplySkeleton Component', () => {
  it('should render InnBusinessPayApplySkeleton component', () => {
    const { getByTestId } = render(<InnBusinessPayApplySkeleton />);
    expect(getByTestId('InnBusinessPayApplyTab-Skeleton')).toBeInTheDocument();
  });
});
