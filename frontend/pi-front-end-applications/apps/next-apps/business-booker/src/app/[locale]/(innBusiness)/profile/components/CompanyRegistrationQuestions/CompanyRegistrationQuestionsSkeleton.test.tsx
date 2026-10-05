import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import CompanyRegistrationQuestionsSkeleton from './CompanyRegistrationQuestionsSkeleton';

describe('CompanyRegistrationQuestionsSkeleton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyRegistrationQuestionsSkeleton component', async () => {
    const { getByTestId } = render(<CompanyRegistrationQuestionsSkeleton />);

    expect(getByTestId('CompanyRegistrationQuestionsSkeleton')).toBeInTheDocument();
  });
});
