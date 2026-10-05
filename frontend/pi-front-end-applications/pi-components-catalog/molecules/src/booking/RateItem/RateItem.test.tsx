import '@testing-library/jest-dom';
import type { HIRoomType } from '@whitbread-eos/api';

import { render, userEvent } from '../../utils/test-utils';
import RateItem from './RateItem.component';

const mockRateItemProps = {
  brand: 'pi',
  value: '0-0',
  selectedRoomClassAndRate: '',
  setSelectedRoomClassAndRate: jest.fn(),
  roomRate: {
    rateCategory: '',
    ratePlanCode: 'FLEXRATE',
    cellCode: 'EMP01',
    roomTypes: [
      {
        roomType: 'TWIN',
        adults: 2,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'WINCMB',
            silentSubstitution: false,
            roomClass: 'ST',
            cotAvailable: false,
            roomPriceBreakdown: {
              totalNetAmount: 115.33,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2022-08-13',
                  netPrice: 75.0,
                },
                {
                  date: '2022-08-14',
                  netPrice: 40.33,
                },
              ],
            },
            specialRequests: ['TWDS'],
          },
          {
            pmsRoomType: 'TWDoubleBed',
            silentSubstitution: false,
            roomClass: 'ST',
            cotAvailable: false,
            roomPriceBreakdown: {
              totalNetAmount: 115.33,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2022-08-13',
                  netPrice: 75.0,
                },
                {
                  date: '2022-08-14',
                  netPrice: 40.33,
                },
              ],
            },
            specialRequests: ['TWDS'],
          },
        ],
      } as HIRoomType,
      {
        roomType: 'FAM',
        adults: 2,
        children: 1,
        cotRequested: true,
        rooms: [
          {
            pmsRoomType: 'FMTHRE',
            silentSubstitution: false,
            roomClass: 'ST',
            cotAvailable: true,
            roomPriceBreakdown: {
              totalNetAmount: 115.33,
              currencyCode: 'GBP',
              dailyPrices: [
                {
                  date: '2022-08-13',
                  netPrice: 75.0,
                },
                {
                  date: '2022-08-14',
                  netPrice: 40.33,
                },
              ],
            },
            specialRequests: ['TRIP'],
          },
        ],
      } as HIRoomType,
    ],
  },
  rateClassification: {
    rateClassification: 'FLEXRATE',
    rateDescription:
      'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
    rateLongDescription: '',
    rateName: 'Flex',
    rateNotes: '',
    rateOrder: '1',
    ratePlanCode: '',
    rateCategory: '',
    isCorporateDiscountAvailable: true,
  },
  totalReservationAmount: 230.66,
  totalBaseAmount: 0,
  numberOfNights: 2,
  numberOfUnits: 2,
};

const mockRateItemWithBasePriceProps = {
  ...mockRateItemProps,
  totalBaseAmount: 50,
};

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

//TODO - should render rate total reservation amount
describe('RateItem component', () => {
  it('should render RateItem', async () => {
    const { getByTestId } = render(<RateItem {...mockRateItemProps} />);
    const rateSelectionButton = getByTestId('hdp_ratePlanName');
    await userEvent.click(rateSelectionButton);
    expect(getByTestId('hdp_ratePlanName')).toBeInTheDocument();
  });

  it('should render with hub Flex rate plan', async () => {
    const { getByText } = render(<RateItem {...mockRateItemProps} brand="hub" />);
    expect(getByText('booking.rates.hub.prefix Flex')).toBeInTheDocument();
  });

  it('should render rate name', async () => {
    mockRateItemProps.rateClassification.rateName = 'Test';
    const { getByText, getByTestId } = render(<RateItem {...mockRateItemProps} />);
    expect(getByText('Test')).toBeInTheDocument();
    expect(getByTestId('hdp_ratePlanName')).toBeInTheDocument();
  });

  it('should render rate description', async () => {
    const { getByText, getByTestId } = render(<RateItem {...mockRateItemProps} />);
    expect(
      getByText(
        'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival'
      )
    ).toBeInTheDocument();
    expect(getByTestId('hdp_ratePlanDescription')).toBeInTheDocument();
  });

  it('should render rate total reservation amount', async () => {
    const { getByTestId } = render(<RateItem {...mockRateItemProps} />);
    expect(getByTestId('hdp_rateItemTotalPrice')).toBeInTheDocument();
    expect(getByTestId('hdp_rateItemTotalPriceTitle')).toBeInTheDocument();
  });

  it('should not render total base amount when not available', async () => {
    const { queryByTestId } = render(<RateItem {...mockRateItemProps} />);
    expect(queryByTestId('hdp_rateItemBasePrice')).not.toBeInTheDocument();
  });

  it('should render total base amount when available', async () => {
    const { getByTestId } = render(<RateItem {...mockRateItemWithBasePriceProps} />);
    expect(getByTestId('hdp_rateItemBasePrice')).toBeInTheDocument();
    expect(getByTestId('hdp_rateItemBasePrice')).toHaveTextContent('£50');
  });

  it('should render employee offer', async () => {
    const { getByText, getByTestId } = render(<RateItem {...mockRateItemProps} />);
    expect(getByText('promotion.EMP01.available')).toBeInTheDocument();
    expect(getByTestId('hdp_employeeOffer')).toBeInTheDocument();
  });

  it('should not render employee offer when cellCode is null', async () => {
    mockRateItemProps.roomRate.cellCode = '';
    const { queryByText, queryByTestId } = render(<RateItem {...mockRateItemProps} />);
    expect(queryByText('promotion.EMP01.available')).toBeNull();
    expect(queryByTestId('hdp_employeeOffer')).not.toBeInTheDocument();
  });

  it('should have by default the radio button checked', async () => {
    mockRateItemProps.value = '0-0';
    mockRateItemProps.selectedRoomClassAndRate = '0-0';
    const { getByRole } = render(<RateItem {...mockRateItemProps} />);
    const radio = getByRole('radio');
    expect(radio).toBeInTheDocument();
    expect(radio).toBeChecked();
  });

  it('should render no rate name if none provided', async () => {
    mockRateItemProps.rateClassification.rateName = '';
    const { queryByText } = render(<RateItem {...mockRateItemProps} />);
    expect(queryByText('Flex')).toBeNull();
    expect(queryByText('Test')).toBeNull();
  });

  it('should render base price with line through when available', async () => {
    const { getByTestId } = render(<RateItem {...mockRateItemWithBasePriceProps} />);
    const basePriceElement = getByTestId('hdp_rateItemBasePrice');
    expect(basePriceElement).toHaveTextContent('£50');
    expect(basePriceElement).toHaveStyle('text-decoration: line-through');
  });
});
