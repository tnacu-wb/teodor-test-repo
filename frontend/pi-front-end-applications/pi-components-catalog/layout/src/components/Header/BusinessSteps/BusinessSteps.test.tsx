import React from 'react';

import { render } from '../../../utils/test-utils';
import { BusinessSteps, BusinessStepType } from './BusinessSteps.component';

const mockProps = {
  type: BusinessStepType.GUEST_DETAILS_PAGE,
};

describe('BusinessSteps component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render', () => {
    const { getByTestId } = render(<BusinessSteps {...mockProps} />);

    expect(getByTestId('BusinessSteps')).toBeInTheDocument();
  });
});
