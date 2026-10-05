import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { Area, Channel } from '@whitbread-eos/api';
import { useFeatureToggle } from '@whitbread-eos/utils';

import { act, fireEvent, render, waitFor } from '../../utils/test-utils';
import {
  mockedHotelAvailabilityParams,
  mockedHotelAvailabilityResponse,
  mockedLeadGuestDetailsLabels,
  mockedLeadGuestValidationLabels,
  mockedNotificationLabels,
  mockedRoomAvailabilityLabels,
  mockedRoomRules,
  mockedRoomsAndGuestsLabels,
} from '../utilities/mockResponse';
import RoomModal from './RoomModal.component';
import RoomModalContainer, { Props } from './RoomModal.container';

const mockUseFeatureToggle = useFeatureToggle as jest.Mock;

jest.mock('@tanstack/react-query', () => {
  const original: typeof ReactQuery = jest.requireActual('@tanstack/react-query');
  return {
    ...original,
    fetchQuery: async (queryKey: string, queryFn: () => void) => {
      const key = queryKey[0];
      queryFn();
      if (key === 'hotelAvailabilityBB') {
        return Promise.resolve(mockedHotelAvailabilityResponse);
      }
      if (key === 'hotelAvailability') {
        return Promise.resolve(mockedHotelAvailabilityResponse);
      }
    },
  };
});
const mockUseUserDetails = {};

const mockedListOfEmployees: any[] = [];
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getListOfEmployees: () => Promise.resolve(mockedListOfEmployees),
  useUserDetails: () => mockUseUserDetails,
  graphQLRequest: () => Promise.resolve(mockedHotelAvailabilityResponse),
  useGetDiscountRateComapnyId: jest.fn().mockReturnValue('8986523'),
  useFeatureToggle: jest.fn(() => ({
    release_pi_promo_code_landing_page: true,
    release_pi_promo_code_site_wide: true,
    release_promotions_in_hotelavailability: false,
  })),
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
  isAvailabilityCheckBtn: false,
  onCloseModal: jest.fn(),
  onChange: jest.fn(),
  onAddNewRoom: jest.fn(),
  onAvailabilityCheck: jest.fn(),
  onUpdateRoom: jest.fn(),
  getFormState: jest.fn(),
  title: 'Add a room',
  roomRules: mockedRoomRules,
  hotelAvailabilityParams: mockedHotelAvailabilityParams,
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
    price: '£200.00',
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
  onAdultsChanged: false,
  roomDropdownRoomCodes: {
    accessible: 'DIS',
    double: 'DB',
    family: 'FAM',
    single: 'SB',
    twin: 'TWIN',
  },
  isAvailabilityButtonEnabled: false,
  isLocationRequired: false,
  setIsLocationRequired: jest.fn(),
  isAdultsDecreased: false,
  setIsAdultsDecreased: jest.fn(),
  onChildrenChanged: jest.fn(),
  setOnChildrenChanged: jest.fn(),
  isChildrenIncreased: false,
  setIsChildrenIncreased: jest.fn(),
  hotelCountry: 'United Kingdom (the)',
  language: 'en',
  isBbGuestEdited: false,
  setIsBbGuestEdited: jest.fn(),
  channel: Channel.Pi,
  brand: 'pi',
  isPromoCodeLandingPageEnabled: false,
  reservationId: '',
};

const roomModalContainerProps: Props = {
  isOpen: true,
  onClose: jest.fn(),
  onSaveNewRoom: jest.fn(),
  onUpdateRoom: jest.fn(),
  title: 'Add a room',
  roomRules: mockedRoomRules,
  hotelAvailabilityParams: mockedHotelAvailabilityParams,
  labels: {
    roomDropdownLabels: mockedRoomsAndGuestsLabels.roomModalLabels.roomDropdownLabels,
    roomDropdownRoomCodes: mockedRoomsAndGuestsLabels.roomModalLabels.roomDropdownRoomCodes,
    leadGuestLabels: mockedLeadGuestDetailsLabels,
    roomAvailabilityLabels: mockedRoomAvailabilityLabels,
    leadGuestValidationLabels: mockedLeadGuestValidationLabels,
    notificationLabels: mockedNotificationLabels,
  },
  baseDataTestId: 'amend',
  isEdit: false,
  roomDetailsEdit: {
    children: 0,
    adults: 1,
    roomType: 'Double',
    operaRoomType: 'Double',
    roomTypeCode: '',
  },
  price: '£200.00',
  reservationGuestList: {
    givenName: 'GuestOne',
    surName: 'Test',
    nameTitle: 'Mrs',
    email: 'test@test.com',
    address: {
      addressLine1: 'Address Line 1',
      addressLine2: 'Address Line 2',
      addressLine3: 'Address Line 3',
      cityName: 'London',
      countryCode: 'GB',
      postalCode: 'TE1 1TE',
    },
  },
  reservationId: '',
  roomNumber: 1,
  variant: Area.PI,
  brand: 'pi',
  hotelCountry: 'United Kingdom (the)',
  language: 'en',
  channel: Channel.Pi,
  isPromoCodeLandingPageEnabled: false,
};

