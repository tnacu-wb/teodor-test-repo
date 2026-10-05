import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { useFeatureToggle } from '@whitbread-eos/utils';
import React from 'react';
import { useForm } from 'react-hook-form';

import { fireEvent, render } from '../../utils/test-utils';
import BBGuestDetailsRoom from './BBGuestDetailsRoom';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(),
}));
const mockHandleResetField = jest.fn();
const reservationByIdList = [
  {
    additionalGuestInfo: {
      purposeOfStay: '',
    },
    reservationId: '2381005',
    roomStay: {
      adultsNumber: 2,
      childrenNumber: 0,
      arrivalDate: '2024-10-10',
      departureDate: '2024-10-11',
      ratePlanCode: 'BUSIFLEX',
      rateExtraInfo: {
        rateName: 'Business Flex',
      },
      roomExtraInfo: {
        roomType: 'DOUBLE',
        roomName: 'Double room',
      },
      accessibleRoom: {
        isAccessible: false,
        phoneNumber: '0333 321 1262',
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
];

const singleRoomProps: any = {
  numberOfRooms: 1,
  labels: {},
  roomNumber: 0,
  testid: 'testing-id',
  t: jest.fn(),
  queryClient: jest.fn(),
  guestList: {
    bbGuestDetails: [
      {
        emailAddress: '',
        firstName: '',
        id: '',
        lastName: '',
        title: '',
        composedName: '',
      },
    ],
  },
  setGuestUser: jest.fn(),
  index: 0,
  reset: jest.fn(),
  isDynamicSearchVisible: false,
};

const multipleRoomsProps: any = {
  numberOfRooms: 2,
  labels: {},
  roomNumber: 0,
  testid: 'testing-id',
  t: jest.fn(),
  queryClient: jest.fn(),
  guestList: {
    bbGuestDetails: [
      {
        emailAddress: '',
        firstName: '',
        id: '',
        lastName: '',
        title: '',
        composedName: '',
      },
      {
        emailAddress: '',
        firstName: '',
        id: '',
        lastName: '',
        title: '',
        composedName: '',
      },
    ],
  },
  setGuestUser: jest.fn(),
  index: 0,
  reset: jest.fn(),
  isDynamicSearchVisible: false,
};

const ComponentWithSingleRoom = (singleRoomProps) => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'bbGuestDetails',
    dropdownOptions: [{ id: 'Mr', label: 'Mr' }],
    label: '',
    props: {},
  };

  return <BBGuestDetailsRoom {...{ ...singleRoomProps, control, formField: fieldType, errors }} />;
};

const ComponentWithMultipleRooms = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'bbGuestDetails',
    dropdownOptions: [{ id: 'Mr', label: 'Mr' }],
    label: '',
    props: {},
  };

  return (
    <QueryClientProvider client={new QueryClient()}>
      <BBGuestDetailsRoom {...{ ...multipleRoomsProps, control, formField: fieldType, errors }} />
    </QueryClientProvider>
  );
};

describe('Guest Details BB', () => {
  it('should render the  Guest Details BB with single room', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_bb_accompanying_guest_details: false,
    });
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <ComponentWithSingleRoom {...singleRoomProps} />
      </QueryClientProvider>
    );
    expect(getByTestId('testing-id-Room-1')).toBeInTheDocument();
  });
  it('should render the  Guest Details BB with multiple rooms', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_bb_accompanying_guest_details: false,
    });
    const { getByTestId } = render(<ComponentWithMultipleRooms />);
    expect(getByTestId('testing-id-Room-1')).toBeInTheDocument();
  });

  it('should render the  Guest Details BB and should switch to dynamic mode', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_bb_accompanying_guest_details: false,
    });
    const { getByTestId, findByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <ComponentWithSingleRoom {...singleRoomProps} />
      </QueryClientProvider>
    );
    const button = getByTestId('testing-id-SwitchToDynamic');
    fireEvent.click(button);
    expect(await findByTestId('testing-id-DynamicGuestLead-1')).toBeInTheDocument();
  });

  it('should render the  Guest Details BB and should switch to dynamic mode, resetting the field', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_bb_accompanying_guest_details: true,
    });
    const { getByTestId, findByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <ComponentWithSingleRoom
          {...{
            ...singleRoomProps,
            handleResetField: mockHandleResetField,
            reservationByIdList,
            componentName: 'bbGuestDetails',
          }}
        />
      </QueryClientProvider>
    );
    const button = getByTestId('testing-id-SwitchToDynamic');
    fireEvent.click(button);
    expect(await findByTestId('testing-id-DynamicGuestLead-1')).toBeInTheDocument();
    expect(mockHandleResetField).toHaveBeenCalled();
  });

  it('should render the  Guest Details BB and display lead guest details label', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_bb_accompanying_guest_details: true,
    });
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <ComponentWithMultipleRooms
          {...{
            ...multipleRoomsProps,
            handleResetField: mockHandleResetField,
            reservationByIdList,
            componentName: 'bbGuestDetails',
          }}
        />
      </QueryClientProvider>
    );

    expect(getByTestId('testing-id-LeadGuestDetails')).toBeInTheDocument();
  });
});
