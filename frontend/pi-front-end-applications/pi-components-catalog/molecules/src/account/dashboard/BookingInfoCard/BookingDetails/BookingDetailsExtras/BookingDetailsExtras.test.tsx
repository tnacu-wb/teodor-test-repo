import { ExtrasId } from '@whitbread-eos/api';

import { render } from '../../../../../utils/test-utils';
import BookingDetailsExtrasComponent, { Props } from './BookingDetailsExtras.component';

const props = {
  language: 'en',
  bookingStatus: '',
  noNights: 1,
  currencyCode: 'GBP',
  childrenMealDescription: [
    {
      id: '2',
      noSelections: 1,
      title: 'ChildMeal',
    },
  ],
  adultMealDescription: [
    {
      id: '1',
      noSelections: 1,
      price: 1,
      title: 'AdultMeal',
    },
  ],
  area: 'pi',
  extrasPackageRoom: {
    packagesList: ['HSCKIN', 'HSCOU2', 'FI24HR'],
    priceEci: 10,
    reservationId: '111111',
    priceLco: 10,
    priceWifi: 10,
  },
} as Props;

describe('BookingDetailsExtrasComponent', () => {
  it('it should render the BookingDetailsExtrasComponent with default props', () => {
    const { getByText } = render(
      <BookingDetailsExtrasComponent
        {...props}
        featureToggles={{ release_pi_ancillaries_extras_display: true }}
      />
    );
    expect(getByText('AdultMeal')).toBeInTheDocument();
    expect(getByText('ChildMeal')).toBeInTheDocument();
    expect(getByText('£1.00')).toBeInTheDocument();
    expect(getByText('£0.00')).toBeInTheDocument();
    expect(
      getByText(
        'dashboard.bookings.priceFor 1 dashboard.bookings.night, 1 dashboard.bookings.guest'
      )
    ).toBeInTheDocument();
    expect(getByText('dashboard.bookings.priceFor 1 dashboard.bookings.child')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsExtrasComponent with 2 adults and 2 children', () => {
    props.adultMealDescription[0].noSelections = 2;
    props.childrenMealDescription[0].noSelections = 2;
    const { getByText } = render(<BookingDetailsExtrasComponent {...props} />);
    expect(getByText('£2.00')).toBeInTheDocument();
    expect(getByText('£0.00')).toBeInTheDocument();
    expect(
      getByText(
        'dashboard.bookings.priceFor 1 dashboard.bookings.night, 2 dashboard.bookings.guests'
      )
    ).toBeInTheDocument();
    expect(
      getByText('dashboard.bookings.priceFor 2 dashboard.bookings.children')
    ).toBeInTheDocument();
  });

  it('it should render the BookingDetailsExtrasComponent with 2 nights', () => {
    props.noNights = 2;
    const { getByText } = render(<BookingDetailsExtrasComponent {...props} />);
    expect(getByText('£4.00')).toBeInTheDocument();
    expect(getByText('£0.00')).toBeInTheDocument();
    expect(
      getByText(
        'dashboard.bookings.priceFor 2 dashboard.bookings.nights, 2 dashboard.bookings.guests'
      )
    ).toBeInTheDocument();
  });

  it('it should render the BookingDetailsExtrasComponent with language in de', () => {
    props.language = 'de';
    props.currencyCode = 'EUR';
    const { getByText } = render(<BookingDetailsExtrasComponent {...props} />);
    expect(getByText('4,00€')).toBeInTheDocument();
    expect(getByText('0,00€')).toBeInTheDocument();
  });
  it('it should render the BookingDetailsExtrasComponent with ECI/LCO', () => {
    const { getAllByTestId } = render(
      <BookingDetailsExtrasComponent {...props} showEciLco={true} />
    );
    expect(getAllByTestId('extras-price').length).toBe(3);
    expect(getAllByTestId('extras-name').length).toBe(3);
  });
  it('should return correct price for early check-in (ECI)', () => {
    const mockProps: Props = {
      adultMealDescription: [],
      childrenMealDescription: [],
      currencyCode: 'GBP',
      language: 'en',
      noNights: 2,
      bookingStatus: '',
      extrasPackageRoom: {
        reservationId: '1234',
        priceEci: 20.0,
        priceLco: 15.0,
        priceWifi: 25.0,
        priceBOProsecco: 25.0,
        packagesList: [
          ExtrasId.EARLY_CHECK_IN,
          ExtrasId.LATE_CHECK_OUT,
          ExtrasId.ULTIMATE_WIFI,
          ExtrasId.BOTTLE_OF_PROSECCO,
        ],
      },
      showEciLco: true,
    };
    const { getAllByTestId } = render(
      <BookingDetailsExtrasComponent {...mockProps} showEciLco={true} />
    );

    const extrasPrices = getAllByTestId('extras-price');
    expect(extrasPrices[0]).toHaveTextContent('£20.00');
    expect(extrasPrices[1]).toHaveTextContent('£15.00');
    expect(extrasPrices[2]).toHaveTextContent('£25.00');
    expect(extrasPrices[3]).toHaveTextContent('£25.00');
  });

  it('should NOT use WIFI price for non-WIFI extras', () => {
    const mockProps: Props = {
      adultMealDescription: [],
      childrenMealDescription: [],
      currencyCode: 'GBP',
      language: 'en',
      noNights: 1,
      bookingStatus: '',
      extrasPackageRoom: {
        reservationId: '1234',
        priceWifi: 30.0,
        priceEci: 10.0,
        packagesList: [ExtrasId.EARLY_CHECK_IN],
      },
      showEciLco: true,
    };

    const { getByTestId } = render(<BookingDetailsExtrasComponent {...mockProps} showEciLco />);

    expect(getByTestId('extras-price')).toHaveTextContent('£10.00');
  });

  it('should handle undefined extras id gracefully', () => {
    const mockProps: Props = {
      adultMealDescription: [],
      childrenMealDescription: [],
      currencyCode: 'GBP',
      language: 'en',
      noNights: 1,
      bookingStatus: '',
      extrasPackageRoom: {
        reservationId: '1234',
        priceWifi: 30.0,
        packagesList: [undefined as unknown as string],
      },
      showEciLco: true,
    };

    const { getByTestId } = render(<BookingDetailsExtrasComponent {...mockProps} showEciLco />);

    // Should not crash and should render empty price
    expect(getByTestId('extras-price')).toBeInTheDocument();
  });

  it('should return correct price for WIFI extras', () => {
    const mockProps: Props = {
      adultMealDescription: [],
      childrenMealDescription: [],
      currencyCode: 'GBP',
      language: 'en',
      noNights: 1,
      bookingStatus: '',
      extrasPackageRoom: {
        reservationId: '1234',
        priceWifi: 30.0,
        packagesList: [ExtrasId.ULTIMATE_WIFI],
      },
      showEciLco: true,
    };

    const { getByTestId } = render(<BookingDetailsExtrasComponent {...mockProps} showEciLco />);

    expect(getByTestId('extras-price')).toHaveTextContent('£30.00');
  });
});
