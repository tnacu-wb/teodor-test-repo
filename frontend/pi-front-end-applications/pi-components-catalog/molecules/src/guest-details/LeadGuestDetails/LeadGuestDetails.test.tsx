import LeadGuestDetails from '.';
import '@testing-library/jest-dom';
import { ROOM_TYPE } from '@whitbread-eos/api';
import { FORM_FIELD_TYPES, FieldsType } from '@whitbread-eos/atoms';
import { formatDataTestId, getLocalStorageMock } from '@whitbread-eos/utils';
import { useForm } from 'react-hook-form';

import { render, userEvent, fireEvent, waitFor } from '../../utils/test-utils';

const mockUseFeatureSwitch = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureSwitch: () => mockUseFeatureSwitch(),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      locale: 'en',
    },
    query: {
      reservationId: 'basket123',
    },
  }),
}));

const baseDataTestId = 'GuestDetails-leadGuest';
const mockedSingleRoomBkndData = {
  isUserLoggedIn: false,
  rooms: [
    {
      additionalGuestInfo: {
        purposeOfStay: '',
      },
      reservationId: '3720923',
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2023-12-13',
        departureDate: '2023-12-14',
        ratePlanCode: 'FLEXRATE',
        rateExtraInfo: {
          rateName: 'Flex Rate',
        },
        roomExtraInfo: {
          roomType: 'BIGWIN',
          roomName: 'Bigger room',
        },
        accessibleRoom: {
          isAccessible: false,
          phoneNumber: '0333 321 3104',
        },
      },
      reservationGuestList: [
        {
          givenName: '',
          surName: 'TEMPORARY',
          nameTitle: null,
        },
      ],
      billing: null,
    },
  ],
};

const mockedMultipleRoomBknData = {
  isUserLoggedIn: false,
  rooms: [
    {
      roomStay: {
        adultsNumber: 2,
        childrenNumber: 0,
        arrivalDate: '2022-09-28',
        departureDate: '2022-09-30',
        ratePlanCode: 'FLEXRATE',
        rateExtraInfo: {
          rateName: 'Flex',
        },
        roomExtraInfo: {
          roomType: ROOM_TYPE.DOUBLE,
          roomName: 'Double Room',
        },
        accessibleRoom: {
          isAccessible: false,
          phoneNumber: '0333 321 1315',
        },
      },
      reservationId: '111111',
    },
    {
      roomStay: {
        adultsNumber: 2,
        childrenNumber: 0,
        arrivalDate: '2022-09-28',
        departureDate: '2022-09-30',
        ratePlanCode: 'FLEXRATE',
        rateExtraInfo: {
          rateName: 'Flex',
        },
        roomExtraInfo: {
          roomType: ROOM_TYPE.DOUBLE,
          roomName: 'Double Room',
        },
        accessibleRoom: {
          isAccessible: false,
          phoneNumber: '0333 321 1315',
        },
      },
      reservationId: '111112',
    },
  ],
};

const mockedMultipleRoomBknDataAdultsChildren = {
  isUserLoggedIn: false,
  rooms: [
    {
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 2,
        arrivalDate: '2022-09-28',
        departureDate: '2022-09-30',
        ratePlanCode: 'FLEXRATE',
        rateExtraInfo: {
          rateName: 'Flex',
        },
        roomExtraInfo: {
          roomType: ROOM_TYPE.DOUBLE,
          roomName: 'Double Room',
        },
        accessibleRoom: {
          isAccessible: false,
          phoneNumber: '0333 321 1315',
        },
      },
      reservationId: '111111',
    },
    {
      roomStay: {
        adultsNumber: 2,
        childrenNumber: 0,
        arrivalDate: '2022-09-28',
        departureDate: '2022-09-30',
        ratePlanCode: 'FLEXRATE',
        rateExtraInfo: {
          rateName: 'Flex',
        },
        roomExtraInfo: {
          roomType: ROOM_TYPE.DOUBLE,
          roomName: 'Double Room',
        },
        accessibleRoom: {
          isAccessible: false,
          phoneNumber: '0333 321 1315',
        },
      },
      reservationId: '111112',
    },
  ],
};
const mockHandleSetValue = jest.fn();
const mockHandleResetField = jest.fn();
const mockHandleGetValues = jest.fn();

