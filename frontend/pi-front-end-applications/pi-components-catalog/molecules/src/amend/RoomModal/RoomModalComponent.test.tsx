import '@testing-library/jest-dom';
import { Area } from '@whitbread-eos/api';

import { act, fireEvent, render, userEvent, waitFor } from '../../utils/test-utils';
import { getPromotionsInformation } from '../utilities';
import {
  mockedHotelAvailabilityResponse,
  mockedLeadGuestDetailsLabels,
  mockedLeadGuestValidationLabels,
  mockedNotificationLabels,
  mockedRoomAvailabilityLabels,
  mockedRoomRules,
} from '../utilities/mockResponse';
import RoomModal from './RoomModal.component';

jest.mock('../utilities', () => ({
  ...jest.requireActual('../utilities'),
  getPromotionsInformation: jest.fn(),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  PromotionsNotification: () => <div data-testid="promotions-notification" />,
}));

const mockGetPromotionsInformation = getPromotionsInformation as jest.Mock;

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: () => ({
    cancelQueries: jest.fn(),
  }),
}));

const listSuggestions = [
  {
    employee: {
      title: 'Ms',
      lastName: 'test',
      id: '2',
      firstName: 'Ella',
      emailAddress: 'booker.cgl@mailinator.com',
      composedName: 'Ms Ella  test (booker.cgl@mailinator.com)',
    },
    prettyFormatDisplay: 'Ms Ella  <strong>test</strong> (booker.cgl@mailinator.com)',
  },
];

const roomModalProps = {
  isOpen: true,
  isHotelAvailable: {
    displayNotification: false,
    available: true,
  },
  isAvailabilityButtonEnabled: false,
  onCloseModal: jest.fn(),
  onChange: jest.fn(),
  onAddNewRoom: jest.fn(),
  onAvailabilityCheck: jest.fn(),
  onUpdateRoom: jest.fn(),
  getFormState: jest.fn(),
  setIsBbGuestEdited: jest.fn(),
  title: 'Add a room',
  roomRules: mockedRoomRules,
  roomDetails: {
    children: 0,
    adults: 1,
    roomType: 'Double',
    operaRoomType: '',
    roomTypeCode: 'DB',
  },
  roomTypeOptions: [
    { id: 'single', label: 'Single' },
    { id: 'double', label: 'Double' },
    { id: 'accesible', label: 'Accesible' },
    { id: 'twin', label: 'Twin' },
    { id: 'family', label: 'Family' },
  ],
  labels: {
    leadGuestLabels: mockedLeadGuestDetailsLabels,
    roomAvailabilityLabels: mockedRoomAvailabilityLabels,
    leadGuestValidationLabels: mockedLeadGuestValidationLabels,
    notificationLabels: mockedNotificationLabels,
  },
  baseDataTestId: 'amend',
  roomOccupancyDetails: {
    displayRoomOccupancy: false,
    price: '',
    guests: '',
  },
  leadGuestDetails: {
    title: 'Mr',
    firstName: 'test',
    lastName: 'testLast',
    emailAddress: 'test@gmail.com',
  },
  initialLeadGuestDetails: {
    title: '',
    firstName: '',
    lastName: '',
    emailAddress: '',
  },
  isEdit: false,
  setGuestDetails: jest.fn(),
  isUpdateBtnDisabled: false,
  variant: Area.PI,
  bbEmployeeList: listSuggestions,
  isAdultsDecreased: false,
  roomDropdownRoomCodes: {},
  hotelCountry: 'United Kingdom (the)',
  isLocationRequired: false,
  setIsLocationRequired: jest.fn(),
  language: 'en',
  isBbGuestEdited: false,
  brand: 'testBrand',
  channel: 'testChannel' as any,
  hotelAvailabilityParams: {
    arrival: '2024-01-01',
    bookingChannel: {
      channel: 'testChannel',
      language: 'en',
      subchannel: 'testSubchannel',
    },
    country: 'United Kingdom',
    departure: '2024-01-02',
    hotelCode: 'TEST123',
    language: 'en',
    numberOfAdults: 1,
    numberOfChildren: 0,
    hotelId: 'TEST123',
    rooms: [],
    rateCode: '',
  },
  isPromoCodeLandingPageEnabled: false,
};

