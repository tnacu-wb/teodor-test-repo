import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { BUSINESS_BOOKER_USER_ROLES } from '@whitbread-eos/api';

import { render } from '../../utils/test-utils';
import GuestDetailsBBContainer from './GuestDetailsBBContainer';

const props = {
  labels: {},
  validationLabels: {},
  onSubmit: jest.fn(),
  numberOfRooms: 2,
  isAccompanyingGuestDetailsEnabled: false,
  accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
  selfBookerDetails: '',
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
};

describe('<GuestDetailsBBContainer />', () => {
  it('should render the GuestDetailsBBContainer with given props', () => {
    const { getByTestId } = render(
      <GuestDetailsBBContainer
        guestList={undefined}
        setGuestUser={undefined}
        getFormState={undefined}
        queryClient={new QueryClient()}
        isDynamicSearchVisible={false}
        {...props}
      />
    );
    expect(getByTestId('GuestDetailsBBContainer-Form')).toBeInTheDocument();
    expect(getByTestId('GuestDetailsBBContainer-Form-Container')).toBeInTheDocument();
    expect(getByTestId('GuestDetailsBBContainer-Form-Room-1')).toBeInTheDocument();
    expect(getByTestId('GuestDetailsBBContainer-Form-Room-2')).toBeInTheDocument();
  });
});
