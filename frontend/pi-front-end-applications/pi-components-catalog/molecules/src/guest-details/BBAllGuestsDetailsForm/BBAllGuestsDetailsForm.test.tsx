import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { BUSINESS_BOOKER_USER_ROLES } from '@whitbread-eos/api';
import { FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { formatDataTestId, useCookieForABTesting } from '@whitbread-eos/utils';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render } from '../../utils/test-utils';
import BBAllGuestsDetailsForm from './BBAllGuestsDetailsForm';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCookieForABTesting: jest.fn(),
}));

const singleProps = {
  mockAccessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
  mockSefBookerDetails: '',
  isAccompanyingGuestDetailsEnabled: false,
};
const baseDataTestId = 'GuestDetailsBB';

const mockHandleSetValue = jest.fn();
const mockHandleResetField = jest.fn();
const mockHandleGetValues = jest.fn();

let numberOfRooms: number | undefined = 1;
const ComponentWithSingleRoom = (singleProps: any) => {
  const {
    formState: { errors },
  } = useForm();

  const fieldType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'bbGuestDetails',
    dropdownOptions: [{ id: 'Mr', label: 'Mr' }],
    label: '',
    testid: baseDataTestId,
    props: {
      defaultValues: {
        bbGestDetails: [
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
      guestList: {
        bbGuestDetails: [
          {
            composedName: '',
            emailAddress: '',
            firstName: '',
            id: '',
            lastName: '',
            title: '',
          },
        ],
      },
      isDynamicSearchVisible: true,
      numberOfRooms: numberOfRooms,
      labels: {},
      queryClient: jest.fn(),
      setGuestUser: jest.fn(),
      isAccompanyingGuestDetailsEnabled: singleProps.isAccompanyingGuestDetailsEnabled,
      accessLevel: singleProps.mockAccessLevel,
      selfBookerDetails: singleProps.mockSefBookerDetails,
      reservationByIdList: [
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
      ],
      baseDataTestIdAccompayningGuestDetails: 'AccompanyingGuestDetailsBBContainer',
    },
  };

  const props: any = {
    reset: jest.fn(),
    control: {
      _formValues: {
        bbGuestDetails: [
          {
            composedName: '',
            emailAddress: '',
            firstName: '',
            id: '',
            lastName: '',
            title: '',
          },
        ],
      },
    },
    formField: fieldType,
    errors,
    handleSetValue: mockHandleSetValue,
    handleResetField: mockHandleResetField,
    getValues: mockHandleGetValues,
  };

  return (
    <QueryClientProvider client={new QueryClient()}>
      <BBAllGuestsDetailsForm {...props} />
    </QueryClientProvider>
  );
};

describe('Guest Details BB', () => {
  beforeEach(() => {
    (useCookieForABTesting as jest.Mock).mockReturnValue(true);
  });

  it('should render the  Guest Details BB with single room', () => {
    const { getByTestId } = render(<ComponentWithSingleRoom {...singleProps} />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
  });

  it('should render the  Guest Details BB with number of rooms undefined', () => {
    numberOfRooms = undefined;
    const { getByTestId } = render(<ComponentWithSingleRoom {...singleProps} />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
  });

  it('should render the  Guest Details BB with single room and accompanying guest details ', () => {
    const { getByTestId } = render(
      <ComponentWithSingleRoom
        {...{
          ...singleProps,
          isAccompanyingGuestDetailsEnabled: true,
        }}
      />
    );
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
    expect(
      getByTestId('AccompanyingGuestDetailsBBContainer-AccompanyingGDTitle')
    ).toBeInTheDocument();
    expect(
      getByTestId('AccompanyingGuestDetailsBBContainer-AccompanyingGDDescription')
    ).toBeInTheDocument();
  });

  it('should render the  Guest Details BB with single room and self booker user ', () => {
    const { getByTestId } = render(
      <ComponentWithSingleRoom
        {...{
          ...singleProps,
          mockAccessLevel: BUSINESS_BOOKER_USER_ROLES.SELF,
          mockSefBookerDetails: 'Mr Elly Smith',
          isAccompanyingGuestDetailsEnabled: true,
        }}
      />
    );
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
    expect(getByTestId('GuestDetailsBBContainer-SelfBookerDetails')).toBeInTheDocument();
  });
});