const ComponentWithSingleRoom = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'leadGuest',
    label: 'leadGuest',
    bkndData: mockedSingleRoomBkndData,
    testid: baseDataTestId,
    props: {
      showIcon: false,
      area: 'pi',
      hotelBrand: 'PID',
      currentLang: 'de',
      isLocationRequired: true,
      showCheckInInfo: {},
      setShowCheckInInfo: mockHandleSetValue,
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    errors,
    handleSetValue: mockHandleSetValue,
    handleResetField: mockHandleResetField,
    getValues: mockHandleGetValues,
  };

  return <LeadGuestDetails {...props} />;
};

const ComponentWithSingleRoomCCUI = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'leadGuest',
    label: 'leadGuest',
    bkndData: mockedSingleRoomBkndData,
    testid: baseDataTestId,
    props: {
      showIcon: false,
      area: 'ccui',
      hotelBrand: 'PID',
      currentLang: 'de',
      isLocationRequired: true,
      isMultiRoomRedesignEnabled: false,
      isSingleRoomRedesignEnabled: false,
      showCheckInInfo: {},
      setShowCheckInInfo: mockHandleSetValue,
      shouldAskForAccompanyingGuest: true,
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    errors,
    handleSetValue: mockHandleSetValue,
    handleResetField: mockHandleResetField,
    getValues: mockHandleGetValues,
  };

  return <LeadGuestDetails {...props} />;
};

const ComponentWithSingleRoomRedesignFS = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'leadGuest',
    label: 'leadGuest',
    bkndData: mockedSingleRoomBkndData,
    testid: baseDataTestId,
    props: {
      showIcon: false,
      area: 'pi',
      hotelBrand: 'PID',
      currentLang: 'de',
      isLocationRequired: true,
      isBookingForSomeoneElse: true,
      isGermanHotel: true,
      isMultiRoomRedesignEnabled: true,
      isSingleRoomRedesignEnabled: true,
      showCheckInInfo: {},
      setShowCheckInInfo: mockHandleSetValue,
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    errors,
    handleSetValue: mockHandleSetValue,
    handleResetField: mockHandleResetField,
    getValues: mockHandleGetValues,
  };

  return <LeadGuestDetails {...props} />;
};

const ComponentWithSingleRoomRedesignFSGBHotel = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'leadGuest',
    label: 'leadGuest',
    bkndData: mockedSingleRoomBkndData,
    testid: baseDataTestId,
    props: {
      showIcon: false,
      area: 'pi',
      hotelBrand: 'PI',
      currentLang: 'en',
      isLocationRequired: true,
      isBookingForSomeoneElse: true,
      isGermanHotel: false,
      isMultiRoomRedesignEnabled: true,
      isSingleRoomRedesignEnabled: true,
      showCheckInInfo: {},
      setShowCheckInInfo: mockHandleSetValue,
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    errors,
    handleSetValue: mockHandleSetValue,
    handleResetField: mockHandleResetField,
    getValues: mockHandleGetValues,
  };

  return <LeadGuestDetails {...props} />;
};

const ComponentWithSingleRoomRedesignFSGBHotelBookingMyself = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'leadGuest',
    label: 'leadGuest',
    bkndData: mockedSingleRoomBkndData,
    testid: baseDataTestId,
    props: {
      showIcon: false,
      area: 'pi',
      hotelBrand: 'PI',
      currentLang: 'en',
      isLocationRequired: true,
      isBookingForSomeoneElse: false,
      isGermanHotel: false,
      isMultiRoomRedesignEnabled: true,
      isSingleRoomRedesignEnabled: true,
      showCheckInInfo: {},
      setShowCheckInInfo: mockHandleSetValue,
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    errors,
    handleSetValue: mockHandleSetValue,
    handleResetField: mockHandleResetField,
    getValues: mockHandleGetValues,
  };

  return <LeadGuestDetails {...props} />;
};

