import '@testing-library/jest-dom';

import { render } from '../../../../utils/test-utils';
import type { Props } from './HotelParking.component';
import HotelParkingComponent from './HotelParking.component';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

const props = {
  content:
    '<p>There is on-site chargeable parking available at £5 per night on non-match/event days &amp; £20 per night on match/event days. The car park is fully managed by Horizon Parking with parking available on a first come, first served basis.</p>',
} as Props;

const expectProp =
  'There is on-site chargeable parking available at £5 per night on non-match/event days & £20 per night on match/event days. The car park is fully managed by Horizon Parking with parking available on a first come, first served basis.';

describe('HotelParkingComponent', () => {
  it('it should render the HotelParkingComponent component', () => {
    const { getByText } = render(<HotelParkingComponent {...props} />);
    expect(getByText(expectProp)).toBeInTheDocument();
  });
});
