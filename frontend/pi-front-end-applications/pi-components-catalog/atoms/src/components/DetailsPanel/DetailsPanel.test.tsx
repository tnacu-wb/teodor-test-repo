import type { StyleProps, BoxProps } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import DetailsPanel from './DetailsPanel.component';

const stylesObject = {
  wrapper: {
    pos: 'relative',
    mt: 'md',
    mb: 'xl',
    py: 'lg',
    px: 'lg',
    w: { base: '100%', md: '40em' },
  } as BoxProps,
  title: {
    fontSize: 'lg',
    fontWeight: 'medium',
    color: 'baseBlack',
    mb: 'lg',
  } as StyleProps,
  rowKey: {
    fontSize: 'md',
    fontWeight: 'medium',
  } as StyleProps,
  rowValue: {
    fontSize: 'md',
    fontWeight: 'normal',
    color: 'lightGrey1',
  } as StyleProps,
};

const bookingData = {
  title: 'Your booking',
  rows: [
    {
      key: 'Booking Reference',
      value: '12345678',
    },
    {
      key: 'Hotel Name',
      value: 'Hotel Hill Montain',
    },
    {
      key: 'Arrival date',
      value: '25 MArch 2024',
    },
    {
      key: 'Departure Date',
      value: '25 March 2024',
    },
  ],
};

describe('DetailsPanel component', () => {
  it('should render the DetailsPanel Component', () => {
    const { getByTestId } = render(<DetailsPanel data={bookingData} stylesObject={stylesObject} />);
    expect(getByTestId(bookingData.title)).toBeInTheDocument();
    expect(getByTestId(bookingData.rows[0].key)).toBeInTheDocument();
  });
});