const ComponentWithMultipleRoomsMultiAddressFS = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'leadGuest',
    label: 'leadGuest',
    bkndData: mockedMultipleRoomBknData,
    testid: baseDataTestId,
    props: {
      showIcon: false,
      area: 'pi',
      hotelBrand: 'PID',
      currentLang: 'en',
      isLocationRequired: true,
      isBookingForSomeoneElse: true,
      isGermanHotel: true,
      isMultiRoomRedesignEnabled: true,
      isSingleRoomRedesignEnabled: true,
      isAdditionalInformationEnabled: true,
      showCheckInInfo: { '111111': true },
      setShowCheckInInfo: mockHandleSetValue,
      shouldAskForAccompanyingGuest: true,
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    errors,
    handleSetValue: mockHandleSetValue,
    handleResetField: mockHandleResetField,
    getValues: mockHandleGetValues,
  };

  return <LeadGuestDetails {...props} />;
};

const ComponentWithMultipleRoomsMultiAddressFSDE = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'leadGuest',
    label: 'leadGuest',
    bkndData: mockedMultipleRoomBknData,
    testid: baseDataTestId,
    props: {
      showIcon: false,
      area: 'pi',
      hotelBrand: 'PID',
      currentLang: 'de',
      isLocationRequired: true,
      isBookingForSomeoneElse: true,
      isGermanHotel: true,
      isMultiRoomRedesignEnabled: true,
      isSingleRoomRedesignEnabled: true,
      isAdditionalInformationEnabled: true,
      showCheckInInfo: { '111111': true, '111112': true },
      setShowCheckInInfo: mockHandleSetValue,
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    errors,
    handleSetValue: mockHandleSetValue,
    handleResetField: mockHandleResetField,
    getValues: mockHandleGetValues,
  };

  return <LeadGuestDetails {...props} />;
};

const ComponentWithMultipleRooms = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'leadGuest',
    label: 'leadGuest',
    bkndData: mockedMultipleRoomBknData,
    testid: baseDataTestId,
    props: {
      showIcon: false,
      area: 'pi',
      hotelBrand: 'PID',
      currentLang: 'en',
      isLocationRequired: true,
      isAdditionalInformationEnabled: true,
      showCheckInInfo: { '111111': true },
      setShowCheckInInfo: mockHandleSetValue,
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    errors,
    handleSetValue: mockHandleSetValue,
    handleResetField: mockHandleResetField,
    getValues: mockHandleGetValues,
  };

  return <LeadGuestDetails {...props} />;
};

const ComponentWithMultipleRoomsAdultChildren = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'leadGuest',
    label: 'leadGuest',
    bkndData: mockedMultipleRoomBknDataAdultsChildren,
    testid: baseDataTestId,
    props: {
      showIcon: false,
      area: 'pi',
      hotelBrand: 'PID',
      currentLang: 'en',
      isLocationRequired: true,
      showCheckInInfo: { '111111': true },
      setShowCheckInInfo: mockHandleSetValue,
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    errors,
    handleSetValue: mockHandleSetValue,
    handleResetField: mockHandleResetField,
    getValues: mockHandleGetValues,
  };

  return <LeadGuestDetails {...props} />;
};

const ComponentWithMultipleRoomDE = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'leadGuest',
    label: 'leadGuest',
    bkndData: mockedMultipleRoomBknData,
    testid: baseDataTestId,
    props: {
      showIcon: false,
      area: 'pi',
      hotelBrand: 'PID',
      currentLang: 'de',
      isLocationRequired: true,
      showCheckInInfo: { '111111': true },
      setShowCheckInInfo: mockHandleSetValue,
    },
  };

  const props: any = {
    control,
    formField: fieldType,
    errors,
    handleSetValue: mockHandleSetValue,
    handleResetField: mockHandleResetField,
    getValues: mockHandleGetValues,
  };

  return <LeadGuestDetails {...props} />;
};

