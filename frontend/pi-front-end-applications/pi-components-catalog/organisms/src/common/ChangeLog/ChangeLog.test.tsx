import '@testing-library/jest-dom';
import React from 'react';

import { act, render, userEvent, waitFor } from '../../utils/test-utils';
import ChangeLog from './ChangeLog.component';

const mockQueryRequestBCA = jest.fn();
const mockQueryRequestChangeLog = jest.fn();
const mockSetShowChangeLogModal = jest.fn();
const mockApplyButtonLabel = 'tableFilter.apply';
const mockBookingReference = 'ABC123456';

const mockChangeLogEntries = [
  {
    date: '12.12.2023',
    time: '16:17',
    actionDescription: 'GUEST DETAILD UPDATED',
    actionType: 'UPDATE RESERVATION',
    user: 'WHBOC001_AWS_MS_2',
  },
  {
    date: '12/12/23',
    time: '16:13',
    actionDescription: 'RESORT = FRAMTI CONFIRMATION NO = 8626139',
    actionType: 'NEW RESERVATION',
    user: 'WHBOC001_AWS_MS_1',
  },
];

const mockChangeLogEntries2 = [
  {
    date: '06.12.23',
    time: '11:17',
    actionDescription: 'CANCEL RESERVATION',
    actionType: 'CANCEL',
    user: 'WHBOC001_AWS_MS_3',
  },
  {
    date: '05.12.23',
    time: '17:17',
    actionDescription: 'PRODUCT BREAKFAST ADDED',
    actionType: null,
    user: 'WHBOC001_AWS_MS_2',
  },
  {
    date: '05.12.23',
    time: '16:17',
    actionDescription: 'GUEST DETAILD UPDATED',
    actionType: 'UPDATE RESERVATION',
    user: 'WHBOC001_AWS_MS_2',
  },
  {
    date: '04.12.23',
    time: '16:13',
    actionDescription: 'RESORT = FRAMTI CONFIRMATION NO = 8626139',
    actionType: 'NEW RESERVATION',
    user: 'WHBOC001_AWS_MS_1',
  },
];

const mockBookingConfirmationAuthenticatedResponse = {
  bookingConfirmationAuthenticated: {
    reservationByIdList: [
      {
        reservationId: '12345',
        billing: {
          address: {
            addressLine1: 'Dental Surgery',
            postalCode: 'GU16 7HF',
          },
          title: 'Mr',
          telephone: '+440123123123',
          firstName: 'Catalin',
          lastName: 'Iosif',
          email: 'mailto:catalin.iosif@mailinator.com',
        },
        reservationGuestList: [
          {
            givenName: 'Catalin',
            surName: 'Iosif',
          },
        ],
        gdsReferenceNumber: null,
        roomStay: {
          checkInTime: '15:00',
          checkOutTime: '12:00',
          ratePlanCode: 'FLEXRATE',
          arrivalDate: '2023-06-15',
          departureDate: '2023-06-17',
          bookingChannel: 'PI.com',
          roomPrice: 1998,
          cot: false,
          adultsNumber: 1,
          roomExtraInfo: {
            roomName: 'Premier Plus Room',
          },
          childrenNumber: 0,
        },
        paymentCard: {
          cardNumberMasked: 'XXXXXXXXXXXX0017',
        },
        reservationOverrideReasons: {
          reasonCode: 'DTH',
          callerName: 'test',
          managerName: '',
          reasonName: 'Death',
        },
        reservationOverridden: true,
        guaranteeCode: 'CC',
        reservationStatus: 'Reserved',
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
      },
    ],
    balanceOutstanding: 1998,
    currencyCode: 'GBP',
    newTotal: 1998,
    policyCode: 'D1A',
    previousTotal: 0,
    totalCost: 1998,
    hotelId: 'MANOLD',
    hotelName: 'Manchester Old Trafford',
    bookingFlowId: 'booking-a1',
    rateMessage: '<p>Amend or cancel up to 1pm on arrival day</p>\n',
  },
};

