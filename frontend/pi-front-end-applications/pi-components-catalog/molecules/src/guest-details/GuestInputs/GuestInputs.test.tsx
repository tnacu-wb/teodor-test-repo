import GuestInputs from '.';
import '@testing-library/jest-dom';
import { FORM_FIELD_TYPES, FieldsType } from '@whitbread-eos/atoms';
import { formatDataTestId, getLocalStorageMock } from '@whitbread-eos/utils';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render } from '../../utils/test-utils';

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

const mockHandleSetValue = jest.fn();
const mockHandleGetValues = jest.fn();
mockHandleGetValues.mockReturnValue({
  accompanyingGuest: [
    {
      title: 'Mr',
      firstName: 'First',
      lastName: 'Last',
    },
  ],
  title: 'Mr',
  firstName: 'First',
  lastName: 'Last',
});

const ComponentWithSingleRoom = ({
  sectionName = 'accompanyingGuest',
}: {
  sectionName?: string;
}) => {
  const { control } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: sectionName,
    label: sectionName,
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
    errors: {},
    index: 0,
  };

  return <GuestInputs {...props} />;
};

const localStorageMock = getLocalStorageMock();

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
});
describe('Guest Inputs', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<ComponentWithSingleRoom />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'InputsContainer'))).toBeInTheDocument();
  });
});