const localStorageMock = getLocalStorageMock();

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
});
describe('Lead Guest Details', () => {
  it('should render the  component with single room', () => {
    const { getByTestId } = render(<ComponentWithSingleRoom />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
  });

  it('should render the guest details for single room  when clicking the checkbox', async () => {
    const { getByRole, getByTestId } = render(<ComponentWithSingleRoom />);

    const checkbox = getByRole('checkbox');

    await userEvent.click(checkbox);
    expect(checkbox).toBeChecked();
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Header'))).toBeInTheDocument();
  });

  it('should render the guest details for multiple rooms ', async () => {
    const { getByTestId } = render(<ComponentWithMultipleRooms />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'ContainerRoom-1'))).toBeInTheDocument();
    expect(getByTestId(formatDataTestId(baseDataTestId, 'ContainerRoom-2'))).toBeInTheDocument();
  });
  it('should render the guest details for multiple rooms for adult and children', async () => {
    const { getAllByText } = render(<ComponentWithMultipleRoomsAdultChildren />);
    const adults = getAllByText((content) => content.includes('account.dashboard.adults'));
    const children = getAllByText((content) => content.includes('account.dashboard.children'));
    adults.forEach((element) => expect(element).toBeInTheDocument());
    children.forEach((element) => expect(element).toBeInTheDocument());
  });

  it('should render the guest details for multiple rooms and trigger address toggle ', async () => {
    const { getAllByText, getByTestId, getAllByTestId } = render(
      <ComponentWithMultipleRoomsMultiAddressFS />
    );

    const firstCheckinButton = getAllByTestId(formatDataTestId(baseDataTestId, 'CheckinButton'))[0];
    await userEvent.click(firstCheckinButton);

    const manualAddress = getAllByText('booking.enterManuallAddress')[0];
    await userEvent.click(manualAddress);

    const inputAddressLine1 = getByTestId('input-leadGuest[0].addressLine1');

    fireEvent.focus(inputAddressLine1);
    await userEvent.type(inputAddressLine1, 'Street 22 December');

    expect(inputAddressLine1).toBeInTheDocument();
  });

  it('should render the guest details for multiple rooms DE', async () => {
    const { getByTestId } = render(<ComponentWithMultipleRoomDE />);

    expect(getByTestId(formatDataTestId(baseDataTestId, 'ContainerRoom-1'))).toBeInTheDocument();
    expect(getByTestId(formatDataTestId(baseDataTestId, 'ContainerRoom-2'))).toBeInTheDocument();
  });

  it('should check the lead guest for room 1 ', async () => {
    const { getAllByRole } = render(<ComponentWithSingleRoom />);

    const checkbox = getAllByRole('checkbox')[0];
    await userEvent.click(checkbox);
    expect(checkbox).toBeChecked();
  });

  it('should render multiple check in buttons', () => {
    const { getAllByTestId } = render(<ComponentWithMultipleRoomsMultiAddressFSDE />);
    expect(getAllByTestId(formatDataTestId(baseDataTestId, 'CheckinButton'))).toHaveLength(2);
  });

  it('should render consent radio group component', () => {
    const { getAllByTestId } = render(<ComponentWithMultipleRoomsMultiAddressFSDE />);
    const firstCheckinButton = getAllByTestId(formatDataTestId(baseDataTestId, 'CheckinButton'))[0];
    userEvent.click(firstCheckinButton);
    expect(getAllByTestId(formatDataTestId(baseDataTestId, 'Consent_radio-group'))).toHaveLength(2);
  });

  it('should render dropdown title component', () => {
    const { getAllByTestId } = render(<ComponentWithMultipleRoomsMultiAddressFSDE />);
    expect(
      getAllByTestId('DropdownComp-GuestDetails-leadGuest-TitleDropdown-menuButton')
    ).toHaveLength(2);
  });
  describe('Check Silent Substitution Feature Flag states', () => {
    const SILENT_SUBSTITUTION_STORAGE_KEY = 'SilentSubstitutionRoomLabels';

    it('should check Silent Substitution Feature Flag true, two rooms', function () {
      localStorageMock.setItem(
        SILENT_SUBSTITUTION_STORAGE_KEY,
        JSON.stringify({
          basket123: {
            value: [
              {
                roomLabelCode: 'Double room',
                silentSubstitution: true,
              },
              {
                roomLabelCode: 'Double room',
                silentSubstitution: true,
              },
            ],
            expire: 123,
          },
          basket1234: {
            value: [
              {
                roomLabelCode: 'Double room',
                silentSubstitution: true,
              },
              {
                roomLabelCode: 'Double room',
                silentSubstitution: true,
              },
            ],
            expire: 1234,
          },
        })
      );

      mockUseFeatureSwitch.mockReturnValue(true);

      const { getAllByTestId } = render(<ComponentWithMultipleRooms />);

      expect(getAllByTestId('GuestDetails-leadGuest-DetailsRoom')[0]).toHaveTextContent(
        'Double room 2 account.dashboard.adults'
      );
    });

    it('should check Silent Substitution Feature Flag true, silentSubstitution treu and false, two rooms', function () {
      mockedMultipleRoomBknData.rooms[1].roomStay.roomExtraInfo.roomName = 'Bigger room';
      mockedMultipleRoomBknData.rooms[1].roomStay.roomExtraInfo.roomType = 'BIGWIN';

      localStorageMock.setItem(
        SILENT_SUBSTITUTION_STORAGE_KEY,
        JSON.stringify({
          basket123: {
            value: [
              {
                roomLabelCode: 'Double room',
                silentSubstitution: true,
              },
              {
                roomLabelCode: 'BIGWIN',
                silentSubstitution: false,
              },
            ],
            expire: 123,
          },
        })
      );

      mockUseFeatureSwitch.mockReturnValue(true);

      const { getAllByTestId } = render(<ComponentWithMultipleRooms />);

      expect(getAllByTestId('GuestDetails-leadGuest-DetailsRoom')[0]).toHaveTextContent(
        'Double room 2 account.dashboard.adults'
      );

      expect(getAllByTestId('GuestDetails-leadGuest-DetailsRoom')[1]).toHaveTextContent(
        'Bigger room 2 account.dashboard.adults'
      );
    });

    it('should check Silent Substitution Feature Flag true when nothing is stored inside the local storage', function () {
      localStorageMock.clear();

      mockedMultipleRoomBknData.rooms[0].roomStay.roomExtraInfo.roomName = 'Bigger room';
      mockedMultipleRoomBknData.rooms[0].roomStay.roomExtraInfo.roomType = 'BIGWIN';
      mockUseFeatureSwitch.mockReturnValue(true);

      const { getAllByTestId } = render(<ComponentWithMultipleRooms />);

      expect(getAllByTestId('GuestDetails-leadGuest-DetailsRoom')[0]).toHaveTextContent(
        'Bigger room 2 account.dashboard.adults'
      );
    });

    it('should check Silent Substitution Feature Flag false, two rooms', function () {
      mockedMultipleRoomBknData.rooms[0].roomStay.roomExtraInfo.roomName = 'Bigger room';
      mockedMultipleRoomBknData.rooms[0].roomStay.roomExtraInfo.roomType = 'BIGWIN';
      mockedMultipleRoomBknData.rooms[1].roomStay.roomExtraInfo.roomName = 'Bigger room';
      mockedMultipleRoomBknData.rooms[1].roomStay.roomExtraInfo.roomType = 'BIGWIN';
      mockUseFeatureSwitch.mockReturnValue(false);

      const { getAllByTestId } = render(<ComponentWithMultipleRooms />);

      expect(getAllByTestId('GuestDetails-leadGuest-DetailsRoom')[0]).toHaveTextContent(
        'Bigger room 2 account.dashboard.adults'
      );
    });
  });

  describe('Single booking with redesign changes under feature switch', () => {
    it('should render the  component with single room for De hotel', () => {
      const { getByTestId } = render(<ComponentWithSingleRoomRedesignFS />);
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
    });
    it('should render the  component with single room and not show the address toggle', () => {
      const { getByTestId, queryByText } = render(<ComponentWithSingleRoomRedesignFS />);
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();

      const manualAddress = queryByText('booking.enterManuallAddress');
      expect(manualAddress).not.toBeInTheDocument();
    });
    it('should render the  component with single room for GB hotel', () => {
      const { getByTestId } = render(<ComponentWithSingleRoomRedesignFSGBHotel />);
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
    });

    it('should render lead guest title, fname, sname for single room for GB hotel for booking someonelse', () => {
      const { queryByTestId } = render(<ComponentWithSingleRoomRedesignFSGBHotel />);

      const leadGuestTitle = queryByTestId(
        'DropdownComp-GuestDetails-leadGuest-TitleDropdown-menuButton'
      );
      const leadGuestFname = queryByTestId('input-leadGuest[0][firstName]-label');
      const leadGuestSname = queryByTestId('input-leadGuest[0][lastName]-label');

      expect(leadGuestTitle).toBeInTheDocument();
      expect(leadGuestFname).toBeInTheDocument();
      expect(leadGuestSname).toBeInTheDocument();
    });

    it('should NOT render lead guest title, fname, sname for single room for GB hotel for booking myself', () => {
      const { queryByTestId } = render(<ComponentWithSingleRoomRedesignFSGBHotelBookingMyself />);

      const leadGuestTitle = queryByTestId(
        'DropdownComp-GuestDetails-leadGuest-TitleDropdown-menuButton'
      );
      const leadGuestFname = queryByTestId('input-leadGuest[0][firstName]-label');
      const leadGuestSname = queryByTestId('input-leadGuest[0][lastName]-label');

      expect(leadGuestTitle).not.toBeInTheDocument();
      expect(leadGuestFname).not.toBeInTheDocument();
      expect(leadGuestSname).not.toBeInTheDocument();
    });
  });

  describe('Single booking CCUI without feature switch', () => {
    it('should render the  component with single room', () => {
      const { getByTestId } = render(<ComponentWithSingleRoomCCUI />);
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
    });
    it('should render the corret CCUI specific text', () => {
      const { getByText, getByRole } = render(<ComponentWithSingleRoomCCUI />);
      expect(getByText('ccui.booking.leadGuest.iAmBookingForSomeoneElse')).toBeInTheDocument();

      const checkbox = getByRole('checkbox', {
        name: /ccui.booking.leadGuest.iAmBookingForSomeoneElse/i,
      });
      expect(checkbox).not.toBeChecked();
      userEvent.click(checkbox);
      expect(checkbox).toBeChecked();
      expect(getByText('ccui.booking.leadGuest.labelWhoIsTheLead')).toBeInTheDocument();
    });
  });

  describe('Multiple addresss redesign changes under feature switch', () => {
    it('should render the  component with single room', () => {
      const { getByTestId } = render(<ComponentWithMultipleRoomsMultiAddressFS />);
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
    });
  });

  describe('Additional information section', () => {
    it('should render Additional information section when checkbox is checked', () => {
      const { getByTestId, queryAllByTestId } = render(
        <ComponentWithMultipleRoomsMultiAddressFSDE />
      );

      const checkbox = getByTestId('GuestDetails-leadGuest-RoomCheckboxText-1');

      fireEvent.click(checkbox);

      const button = queryAllByTestId('GuestDetails-leadGuest-CheckinButton');

      expect(button[0]).toBeInTheDocument();
    });

    it('should render Additional information section with address fields for German Hotels', async () => {
      const { getByTestId, queryAllByTestId } = render(
        <ComponentWithMultipleRoomsMultiAddressFSDE />
      );

      const button = queryAllByTestId('GuestDetails-leadGuest-CheckinButton');

      await expect(button[0]).toBeInTheDocument();

      userEvent.click(button[0]);

      await waitFor(() => {
        expect(getByTestId('input-leadGuest[0].addressLine1')).toBeInTheDocument();
        expect(getByTestId('input-leadGuest[0].addressLine2')).toBeInTheDocument();
        expect(getByTestId('input-leadGuest[0].addressLine3')).toBeInTheDocument();
        expect(getByTestId('input-leadGuest[0].addressLine4')).toBeInTheDocument();
        expect(getByTestId('input-leadGuest[0].postcodeAddress')).toBeInTheDocument();
      });
    });
  });

  describe('Accompanying guest section', () => {
    it('should not render Accompanying guest section', () => {
      const { queryByTestId } = render(<ComponentWithSingleRoomCCUI />);
      expect(
        queryByTestId(formatDataTestId(baseDataTestId, 'AccompanyingContainer'))
      ).not.toBeInTheDocument();
    });

    it('should render Accompanying guest section', () => {
      const { getAllByTestId } = render(<ComponentWithMultipleRoomsMultiAddressFS />);
      const sections = getAllByTestId(formatDataTestId(baseDataTestId, 'AccompanyingContainer'));
      expect(sections.length).toBeGreaterThanOrEqual(2);
    });
  });
});