const mockBookingConfirmationAuthenticatedResponse2 = {
  bookingConfirmationAuthenticated: {
    reservationByIdList: [
      {
        reservationId: '12345',
        billing: {
          address: {
            addressLine1: 'Dental Surgery',
            postalCode: 'GU16 7HF',
          },
          title: 'Mr',
          telephone: '+440123123123',
          firstName: 'Catalin',
          lastName: 'Iosif',
          email: 'mailto:catalin.iosif@mailinator.com',
        },
        reservationGuestList: [
          {
            givenName: 'Catalin',
            surName: 'Iosif',
          },
        ],
        gdsReferenceNumber: null,
        roomStay: {
          checkInTime: '15:00',
          checkOutTime: '12:00',
          ratePlanCode: 'FLEXRATE',
          arrivalDate: '2023-06-15',
          departureDate: '2023-06-17',
          bookingChannel: 'PI.com',
          roomPrice: 1998,
          cot: false,
          adultsNumber: 1,
          roomExtraInfo: {
            roomName: 'Premier Plus Room',
          },
          childrenNumber: 0,
        },
        paymentCard: {
          cardNumberMasked: 'XXXXXXXXXXXX0017',
        },
        reservationOverrideReasons: {
          reasonCode: 'DTH',
          callerName: 'test',
          managerName: '',
          reasonName: 'Death',
        },
        reservationOverridden: true,
        guaranteeCode: 'CC',
        reservationStatus: 'Reserved',
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
      },
      {
        reservationId: '67891',
        billing: {
          address: {
            addressLine1: 'Dental Surgery',
            postalCode: 'GU16 7HF',
          },
          title: 'Mr',
          telephone: '+440123123123',
          firstName: 'Catalin',
          lastName: 'Iosif',
          email: 'mailto:catalin.iosif@mailinator.com',
        },
        reservationGuestList: [
          {
            givenName: 'Catalin',
            surName: 'Iosif',
          },
        ],
        gdsReferenceNumber: null,
        roomStay: {
          checkInTime: '15:00',
          checkOutTime: '12:00',
          ratePlanCode: 'FLEXRATE',
          arrivalDate: '2023-06-15',
          departureDate: '2023-06-17',
          bookingChannel: 'PI.com',
          roomPrice: 1998,
          cot: false,
          adultsNumber: 1,
          roomExtraInfo: {
            roomName: 'Premier Plus Room',
          },
          childrenNumber: 0,
        },
        paymentCard: {
          cardNumberMasked: 'XXXXXXXXXXXX0017',
        },
        reservationOverrideReasons: {
          reasonCode: 'DTH',
          callerName: 'test',
          managerName: '',
          reasonName: 'Death',
        },
        reservationOverridden: true,
        guaranteeCode: 'CC',
        reservationStatus: 'Reserved',
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
      },
    ],
    balanceOutstanding: 1998,
    currencyCode: 'GBP',
    newTotal: 1998,
    policyCode: 'D1A',
    previousTotal: 0,
    totalCost: 1998,
    hotelId: 'LONEUS',
    hotelName: 'London Eusotn',
    bookingFlowId: 'booking-a1',
    rateMessage: '<p>Amend or cancel up to 1pm on arrival day</p>\n',
  },
};

const mockQueryRequestResponseBCA = {
  isError: false,
  isLoading: false,
  data: mockBookingConfirmationAuthenticatedResponse,
};

const mockQueryRequestResponseBCA2 = {
  isError: false,
  isLoading: false,
  data: mockBookingConfirmationAuthenticatedResponse2,
};

const mockQueryRequestResponseChangeLog = {
  retrieveChangesLog: {
    activityLog: {
      activityLog: mockChangeLogEntries,
      totalPages: 1,
      offset: 20,
      limit: 20,
      hasMore: false,
      totalResults: 2,
    },
  },
};

const mockQueryRequestResponseChangeLog2 = {
  retrieveChangesLog: {
    activityLog: {
      activityLog: mockChangeLogEntries2,
      totalPages: 1,
      offset: 20,
      limit: 20,
      hasMore: true,
      totalResults: 4,
    },
  },
};
const mockQueryRequestResponseChangeLog3 = {
  retrieveChangesLog: {
    activityLog: {
      activityLog: [],
      totalPages: 1,
      offset: 20,
      limit: 20,
      hasMore: false,
      totalResults: 4,
    },
  },
};

function mockUseQueryRequest(queryKey) {
  const key = queryKey[0];

  if (typeof key === 'string') {
    if (key === 'getBookingConfirmationAuthenticated') {
      return mockQueryRequestBCA();
    }
    return {};
  }
}

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getAuthCookie: jest.fn().mockImplementation(() => 'test-jwt'),
  useQueryRequest: mockUseQueryRequest,
  graphQLRequest: () => {
    return mockQueryRequestResponseChangeLog;
  },
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: () => ({
    fetchQuery: mockQueryRequestChangeLog,
  }),
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockProps = {
  showChangeLogModal: true,
  setShowChangeLogModal: mockSetShowChangeLogModal,
  bookingReference: mockBookingReference,
};

