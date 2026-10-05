import '@testing-library/jest-dom';

import { render, userEvent } from '../../utils/test-utils';
import ResultList, { type Props } from './ResultList.container';

const changePageMock = jest.fn();

const mockProps: Props = {
  inputValues: {
    bookingReference: 'AWM6248757',
  },
  t: (key: string) => key,
  baseDataTestId: 'ResultList',
  resultsData: {
    bartId: null,
    hasMore: false,
    offset: 0,
    searchData: [
      {
        cells: [
          {
            id: 'BookedFor',
            value: 'Mrs Jane Doe',
          },
          {
            id: 'BookedBy',
            value: 'Mr John Doe',
          },
          {
            id: 'Hotel',
            value: 'London Euston',
          },
          {
            id: 'Date',
            value: '2023-02-23',
          },
          {
            id: 'Status',
            value: 'Upcoming',
          },
          {
            id: 'SourcePms',
            value: 'Opera',
          },
        ],
        basketReference: 'AWM6248757',
        isExpandedByDefault: true,
      },
    ],
    isLoading: false,
    isSuccess: true,
    isError: false,
    error: '',
  },
  changePage: changePageMock,
  bartHotelName: 'Bart Hotel',
};

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('ResultList', () => {
  afterAll(() => {
    jest.resetAllMocks();
  });

  it('should render a <ResultList> with default props', function () {
    const { getByTestId } = render(<ResultList {...mockProps} />);

    expect(getByTestId('ResultList-Table-Container')).toBeInTheDocument();
  });

  it('should have the booker name, booked for and hotel name in the table for an Opera booking', function () {
    const { getByText, queryByText } = render(<ResultList {...mockProps} />);
    expect(getByText('Mr John Doe')).toBeInTheDocument();
    expect(getByText('Mrs Jane Doe')).toBeInTheDocument();
    expect(queryByText('Bart Hotel')).not.toBeInTheDocument();
    expect(getByText('Opera')).toBeInTheDocument();
  });

  it('should have the booker name, booked for and hotel name in the table for an BART booking', function () {
    const props = {
      ...mockProps,
      resultsData: {
        bartId: 'AWM6248757',
        hasMore: false,
        offset: 0,
        searchData: [
          {
            cells: [
              {
                id: 'BookedFor',
                value: 'Mrs Jane Doe',
              },
              {
                id: 'BookedBy',
                value: 'Mr John Doe',
              },
              {
                id: 'Hotel',
                value: 'Bart Hotel',
              },
              {
                id: 'Date',
                value: '2023-02-23',
              },
              {
                id: 'Status',
                value: 'Upcoming',
              },
              {
                id: 'SourcePms',
                value: 'Bart',
              },
            ],
            basketReference: 'AEAR423983',
            isExpandedByDefault: true,
          },
        ],
      },
    };
    const { getByText } = render(<ResultList {...props} />);
    expect(getByText('Mr John Doe')).toBeInTheDocument();
    expect(getByText('Mrs Jane Doe')).toBeInTheDocument();
    expect(getByText('Bart Hotel')).toBeInTheDocument();
    expect(getByText('Bart')).toBeInTheDocument();
  });

  it('should display the show more button and allow it to be clicked if there are more results', async function () {
    const props = {
      ...mockProps,
      resultsData: {
        ...mockProps.resultsData,
        hasMore: true,
        offset: 0,
      },
    };

    const { getByRole } = render(<ResultList {...props} />);
    const loadMoreBtn = getByRole('button', { type: 'button' });

    userEvent.click(loadMoreBtn);
    await expect(changePageMock).toHaveBeenCalledTimes(1);
  });
});

// TODO - need to add props here - fix - see Copilot recommendation
describe('ResultsListContainer', () => {
  it('renders when isRemovePIIDataFromLocalStorageEnabled is true', () => {
    const { getByTestId } = render(
      <ResultList {...mockProps} isRemovePIIDataFromLocalStorageEnabled={true} />
    );

    expect(getByTestId('ResultList-Table-Container')).toBeInTheDocument();
  });

  it('renders legacy header titles when isBookingHistoryRedesignCCUIEnabled is false', () => {
    const { getByText, queryByText } = render(
      <ResultList {...mockProps} isBookingHistoryRedesignCCUIEnabled={false} />
    );
    expect(getByText('ccui.manageBooking.resultList.bookedFor')).toBeInTheDocument();
    expect(getByText('ccui.manageBooking.resultList.bookedBy')).toBeInTheDocument();
    expect(getByText('ccui.manageBooking.resultList.hotel')).toBeInTheDocument();
    expect(getByText('ccui.manageBooking.resultList.date')).toBeInTheDocument();
    expect(getByText('ccui.manageBooking.resultList.status')).toBeInTheDocument();
    expect(getByText('ccui.manageBooking.resultList.sourcePms')).toBeInTheDocument();
    expect(queryByText('ccui.manageBooking.resultList.price')).not.toBeInTheDocument();
  });

  it('renders redesign header titles when isBookingHistoryRedesignCCUIEnabled is true', () => {
    const { getByText, queryByText } = render(
      <ResultList {...mockProps} isBookingHistoryRedesignCCUIEnabled={true} />
    );
    expect(getByText('ccui.manageBooking.resultList.hotel')).toBeInTheDocument();
    expect(getByText('ccui.manageBooking.resultList.date')).toBeInTheDocument();
    expect(getByText('ccui.manageBooking.resultList.bookedFor')).toBeInTheDocument();
    expect(getByText('ccui.manageBooking.resultList.price')).toBeInTheDocument();
    expect(getByText('ccui.manageBooking.resultList.bookedBy')).toBeInTheDocument();
    expect(getByText('ccui.manageBooking.resultList.status')).toBeInTheDocument();
    expect(queryByText('ccui.manageBooking.resultList.sourcePms')).not.toBeInTheDocument();
  });
});