const RoomModalContainerBB = () => {
  return <RoomModalContainer {...roomModalContainerProps} variant={Area.BB} isEdit />;
};

jest.setTimeout(15000);

describe('Room Modal Container', () => {
  it('should change value on RoomModal Guests dropdown', async () => {
    const { getByTestId } = render(
      <RoomModal
        {...roomModalProps}
        isHotelAvailable={{ available: true, displayNotification: true }}
      />
    );

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

  it('should change value on RoomModalContainer adults dropdown', () => {
    const { getByTestId } = render(<RoomModalContainer {...roomModalContainerProps} />);
    const twoAdultsOption = getByTestId('DropdownComp-add-room-modal-adults-number-1');
    const accessibleOption = getByTestId('DropdownComp-add-room-modal-room-type-2');

    act(() => {
      fireEvent.click(twoAdultsOption);
    });

    act(() => {
      fireEvent.click(accessibleOption);
    });

    expect(accessibleOption.textContent).toBe('Accessible');
    expect(roomModalProps.onChange).toHaveBeenCalledWith({ id: 2, label: '2 adults' }, 'adults');
  });

  it('should change value on RoomModalContainer children dropdown', () => {
    const { getByTestId } = render(<RoomModalContainer {...roomModalContainerProps} />);
    const childrenOption = getByTestId('DropdownComp-add-room-modal-children-number-2');

    act(() => {
      fireEvent.click(childrenOption);
    });

    expect(roomModalProps.onChange).toHaveBeenCalledWith(
      { id: 2, label: '2 children' },
      'children'
    );
  });

  it('should click Cancel button and call onClose function', () => {
    const { getByTestId } = render(<RoomModalContainer {...roomModalContainerProps} />);
    const cancelBtn = getByTestId('add-room-close-button');

    act(() => {
      fireEvent.click(cancelBtn);
    });

    expect(roomModalContainerProps.onClose).toHaveBeenCalled();
  });

  it('should maintain the initial price even it has been changed to other roomType', async () => {
    const { getByTestId, getByRole, getByText } = render(
      <RoomModalContainer {...roomModalContainerProps} isEdit={true} reservationId="2195782" />
    );

    const childrenOption = getByTestId('DropdownComp-add-room-modal-children-number-2');
    const roomPrice = getByTestId('add-room-modal-price');

    act(() => {
      fireEvent.click(childrenOption);
    });

    expect(roomModalProps.onChange).toHaveBeenCalledWith(
      { id: 2, label: '2 children' },
      'children'
    );
    const checkAvailabilityButton = getByRole('button', { name: 'Check availability' });
    fireEvent.click(checkAvailabilityButton);

    await waitFor(() => {
      expect(getByText('Room available')).toBeInTheDocument();
    });
    expect(roomPrice).toHaveTextContent('£200.00');
  });

  it('should click update button after input changes on Edit Room modal', () => {
    roomModalContainerProps.roomDetailsEdit!.adults = 0;
    roomModalContainerProps.roomDetailsEdit!.roomType = 'Accessible';
    const { getByTestId } = render(
      <RoomModalContainer {...roomModalContainerProps} isEdit={true} reservationId="2195782" />
    );
    const mrOption = getByTestId('DropdownComp-amend-Form-titleDropdown-0');
    const updateBtn = getByTestId('add-edit-room-button');

    act(() => {
      fireEvent.click(mrOption);
    });

    expect(mrOption.textContent).toBe('Mr');

    act(() => {
      fireEvent.click(updateBtn);
    });

    waitFor(() => {
      expect(updateBtn).not.toBeDisabled();
    });
  });

  it('should keep add room button disabled if the availability call was not done', () => {
    const { getByTestId } = render(
      <RoomModalContainer {...roomModalContainerProps} reservationId="2195782" />
    );
    const mrOption = getByTestId('DropdownComp-amend-Form-titleDropdown-0');
    const firstNameInput = getByTestId('input-firstName');
    const lastNameInput = getByTestId('input-lastName');
    const updateBtn = getByTestId('add-edit-room-button');

    act(() => {
      fireEvent.click(mrOption);
    });

    act(() => {
      fireEvent.change(firstNameInput, { target: { value: 'firstname test' } });
    });

    act(() => {
      fireEvent.change(lastNameInput, { target: { value: 'lastname test' } });
    });

    act(() => {
      fireEvent.click(updateBtn);
    });

    expect(updateBtn).toBeDisabled();
  });

  it('should display the availability notification if the hotel is available', async () => {
    const { getByTestId, getByRole, getByText } = render(
      <RoomModalContainer {...roomModalContainerProps} />
    );

    const singleRoomOption = getByTestId('DropdownComp-add-room-modal-room-type-0');

    fireEvent.click(singleRoomOption);
    await waitFor(() => {
      expect(roomModalProps.onChange).toHaveBeenCalledWith(
        { id: 'single', label: 'Single' },
        'roomType'
      );
      expect(singleRoomOption.textContent).toBe('Single');
    });

    const checkAvailabilityButton = getByRole('button', { name: 'Check availability' });
    expect(checkAvailabilityButton).toBeInTheDocument();
    fireEvent.click(checkAvailabilityButton);

    await waitFor(() => {
      expect(getByText('Room available')).toBeInTheDocument();
    });
  });

  it('should enable the Update button if the hotel is available and the lead guest details form has been filled', async () => {
    const { getByTestId, getByRole, getByText } = render(
      <RoomModalContainer {...roomModalContainerProps} reservationId="2195782" />
    );

    const singleRoomOption = getByTestId('DropdownComp-add-room-modal-room-type-0');

    fireEvent.click(singleRoomOption);

    await waitFor(() => {
      expect(roomModalProps.onChange).toHaveBeenCalledWith(
        { id: 'single', label: 'Single' },
        'roomType'
      );

      expect(singleRoomOption.textContent).toBe('Single');
    });

    const checkAvailabilityButton = getByRole('button', { name: 'Check availability' });
    expect(checkAvailabilityButton).toBeInTheDocument();
    fireEvent.click(checkAvailabilityButton);

    await waitFor(() => {
      expect(getByText('Room available')).toBeInTheDocument();
    });

    const mrOption = getByTestId('DropdownComp-amend-Form-titleDropdown-0');
    const firstNameInput = getByTestId('input-firstName');
    const lastNameInput = getByTestId('input-lastName');
    const updateBtn = getByTestId('add-edit-room-button');

    fireEvent.click(mrOption);

    fireEvent.change(firstNameInput, { target: { value: 'firstname test' } });

    fireEvent.change(lastNameInput, { target: { value: 'lastname test' } });
    await waitFor(() => {
      expect(updateBtn).toBeEnabled();
    });
    fireEvent.click(updateBtn);
  });
});

describe('Room Modal Container BB version', () => {
  it('should render the RoomModal container BB', () => {
    const { getByRole } = render(<RoomModalContainerBB />);
    expect(getByRole('dialog')).toBeInTheDocument();
  });

  it('should display the availability notification if the hotel is available after performing hotelAvailabilityBB request', async () => {
    const { getByRole, getByText, getByTestId } = render(
      <RoomModalContainer {...roomModalContainerProps} variant={Area.BB} />
    );

    const singleRoomOption = getByTestId('DropdownComp-add-room-modal-room-type-0');

    fireEvent.click(singleRoomOption);

    await waitFor(() => {
      expect(roomModalProps.onChange).toHaveBeenCalledWith(
        { id: 'single', label: 'Single' },
        'roomType'
      );
      expect(singleRoomOption.textContent).toBe('Single');
    });

    const checkAvailabilityButton = getByRole('button', { name: 'Check availability' });
    expect(checkAvailabilityButton).toBeInTheDocument();

    fireEvent.click(checkAvailabilityButton);

    await waitFor(() => {
      expect(getByText('Room available')).toBeInTheDocument();
    });
  });
});

jest.setTimeout(15000);

describe('Guests', () => {
  beforeEach(() => jest.clearAllMocks());

  it('should change value on RoomModalContainer adults dropdown', () => {
    const { getByTestId } = render(
      <RoomModalContainer
        {...roomModalContainerProps}
        roomDetailsEdit={{
          children: 0,
          adults: 2,
          roomType: 'Accessible',
          operaRoomType: 'pi',
          roomTypeCode: '',
        }}
      />
    );
    const oneAdultOption = getByTestId('DropdownComp-add-room-modal-adults-number-0');
    const adultsDropdown = getByTestId('DropdownComp-add-room-modal-adults-number-menuButton');

    fireEvent.click(oneAdultOption);
    expect(adultsDropdown).toHaveAccessibleName('1 adult');
  });

  it('should be able to select Accessible roomType if there are no children and at least 1 adult', async () => {
    const { getByTestId } = render(<RoomModalContainer {...roomModalContainerProps} />);

    const roomTypeDropdown = getByTestId('DropdownComp-add-room-modal-room-type-menuButton');

    const noChildren = getByTestId('DropdownComp-add-room-modal-children-number-0');
    const childrenDropdown = getByTestId('DropdownComp-add-room-modal-children-number-menuButton');
    const oneChild = getByTestId('DropdownComp-add-room-modal-children-number-1');

    fireEvent.click(oneChild);
    expect(childrenDropdown).toHaveAccessibleName('1 child');
    expect(roomTypeDropdown).toHaveAccessibleName('Family');

    await waitFor(() => {
      fireEvent.click(noChildren);
      expect(childrenDropdown).toHaveAccessibleName('0 children');
      expect(roomTypeDropdown).toHaveAccessibleName('Double');
    });
    fireEvent.click(roomTypeDropdown);
    fireEvent.blur(roomTypeDropdown);

    const accessibleRoomType = getByTestId('DropdownComp-add-room-modal-room-type-2');
    const doubleRoomType = getByTestId('DropdownComp-add-room-modal-room-type-1');

    fireEvent.click(accessibleRoomType);
    expect(roomTypeDropdown).toHaveAccessibleName('Accessible');

    fireEvent.click(doubleRoomType);
    await waitFor(() => {
      expect(roomTypeDropdown).toHaveAccessibleName('Double');
    });

    fireEvent.click(oneChild);
    expect(childrenDropdown).toHaveAccessibleName('1 child');
    expect(roomTypeDropdown).toHaveAccessibleName('Family');
  });

  it('should change the roomType to Family if there are selected children and turn back to Double if the children has been cleared', async () => {
    const { getByTestId } = render(<RoomModalContainer {...roomModalContainerProps} />);

    const childrenDropdown = getByTestId('DropdownComp-add-room-modal-children-number-menuButton');
    const oneChild = getByTestId('DropdownComp-add-room-modal-children-number-1');

    const roomTypeDropdown = getByTestId('DropdownComp-add-room-modal-room-type-menuButton');

    fireEvent.click(childrenDropdown);

    await waitFor(() => {
      fireEvent.click(oneChild);
      expect(roomTypeDropdown).toHaveAccessibleName('Family');
    });
  });

  it('should switch the roomType to Double if the selected children have been cleared', async () => {
    const { getByTestId } = render(
      <RoomModalContainer
        {...roomModalContainerProps}
        roomDetailsEdit={{
          children: 0,
          adults: 1,
          roomType: 'Double',
          operaRoomType: 'pi',
          roomTypeCode: '',
        }}
      />
    );

    const childrenDropdown = getByTestId('DropdownComp-add-room-modal-children-number-menuButton');
    const roomTypeDropdown = getByTestId('DropdownComp-add-room-modal-room-type-menuButton');

    const noChildren = getByTestId('DropdownComp-add-room-modal-children-number-0');

    fireEvent.click(childrenDropdown);

    await waitFor(() => {
      fireEvent.click(noChildren);
      expect(roomTypeDropdown).toHaveAccessibleName('Double');
    });
  });
  it('should set promo data from hotel availability response when promotions in hotel availability feature is enabled', async () => {
    mockUseFeatureToggle.mockReturnValue({
      release_promotions_in_hotelavailability: true,
    });

    const responseWithPromo = {
      hotelAvailability: {
        ...mockedHotelAvailabilityResponse.hotelAvailability,
        promotionsInformation: {
          showPromo: true,
          promoBookingInfo: {
            promotionCode: 'SAVE10',
          },
        },
      },
    };

    const fetchQuerySpy = jest
      .spyOn(ReactQuery.QueryClient.prototype, 'fetchQuery')
      .mockResolvedValue(responseWithPromo as any);

    const { getByRole, getByTestId } = render(<RoomModalContainer {...roomModalContainerProps} />);

    fireEvent.click(getByTestId('DropdownComp-add-room-modal-room-type-0'));

    const checkAvailabilityButton = getByRole('button', {
      name: 'Check availability',
    });

    fireEvent.click(checkAvailabilityButton);

    await waitFor(() => {
      expect(fetchQuerySpy).toHaveBeenCalled();
    });

    fetchQuerySpy.mockRestore();
  });
});
