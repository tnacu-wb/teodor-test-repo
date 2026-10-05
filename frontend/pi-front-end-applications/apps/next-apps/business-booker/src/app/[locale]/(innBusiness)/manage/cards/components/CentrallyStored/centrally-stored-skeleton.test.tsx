import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { CentrallyStoredSkeleton } from './centrally-stored-skeleton';

describe('CentrallyStoredSkeleton Component', () => {
  it('should render CentrallyStoredSkeleton component', () => {
    const { getByTestId } = render(<CentrallyStoredSkeleton />);
    expect(getByTestId('CentrallyStoredTab-Skeleton')).toBeInTheDocument();
  });
});
