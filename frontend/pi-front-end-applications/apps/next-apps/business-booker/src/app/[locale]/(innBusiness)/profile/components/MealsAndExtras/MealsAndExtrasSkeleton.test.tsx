import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import MealsAndExtrasSkeleton from './MealsAndExtrasSkeleton';

describe('MealsAndExtrasSkeleton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render MealsAndExtrasSkeleton component', async () => {
    const { getByTestId } = render(<MealsAndExtrasSkeleton />);

    expect(getByTestId('MealsAndExtrasSkeleton')).toBeInTheDocument();
  });
});
