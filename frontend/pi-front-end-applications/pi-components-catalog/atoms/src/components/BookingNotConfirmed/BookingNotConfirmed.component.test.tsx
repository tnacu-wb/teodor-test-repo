import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import BookingNotConfirmed from './BookingNotConfirmed.component';

const mockData = {
  t: (key: string) => {
    if (key === 'booking.confirmation.sorry') {
      return `We're sorry, we can't confirm your booking right now`;
    } else {
      return 'default';
    }
  },
  currentLang: 'en',
  data: {
    BookingNotConfirmed: {
      title: 'Mister',
      lastName: 'Sergio',
      firstName: 'aaa',
      emailAddress: 'sergio@mail.com',
    },
  },
};

describe('BookingNotConfirmed', () => {
  it('should render BookingNotConfirmed corectly', function () {
    const { getByText } = render(<BookingNotConfirmed {...mockData} />);

    expect(getByText(`We're sorry, we can't confirm your booking right now`)).toBeInTheDocument();
  });
});