describe('ChangeLog', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockQueryRequestBCA.mockReturnValue(mockQueryRequestResponseBCA);
    mockQueryRequestChangeLog.mockReturnValue(mockQueryRequestResponseChangeLog);
  });

  it('should display the change log modal on initial render', () => {
    const { getByText, getByRole } = render(<ChangeLog {...mockProps} />);

    expect(getByRole('dialog')).toBeInTheDocument();
    expect(getByText('ccui.changeLogModal.title')).toBeInTheDocument();
  });

  it('should display all the required table columns in the table', async () => {
    const { getByText } = render(<ChangeLog {...mockProps} />);

    await waitFor(() => {
      expect(getByText('ccui.changeLogModal.date')).toBeInTheDocument();
      expect(getByText('ccui.changeLogModal.time')).toBeInTheDocument();
      expect(getByText('ccui.changeLogModal.actionType')).toBeInTheDocument();
      expect(getByText('ccui.changeLogModal.actionDescription')).toBeInTheDocument();
      expect(getByText('ccui.changeLogModal.user')).toBeInTheDocument();
    });
  });

  it('should display all the change log entries as rows', async () => {
    const { getByRole, getAllByRole } = render(<ChangeLog {...mockProps} />);
    await waitFor(() => {
      mockChangeLogEntries.forEach((log) => {
        expect(getByRole('cell', { name: log.time })).toBeInTheDocument();
        expect(getByRole('cell', { name: log.actionType })).toBeInTheDocument();
        expect(getByRole('cell', { name: log.actionDescription })).toBeInTheDocument();
        expect(getByRole('cell', { name: log.user })).toBeInTheDocument();
      });
      expect(getAllByRole('cell', { name: '12.12.23' })).toHaveLength(2);
    });
  });

  it('should display a loading spinner when change log retrieval query is running', () => {
    mockQueryRequestChangeLog.mockReturnValue({
      ...mockQueryRequestResponseChangeLog,
      isLoading: true,
    });

    const { queryByRole, queryAllByRole, getByTestId } = render(<ChangeLog {...mockProps} />);

    const loadingSpinner = getByTestId('loading-spinner');
    expect(loadingSpinner).toBeInTheDocument();
    expect(loadingSpinner).toHaveTextContent('ccui.changeLogModal.loading');
    expect(queryByRole('table')).not.toBeInTheDocument();
    expect(queryAllByRole('row')).toHaveLength(0);
  });

  it('should display a loading spinner when booking confirmation query is running', () => {
    mockQueryRequestBCA.mockReturnValue({
      ...mockQueryRequestResponseBCA,
      isLoading: true,
    });

    const { queryByRole, queryAllByRole, getByTestId } = render(<ChangeLog {...mockProps} />);

    const loadingSpinner = getByTestId('loading-spinner');
    expect(loadingSpinner).toBeInTheDocument();
    expect(loadingSpinner).toHaveTextContent('ccui.changeLogModal.loading');
    expect(queryByRole('table')).not.toBeInTheDocument();
    expect(queryAllByRole('row')).toHaveLength(0);
  });

  it('should display a message when query returns no change log entries', async () => {
    mockQueryRequestChangeLog.mockReturnValue({ ...mockQueryRequestResponseChangeLog3 });

    const { queryByRole, queryAllByRole, getByText } = render(<ChangeLog {...mockProps} />);
    await waitFor(() => {
      expect(getByText('ccui.changeLogModal.title')).toBeInTheDocument();
      expect(queryByRole('table')).not.toBeInTheDocument();
      expect(queryAllByRole('row')).toHaveLength(0);
    });
  });

  it('should display an error message when nooking confirmation query fails', () => {
    mockQueryRequestBCA.mockReturnValue({
      ...mockQueryRequestResponseBCA,
      isError: true,
    });

    const { queryByRole, queryAllByRole, getByText } = render(<ChangeLog {...mockProps} />);

    expect(getByText('ccui.changeLogModal.errorHeading')).toBeInTheDocument();
    expect(getByText('ccui.changeLogModal.errorDescription')).toBeInTheDocument();
    expect(queryByRole('table')).not.toBeInTheDocument();
    expect(queryAllByRole('row')).toHaveLength(0);
  });

  it('should update the state when clicking on close button', () => {
    const { getByTestId } = render(<ChangeLog {...mockProps} />);

    act(() => {
      const closeIcon = getByTestId('ChangeLog-ModalCloseIcon');
      userEvent.click(closeIcon);
    });

    expect(mockSetShowChangeLogModal).toHaveBeenCalledTimes(1);
    expect(mockSetShowChangeLogModal).toHaveBeenCalledWith(false);
  });

  it('should dispaly a chevron (filter trigger) for expanding filter popover for filterable columns', async () => {
    const { getByTestId, queryByTestId } = render(<ChangeLog {...mockProps} />);
    await waitFor(() => {
      expect(getByTestId('FilterExxpand-actionType')).toBeInTheDocument();
      expect(getByTestId('FilterExxpand-user')).toBeInTheDocument();
      expect(queryByTestId('FilterExxpand-date')).not.toBeInTheDocument();
      expect(queryByTestId('FilterExxpand-time')).not.toBeInTheDocument();
      expect(queryByTestId('FilterExxpand-actionDescription')).not.toBeInTheDocument();
    });
  });

  it.each([
    ['actionType', ['UPDATE RESERVATION', 'NEW RESERVATION']],
    ['user', ['WHBOC001_AWS_MS_1', 'WHBOC001_AWS_MS_2']],
  ])(`should display the correct list of filters [%s]`, async (filterKey, filterOptions) => {
    const { getByTestId, getByRole } = render(<ChangeLog {...mockProps} />);

    await waitFor(() => {
      const filterTrigger = getByTestId(`FilterExxpand-${filterKey}`);
      userEvent.click(filterTrigger);
    });

    await waitFor(() => {
      filterOptions.forEach((filter) => {
        expect(getByRole('checkbox', { name: filter })).toBeInTheDocument();
      });
    });
  });

  it('should display the filtered rows after applying filters on a column', async () => {
    mockQueryRequestChangeLog.mockReturnValue(mockQueryRequestResponseChangeLog2);

    const { getByTestId, getByRole, getAllByRole, queryByText } = render(
      <ChangeLog {...mockProps} />
    );
    await waitFor(() => {
      expect(getAllByRole('row')).toHaveLength(5);
    });
    const filterTrigger = getByTestId(`FilterExxpand-actionType`);
    userEvent.click(filterTrigger);

    await waitFor(() =>
      expect(getByRole('checkbox', { name: 'UPDATE RESERVATION' })).toBeInTheDocument()
    );
    const checkbox1 = getByRole('checkbox', { name: 'UPDATE RESERVATION' });
    userEvent.click(checkbox1);
    const apply = getByRole('button', { name: mockApplyButtonLabel });
    userEvent.click(apply);

    await waitFor(() => {
      expect(getAllByRole('row')).toHaveLength(2);
      expect(queryByText('RESORT = FRAMTI CONFIRMATION NO = 8626139')).not.toBeInTheDocument();
      expect(queryByText('CANCEL RESERVATION')).not.toBeInTheDocument();
      expect(queryByText('PRODUCT BREAKFAST ADDED')).not.toBeInTheDocument();
    });
  });

  it('should display a notification when there are no results after applying filters', async () => {
    const { getByTestId, getByRole, getAllByRole, queryByText, getByText } = render(
      <ChangeLog {...mockProps} />
    );
    await waitFor(() => {
      expect(getAllByRole('row')).toHaveLength(3);
    });
    const filterTrigger1 = getByTestId(`FilterExxpand-actionType`);
    userEvent.click(filterTrigger1);

    await waitFor(() =>
      expect(getByRole('checkbox', { name: 'UPDATE RESERVATION' })).toBeInTheDocument()
    );
    const checkbox1 = getByRole('checkbox', { name: 'UPDATE RESERVATION' });
    userEvent.click(checkbox1);
    const apply1 = getByRole('button', { name: mockApplyButtonLabel });
    userEvent.click(apply1);

    const filterTrigger2 = getByTestId(`FilterExxpand-user`);
    userEvent.click(filterTrigger2);

    await waitFor(() =>
      expect(getByRole('checkbox', { name: 'WHBOC001_AWS_MS_1' })).toBeInTheDocument()
    );
    const checkbox2 = getByRole('checkbox', { name: 'WHBOC001_AWS_MS_1' });
    userEvent.click(checkbox2);
    const apply2 = getAllByRole('button', { name: mockApplyButtonLabel })[1];
    userEvent.click(apply2);

    await waitFor(() => {
      expect(getAllByRole('row')).toHaveLength(1);
      expect(queryByText('RESORT = FRAMTI CONFIRMATION NO = 8626139')).not.toBeInTheDocument();
      expect(queryByText('GUEST DETAILD UPDATED')).not.toBeInTheDocument();
      expect(getByRole('status')).toBeInTheDocument();
      expect(getByText('ccui.changeLogModal.noResults')).toBeInTheDocument();
    });
  });

  it('should not display room selection dropdown for a single room reservation', () => {
    const { queryByTestId } = render(<ChangeLog {...mockProps} />);

    expect(
      queryByTestId('DropdownComp-ChangeLog-MultiRoomDropdown-menuButton')
    ).not.toBeInTheDocument();
  });

  it('should display room selection dropdown for multi room reservation', () => {
    mockQueryRequestBCA.mockReturnValue(mockQueryRequestResponseBCA2);

    const { getByTestId } = render(<ChangeLog {...mockProps} />);

    expect(getByTestId('DropdownComp-ChangeLog-MultiRoomDropdown-menuButton')).toBeInTheDocument();
  });

  it('should display has more button', async () => {
    mockQueryRequestChangeLog.mockReturnValue(mockQueryRequestResponseChangeLog2);
    const { getByText } = render(<ChangeLog {...mockProps} />);

    waitFor(() => {
      expect(getByText('ccui.changeLogModal.loadMore')).toBeInTheDocument();
    });
  });
});
