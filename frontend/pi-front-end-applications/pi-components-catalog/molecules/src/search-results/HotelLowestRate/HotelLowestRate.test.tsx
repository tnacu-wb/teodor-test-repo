import '@testing-library/jest-dom';
import { formatPriceWithDecimal } from '@whitbread-eos/utils';

import { render } from '../../utils/test-utils';
import HotelLowestRate from './HotelLowestRate.component';

const mockedData = {
  lowestRoomRate: {
    currencyCode: 'GBP',
    netTotal: 158,
  },
};
const mockedLabels = {
  priceFrom: 'From',
};

describe('SRP - HotelLowestRate', () => {
  it('should render the component', () => {
    const { getByTestId } = render(
      <HotelLowestRate
        data={mockedData}
        locale="en"
        labels={mockedLabels}
        testId="SRP-lowest-rate"
        styles={{}}
        pricePerNight={true}
      />
    );
    expect(getByTestId('SRP-lowest-rate')).toBeInTheDocument();
  });
});

describe('formatPriceWithDecimal function', () => {
  it('should return {currency}{price} if the currency is £, regardless of the country', () => {
    expect(formatPriceWithDecimal('en', '£', mockedData.lowestRoomRate.netTotal)).toBe(
      `£${mockedData.lowestRoomRate.netTotal}`
    );
    expect(formatPriceWithDecimal('de', '£', mockedData.lowestRoomRate.netTotal)).toBe(
      `£${mockedData.lowestRoomRate.netTotal}`
    );
  });
  it('should return {currency}{price} if the locale is en and currency is €', () => {
    expect(formatPriceWithDecimal('en', '€', mockedData.lowestRoomRate.netTotal)).toBe(
      `€${mockedData.lowestRoomRate.netTotal}`
    );
  });
  it('should return {price} {currency} if the locale is de and the currency is €', () => {
    expect(formatPriceWithDecimal('de', '€', mockedData.lowestRoomRate.netTotal)).toBe(
      `${mockedData.lowestRoomRate.netTotal} €`
    );
  });
});
