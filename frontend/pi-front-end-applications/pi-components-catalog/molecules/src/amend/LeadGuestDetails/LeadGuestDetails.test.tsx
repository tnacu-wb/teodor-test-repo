import { LeadGuestDetails } from '.';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import {
  mockedLeadGuestDetailsLabels,
  mockedLeadGuestValidationLabels,
} from '../utilities/mockResponse';

const initialProps = {
  labels: mockedLeadGuestDetailsLabels,
  baseDataTestId: 'amend',
  setAddNewRoomButtonEnabled: jest.fn(),
  validationLabels: mockedLeadGuestValidationLabels,
  leadGuestDetails: {
    title: 'Mr',
    firstName: 'test',
    lastName: 'testLast',
    emailAddress: 'test@gmail.com',
  },
  initialLeadGuestDetails: { title: '', firstName: '', lastName: '', emailAddress: '' },
  getFormState: undefined,
  onSubmit: jest.fn(),
};

describe('LeadGuestDetails component', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<LeadGuestDetails {...initialProps} />);
    expect(getByTestId('amend-lg-section')).toBeInTheDocument();
  });
});