const mockUseUserDetails = {};
const mockedListOfEmployees: any[] = [];
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getListOfEmployees: () => Promise.resolve(mockedListOfEmployees),
  useUserDetails: () => mockUseUserDetails,
  graphQLRequest: () => {
    return {
      hotelAvailability: mockedHotelAvailabilityResponse,
    };
  },
}));

describe('Room Modal Component', () => {
  afterEach(() => jest.clearAllMocks());

  it('should display the warning message with "Your meal selection has been reset" if the Edit room Modal is displayed, the adults number has changed and the hotel is available', async () => {
    const { getByText } = render(
      <RoomModal
        {...roomModalProps}
        isHotelAvailable={{
          displayNotification: true,
          available: true,
        }}
        isEdit
        isAdultsDecreased
      />
    );

    expect(getByText(mockedNotificationLabels.title)).toBeInTheDocument();
  });

  it('should display the error message if the hotel is not available', () => {
    const { getAllByText } = render(
      <RoomModal
        {...roomModalProps}
        isEdit
        isHotelAvailable={{
          displayNotification: true,
          available: false,
        }}
      />
    );
    expect(
      getAllByText(mockedRoomAvailabilityLabels.roomsUnavailableDescription)[0]
    ).toBeInTheDocument();
  });

  it('should disable the "Add a room" button if the hotel is not available', async () => {
    const { getByTestId } = render(
      <RoomModal
        {...roomModalProps}
        variant={Area.BB}
        isAdultsDecreased
        isAvailabilityButtonEnabled={true}
        isHotelAvailable={{
          displayNotification: false,
          available: false,
        }}
      />
    );

    const addARoomButton = getByTestId('add-edit-room-button');

    expect(addARoomButton).toBeDisabled();
  });
});

describe('Guests', () => {
  afterEach(() => jest.clearAllMocks());

  it('should change value on RoomModal Guests dropdown', () => {
    const { getByTestId } = render(<RoomModal {...roomModalProps} />);

    const singleRoomOption = getByTestId('DropdownComp-add-room-modal-room-type-0');
    const doubleRoomOption = getByTestId('DropdownComp-add-room-modal-room-type-1');
    const familyRoomOption = getByTestId('DropdownComp-add-room-modal-room-type-4');
    const twoAdultsOption = getByTestId('DropdownComp-add-room-modal-adults-number-1');
    const twoChildrenOption = getByTestId('DropdownComp-add-room-modal-children-number-2');

    act(() => {
      fireEvent.click(singleRoomOption);
    });
    expect(roomModalProps.onChange).toHaveBeenCalledWith(
      { id: 'single', label: 'Single' },
      'roomType'
    );
    expect(singleRoomOption.textContent).toBe('Single');

    act(() => {
      fireEvent.click(twoAdultsOption);
    });
    expect(roomModalProps.onChange).toHaveBeenCalledWith({ id: 2, label: '2 adults' }, 'adults');
    expect(doubleRoomOption.textContent).toBe('Double');
    expect(twoAdultsOption.textContent).toBe('2 adults');

    act(() => {
      fireEvent.click(twoChildrenOption);
    });
    expect(familyRoomOption.textContent).toBe('Family');
    expect(twoChildrenOption.textContent).toBe('2 children');
  });

  it('should change the roomType to Family if there are selected children', async () => {
    const { getAllByTestId, getAllByText } = render(<RoomModal {...roomModalProps} />);

    await waitFor(() => {
      const childrenDropdown = getAllByTestId(
        'DropdownComp-add-room-modal-children-number-menuButton'
      );
      fireEvent.click(childrenDropdown[0]);

      const familyLabel = getAllByText(/family/i);
      expect(familyLabel[0]).toBeInTheDocument();
    });
  });
});

