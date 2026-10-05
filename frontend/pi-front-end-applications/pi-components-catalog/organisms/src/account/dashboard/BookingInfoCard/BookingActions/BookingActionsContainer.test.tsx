import '@testing-library/jest-dom';
import { Area } from '@whitbread-eos/api';

import { render } from '../../../../utils/test-utils';
import BookingActionsContainer from './BookingActions.container';

const mockProps = {
  area: Area.PI,
  bookingStatus: 'success',
  basketReference: '',
};

describe('BookingActionsContainer', () => {
  it('should render BookingActionsContainer', () => {
    const { getByTestId } = render(<BookingActionsContainer {...mockProps} />);
    expect(getByTestId('BookingActions-Container')).toBeInTheDocument();
  });
});