describe('Lead Guest Details', () => {
  beforeEach(() => jest.clearAllMocks());

  it('should call the onUpdateRoom if the isEdit prop is enabled', async () => {
    jest.useFakeTimers();
    mockedListOfEmployees.push(listSuggestions[0].employee);

    const { getByRole, getByTestId } = render(
      <RoomModal
        {...roomModalProps}
        isHotelAvailable={{
          displayNotification: true,
          available: true,
        }}
        isAvailabilityButtonEnabled={true}
        isEdit={true}
        variant={Area.BB}
        isUpdateBtnDisabled={false}
      />
    );

    const dynamicSearchInput = getByRole('textbox');
    fireEvent.click(dynamicSearchInput);
    fireEvent.change(dynamicSearchInput, { target: { value: 'test' } });

    await act(async () => {
      jest.advanceTimersByTime(500);
    });

    const dropdownItem = await waitFor(() =>
      getByTestId('GuestDetailsBBContainer-Form-DropDownItem-0')
    );
    fireEvent.click(dropdownItem);

    const addARoomButton = getByTestId('add-edit-room-button');
    fireEvent.click(addARoomButton);

    await waitFor(() => {
      expect(roomModalProps.onUpdateRoom).toHaveBeenCalledWith(
        expect.objectContaining({
          firstName: listSuggestions[0].employee.firstName,
          lastName: listSuggestions[0].employee.lastName,
        })
      );
    });

    mockedListOfEmployees.length = 0;
    jest.useRealTimers();
  });

  it('should match the LeadGuestDetails manual form with DynamicSearch if the user is in editing mode', async () => {
    const { getAllByRole, getByTestId } = render(
      <RoomModal
        {...roomModalProps}
        variant={Area.BB}
        isAdultsDecreased
        isUpdateBtnDisabled={false}
        isAvailabilityButtonEnabled={true}
        isHotelAvailable={{
          displayNotification: true,
          available: true,
        }}
        isEdit={true}
        leadGuestDetails={{
          title: 'Ms',
          firstName: listSuggestions[0].employee.firstName,
          lastName: listSuggestions[0].employee.lastName,
          emailAddress: 'firstName@test.com',
        }}
      />
    );

    const switchToManualBtn = getByTestId(/switch/i);
    expect(switchToManualBtn).toBeInTheDocument();
    fireEvent.click(switchToManualBtn);
    const firstNameInput = getAllByRole('textbox')[0];
    const lastNameInput = getAllByRole('textbox')[1];

    userEvent.clear(firstNameInput);
    userEvent.clear(lastNameInput);
    userEvent.type(firstNameInput, listSuggestions[0].employee.firstName);
    userEvent.type(lastNameInput, listSuggestions[0].employee.lastName);

    expect(firstNameInput).toHaveValue(listSuggestions[0].employee.firstName);
    expect(lastNameInput).toHaveValue(listSuggestions[0].employee.lastName);
  });

  it('should change the LeadGuestDetails if an input has been changed', async () => {
    const { getAllByRole, getByTestId } = render(
      <RoomModal {...roomModalProps} variant={Area.BB} isEdit />
    );

    const switchToManualBtn = getByTestId(/switch/i);
    expect(switchToManualBtn).toBeInTheDocument();
    fireEvent.click(switchToManualBtn);
    const firstNameInput = getAllByRole('textbox')[0];
    const lastNameInput = getAllByRole('textbox')[1];
    userEvent.clear(firstNameInput);
    userEvent.clear(lastNameInput);
    userEvent.type(firstNameInput, 'testnewFirstName');
    userEvent.type(lastNameInput, 'testLast');

    expect(firstNameInput).toHaveValue('testnewFirstName');
    expect(lastNameInput).toHaveValue('testLast');
  });

  it('should call the onAddNewRoom function with selected DynamicSearchLeadGuest values', async () => {
    const { getByRole, getByTestId } = render(
      <RoomModal {...roomModalProps} variant={Area.BB} isUpdateBtnDisabled={false} />
    );

    const dynamicSearchInput = getByRole('textbox');
    fireEvent.click(dynamicSearchInput);

    const dropdownItem = await waitFor(() =>
      getByTestId('GuestDetailsBBContainer-Form-DropDownItem-0')
    );
    fireEvent.click(dropdownItem);

    const addARoomButton = getByTestId('add-edit-room-button');
    fireEvent.click(addARoomButton);

    await waitFor(() => {
      expect(roomModalProps.onAddNewRoom).toHaveBeenCalledWith(listSuggestions[0].employee);
    });
  });

  it('should display the cityTaxNotIncluded message on German hotels bookings', () => {
    const { getByTestId } = render(
      <RoomModal
        {...roomModalProps}
        roomOccupancyDetails={{
          displayRoomOccupancy: true,
          price: '2500',
          guests: '2',
        }}
        hotelCountry="Germany"
      />
    );

    expect(getByTestId('add-room-modal-cityTaxNotIncluded')).toBeInTheDocument();
  });

  it('should fetch promotions information before availability check when feature flag is disabled', async () => {
    const setPromoData = jest.fn();
    const onAvailabilityCheck = jest.fn();

    mockGetPromotionsInformation.mockResolvedValue({
      showPromo: true,
    });

    const { getByTestId } = render(
      <RoomModal
        {...roomModalProps}
        isAvailabilityButtonEnabled={true}
        setPromoData={setPromoData}
        promoData={null}
        isPromotionsInHotelAvailabilityEnabled={false}
        onAvailabilityCheck={onAvailabilityCheck}
      />
    );

    await userEvent.click(getByTestId('add-room-modal-availability-button'));

    await waitFor(() => {
      expect(mockGetPromotionsInformation).toHaveBeenCalled();
      expect(setPromoData).toHaveBeenCalled();
      expect(onAvailabilityCheck).toHaveBeenCalled();
    });
  });

  it('should handle promotions information errors', async () => {
    const consoleSpy = jest.spyOn(console, 'log').mockImplementation(() => {});

    mockGetPromotionsInformation.mockRejectedValueOnce(new Error('promo failed'));

    const { getByTestId } = render(
      <RoomModal
        {...roomModalProps}
        isAvailabilityButtonEnabled={true}
        promoData={null}
        setPromoData={jest.fn()}
        isPromotionsInHotelAvailabilityEnabled={false}
      />
    );

    await userEvent.click(getByTestId('add-room-modal-availability-button'));

    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith('e');
    });

    consoleSpy.mockRestore();
  });

  it('should skip promotions request when feature flag is enabled', async () => {
    const onAvailabilityCheck = jest.fn();

    const { getByTestId } = render(
      <RoomModal
        {...roomModalProps}
        isAvailabilityButtonEnabled={true}
        promoData={null}
        setPromoData={jest.fn()}
        isPromotionsInHotelAvailabilityEnabled={true}
        onAvailabilityCheck={onAvailabilityCheck}
      />
    );

    await userEvent.click(getByTestId('add-room-modal-availability-button'));

    await waitFor(() => {
      expect(mockGetPromotionsInformation).not.toHaveBeenCalled();
      expect(onAvailabilityCheck).toHaveBeenCalled();
    });
  });

  it('should display room available notification', () => {
    const { getByText } = render(
      <RoomModal
        {...roomModalProps}
        isHotelAvailable={{
          displayNotification: true,
          available: true,
        }}
      />
    );

    expect(getByText(mockedRoomAvailabilityLabels.roomAvailable)).toBeInTheDocument();
  });

  it('should display promotion notification when promo data exists', () => {
    const { getByTestId } = render(
      <RoomModal
        {...roomModalProps}
        promoData={
          {
            showPromo: true,
          } as any
        }
        setPromoData={jest.fn()}
      />
    );

    expect(getByTestId('promotions-notification')).toBeInTheDocument();
  });

  it('should call onCloseModal when cancel button is clicked', async () => {
    const onCloseModal = jest.fn();

    const { getByTestId } = render(
      <RoomModal {...roomModalProps} variant={Area.BB} onCloseModal={onCloseModal} />
    );

    await userEvent.click(getByTestId('add-room-close-button'));

    expect(onCloseModal).toHaveBeenCalled();
  });
  it('should disable update button for PI when isUpdateBtnDisabled is true', () => {
    const { getByTestId } = render(
      <RoomModal {...roomModalProps} variant={Area.PI} isUpdateBtnDisabled />
    );

    expect(getByTestId('add-edit-room-button')).toBeDisabled();
  });

  it('should call availability check', async () => {
    const onAvailabilityCheck = jest.fn().mockResolvedValue(undefined);

    const { getByTestId } = render(
      <RoomModal
        {...roomModalProps}
        isAvailabilityButtonEnabled={true}
        onAvailabilityCheck={onAvailabilityCheck}
        promoData={null}
        setPromoData={jest.fn()}
        isPromotionsInHotelAvailabilityEnabled={true}
      />
    );

    const button = getByTestId('add-room-modal-availability-button');

    expect(button).not.toBeDisabled();

    await userEvent.click(button);

    await waitFor(() => {
      expect(onAvailabilityCheck).toHaveBeenCalledTimes(1);
    });
  });
});
