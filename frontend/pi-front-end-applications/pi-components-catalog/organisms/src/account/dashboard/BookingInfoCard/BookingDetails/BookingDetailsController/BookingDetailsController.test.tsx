import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { Area, BC_RESERVATION_STATUS, BOOKING_TYPE, SOURCE_SYSTEM } from '@whitbread-eos/api';
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';

import { fireEvent, render, waitFor } from '../../../../../utils/test-utils';
import type { Props } from './BookingDetailsController.component';
import BookingDetailsController from './BookingDetailsController.component';

const mockUseRouter = {
  route: '/',
  pathname: '',
  query: { reservationId: '12345' },
  asPath: '',
  push: jest.fn(),
};

jest.mock('next/router', () => ({
  useRouter() {
    return mockUseRouter;
  },
}));

const manageBookingData = {
  isCancellable: true,
  isAmendable: false,
  isRuleCompliant: true,
  aemLabelKey: 'Amends are not available within 24hours before stay date',
};

const props: Props = {
  rateType: '',
  bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
  refetchManageBooking: jest.fn(),
  setIdvData: jest.fn(),
  manageBookingData: manageBookingData,
  bookingReference: 'AQPR1437',
  basketReference: 'AQPR1437',
  operaConfNumber: null,
  idvData: {
    personalInformation: {
      bookerName: '',
      guestName: '',
      address: '',
      postcode: '',
      telephoneNumber: '',
      cardUsedToMakeBooking: '',
    },
    bookingInformation: {
      reservationNumber: { value: '', partOfSearch: false },
      hotelName: '',
      arrivalDate: '2022-03-28',
      departureDate: '2022-03-29',
      emailAddress: '',
    },
    dpaStatus: {
      dpaPassed: false,
      dpaOverride: false,
      eCnpPassword: '',
    },
  },
  dpaInfo: { dpaOverride: false, dpaPassed: false },
  setDpaInfo: jest.fn(),
  inputValues: { bookerLastName: 'Parker' },
  defaultDataFromBooking: [],
};

const mockUseQueryRequest = {
  isLoading: false,
  isError: false,
  error: '',
  data: {
    manageBooking: {
      isCancellable: true,
      isAmendable: true,
      isRuleCompliant: true,
      aemLabelKey: 'Amends are not available within 24hours before stay date',
    },
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useMutationRequest: () => ({
    mutation: jest.fn(),
  }),
  useQueryRequest: () => mockUseQueryRequest,
  getLoggedInUserInfo: () => ({
    accessLevel: 'SUPER',
    profile: {},
    sessionId: '',
  }),
  graphQLRequest: jest.fn().mockResolvedValue({
    findBooking: {
      cookieName: 'pi.single-booking',
      redirectBase: '/gb/en/account/dashboard',
      ref: 'AQPR1437',
      sourcePms: 'Opera',
      token: 'test-token',
      minutesTillExpiry: '30',
      basketReference: 'AQPR1437',
    },
  }),
  useCustomLocale: () => ({
    country: 'gb',
    language: 'en',
  }),
  getSecureTwoURL: () => 'localhost',
  useAuthToken: () => ({
    token: 'test-auth-token',
    isLoading: false,
    error: null,
  }),
}));

jest.mock('../../CancelBookingModal', () => ({
  ...jest.requireActual('../../CancelBookingModal'),
  CancelBookingModal: () => <div data-testid="CancelBookingModal-id" />,
  BookingHistoryCancelBookingModal: () => <div data-testid="BookingHistoryCancelBookingModal-id" />,
}));

jest.mock('@tanstack/react-query', () => {
  const original: typeof ReactQuery = jest.requireActual('@tanstack/react-query');
  return {
    ...original,
  };
});

describe('BookingDetailsController', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('it should render the BookingDetailsController  with default props', () => {
    const { getByTestId } = render(<BookingDetailsController {...props} />);

    expect(getByTestId('BookingDetailsControllerContainer')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsController and cancel is clickable', () => {
    const { getByText } = render(<BookingDetailsController {...props} />);

    const cancelButton = getByText('dashboard.bookings.cancelButton');
    expect(cancelButton).toBeEnabled();
  });

  it('it should render the BookingDetailsController  with isCancelable false', () => {
    const newProps = {
      ...props,
      manageBookingData: { ...manageBookingData, isCancellable: false },
    };
    const { queryByText } = render(<BookingDetailsController {...newProps} />);

    expect(queryByText('dashboard.bookings.cancelButton')).toBeFalsy();
  });

  it('it should render cancelButton in component', () => {
    const newProps = {
      ...props,
      manageBookingData: {
        ...manageBookingData,
        isCancellable: true,
        isAmendable: false,
      },
    };
    const { queryByText } = render(<BookingDetailsController {...newProps} />);

    expect(queryByText('dashboard.bookings.cancelButton')).toBeInTheDocument();
  });

  it('should render the aemLabelKey from manageBooking', () => {
    const newProps = {
      ...props,
      manageBookingData: {
        ...manageBookingData,
        isCancellable: false,
        isAmendable: false,
      },
    };
    const { getByTestId } = render(<BookingDetailsController {...newProps} />);
    expect(getByTestId('BookingDetailsControllerReasonLabel')).toBeInTheDocument();
  });

  it('should not render the aemLabelKey from manageBooking if the aemLabelKey comes empty', () => {
    const newProps = {
      ...props,
      manageBookingData: {
        ...manageBookingData,
        isCancellable: false,
        isAmendable: false,
        aemLabelKey: '',
      },
    };
    const { queryByTestId } = render(<BookingDetailsController {...newProps} />);
    expect(queryByTestId('BookingDetailsControllerReasonLabel')).not.toBeInTheDocument();
  });

  it('should not render the Amend or Cancel button if the reservation is not amendable and cancelable', () => {
    const newProps = {
      ...props,
      manageBookingData: {
        isAmendable: false,
        isCancellable: false,
      },
    };
    const { queryByTestId } = render(<BookingDetailsController {...newProps} />);
    expect(queryByTestId('dashboard.bookings.amendButton')).not.toBeInTheDocument();
    expect(queryByTestId('dashboard.bookings.cancelButton')).not.toBeInTheDocument();
  });
  it('should render isAmendable button if operaConfNumber exists and isAmendable is true', () => {
    const newProps = {
      ...props,
      operaConfNumber: '321321321',
      manageBookingData: {
        ...manageBookingData,
        isAmendable: true,
        isCancellable: false,
      },
    };
    const { queryByTestId } = render(<BookingDetailsController {...newProps} />);
    expect(queryByTestId('BookingDetailsController-AmendButton')).toBeInTheDocument();
  });

  it('should not render amend and cancel CTAs if the reservation has operaConfNumber and is not amendable and cancellable ', () => {
    const newProps = {
      ...props,
      operaConfNumber: '321321321',
      manageBookingData: {
        ...manageBookingData,
        isAmendable: false,
        isCancellable: false,
      },
    };

    const { queryByTestId } = render(<BookingDetailsController {...newProps} />);
    expect(queryByTestId('BookingDetailsController-AmendButton')).not.toBeInTheDocument();
    expect(queryByTestId('BookingDetailsController-CancelButton')).not.toBeInTheDocument();
  });

  it('should display amend and cancellable buttons if the reservation has operaConfNumber and is amendable and cancellable ', () => {
    const newProps = {
      ...props,
      operaConfNumber: '321321321',
      bookingType: BOOKING_TYPE.UPCOMING,
      area: Area.CCUI,
      manageBookingData: {
        ...manageBookingData,
        isAmendable: true,
        isCancellable: true,
      },
    };
    const { queryByTestId } = render(<BookingDetailsController {...newProps} />);
    expect(queryByTestId('BookingDetailsController-AmendButton')).toBeInTheDocument();
    expect(queryByTestId('BookingDetailsController-CancelButton')).toBeInTheDocument();
  });

  it('should display amend and cancellable buttons if the reservation doesnt have operaConfNumber and is amendable and cancellable are true ', () => {
    const newProps = {
      ...props,
      operaConfNumber: null,
      bookingType: BOOKING_TYPE.UPCOMING,
      rateType: 'rateType',
      area: Area.CCUI,
      isAmendPage: true,
      manageBookingData: {
        isAmendable: true,
        isCancellable: true,
      },
    };
    const { queryByTestId } = render(<BookingDetailsController {...newProps} />);
    expect(queryByTestId('BookingDetailsController-AmendButton')).toBeInTheDocument();
    expect(queryByTestId('BookingDetailsController-CancelButton')).toBeInTheDocument();
  });

  it('should display amend and cancellable buttons if the reservation has operaConfNumber and isAmendPage is true ', () => {
    const newProps = {
      ...props,
      operaConfNumber: '321321321',
      area: Area.CCUI,
      isAmendPage: true,
      manageBookingData: {
        ...manageBookingData,
        isAmendable: true,
        isCancellable: true,
      },
    };
    const { queryByTestId } = render(<BookingDetailsController {...newProps} />);
    expect(queryByTestId('BookingDetailsController-AmendButton')).toBeInTheDocument();
    expect(queryByTestId('BookingDetailsController-CancelButton')).toBeInTheDocument();
  });

  it('should render Contact us button because isRuleCompliant is false', () => {
    const newProps = {
      ...props,
      manageBookingData: { ...manageBookingData, isRuleCompliant: false },
    };
    const { queryByText } = render(<BookingDetailsController {...newProps} />);
    expect(queryByText('dashboard.bookings.contactUsButton')).toBeInTheDocument();
  });

  it('it should render the BookingDetailsController  with CCUI content', () => {
    const { getByText } = render(<BookingDetailsController {...{ ...props, area: Area.CCUI }} />);

    expect(getByText('ccui.idv.ctaButton')).toBeInTheDocument();
  });

  it('it should render IDV Modal when clicking on IDV Button', async () => {
    const { getByTestId, queryByTestId } = render(
      <BookingDetailsController {...{ ...props, area: Area.CCUI }} />
    );
    fireEvent.click(getByTestId('BookingDetailsController-CtaButton'));

    expect(queryByTestId('IDVModal-ModalTitle')).toBeInTheDocument();
  });
  it('it should render the BookingDetailsController and on clicking cancel it will not show IDV modal', async () => {
    const { getByTestId, queryByTestId } = render(
      <BookingDetailsController {...{ ...props, area: Area.CCUI, idvData: undefined }} />
    );
    fireEvent.click(getByTestId('BookingDetailsController-CtaButton'));

    expect(queryByTestId('IDVModal-ModalTitle')).not.toBeInTheDocument();
  });

  it('should open the cancel booking modal when the cancel button is clicked', () => {
    const newProps = {
      ...props,
      manageBookingData: { ...manageBookingData, isCancellable: true },
    };

    const { getByTestId, queryByTestId } = render(<BookingDetailsController {...newProps} />);

    const cancelButton = getByTestId('BookingDetailsController-CancelButton');
    expect(cancelButton).toBeInTheDocument();

    fireEvent.click(cancelButton);

    const cancelBookingModal = queryByTestId('CancelBookingModal-id');
    expect(cancelBookingModal).toBeInTheDocument();
  });

  it('should render BookingHistoryCancelBookingModal', async () => {
    const { getByTestId } = render(
      <BookingDetailsController
        {...{
          ...props,
          skipBookingRequest: true,
          bookingChannel: { channel: 'PI', subchannel: 'WEB', language: 'EN' },
          manageBookingData: { ...manageBookingData, isCancellable: true },
        }}
      />
    );

    expect(getByTestId('BookingHistoryCancelBookingModal-id')).toBeInTheDocument();
  });

  it('should call handleIDVModalClose with the correct arguments when closing the IDV modal', async () => {
    const newProps = JSON.parse(JSON.stringify(props));

    newProps.idvData.dpaStatus = {
      dpaPassed: true,
      dpaOverride: true,
      eCnpPassword: '',
    };
    newProps.setDpaInfo = jest.fn();
    newProps.setIdvData = jest.fn();
    newProps.area = Area.CCUI;

    const { getByTestId } = render(<BookingDetailsController {...newProps} />);

    const ctaBtn = getByTestId('BookingDetailsController-CtaButton');
    fireEvent.click(ctaBtn);

    const closeBtnIdv = getByTestId('IDVModal-CloseButton');
    fireEvent.click(closeBtnIdv);

    await waitFor(() => {
      expect(newProps.setDpaInfo).toHaveBeenCalledWith({ dpaPassed: true, dpaOverride: true });
    });
  });

  it('should work handleAmendBooking with isSecureAmend true (CCUI)', () => {
    const newProps = {
      ...props,
      area: Area.CCUI,
      isAmendPage: true,
      manageBookingData: { ...manageBookingData, isAmendable: true },
      dpaInfo: { dpaPassed: true, dpaOverride: false },
      sourceSystem: SOURCE_SYSTEM.BART,
      bookingSurname: 'Parker',
    };
    const { getByText } = render(<BookingDetailsController {...newProps} />);

    const ammendBtn = getByText('dashboard.bookings.amendButton');
    fireEvent.click(ammendBtn);

    expect(mockUseRouter.push).toHaveBeenCalledWith('localhost/gb/en/amend/leisure/details.html');
  });

  it('should work handleAmendBooking with isSecureAmend false (CCUI)', async () => {
    const newProps = {
      ...props,
      area: Area.CCUI,
      isAmendPage: true,
      manageBookingData: { ...manageBookingData, isAmendable: true },
      dpaInfo: { dpaPassed: true, dpaOverride: false },
      sourceSystem: SOURCE_SYSTEM.OPERA,
      bookingSurname: 'George',
      arrivalDate: '2023-12-20',
      bookingType: BOOKING_TYPE.UPCOMING,
    };
    const { getByText } = render(<BookingDetailsController {...newProps} />);

    const ammendBtn = getByText('dashboard.bookings.amendButton');
    fireEvent.click(ammendBtn);

    await waitFor(() => {
      expect(mockUseRouter.push).toHaveBeenCalledWith(
        '/gb/en/amend/details.html?bookingReference=AQPR1437'
      );
    });
  });

  it('should work handleAmendBooking with isSecureAmend false (BB)', async () => {
    const newProps = {
      ...props,
      area: Area.BB,
      isAmendPage: true,
      manageBookingData: { ...manageBookingData, isAmendable: true },
      dpaInfo: { dpaPassed: true, dpaOverride: false },
      sourceSystem: SOURCE_SYSTEM.OPERA,
      bookingSurname: 'George',
      arrivalDate: '2023-12-20',
      bookingType: BOOKING_TYPE.UPCOMING,
      isAmendSuccessful: false,
    };
    const { getByText } = render(<BookingDetailsController {...newProps} />);

    const ammendBtn = getByText('dashboard.bookings.amendButton');
    fireEvent.click(ammendBtn);

    waitFor(() => {
      expect(mockUseRouter.push).toHaveBeenCalledWith(
        '/gb/en/business-booker/amend/details.html?bookingReference=AQPR1437'
      );
    });
  });

  it('should call handleReuseDetails', async () => {
    const url = 'gb/en/guest-details?reservationId=12345';
    Object.defineProperty(window, 'location', {
      value: {
        href: url,
      },
      writable: true,
    });

    const newProps = JSON.parse(JSON.stringify(props));

    newProps.idvData.dpaStatus = {
      dpaPassed: true,
      dpaOverride: true,
      eCnpPassword: '',
    };
    newProps.setDpaInfo = jest.fn();
    newProps.setIdvData = jest.fn();
    newProps.area = Area.CCUI;

    const { getByTestId, getByText } = render(<BookingDetailsController {...newProps} />);

    const ctaBtn = getByTestId('BookingDetailsController-CtaButton');
    fireEvent.click(ctaBtn);

    const reuseBtn = getByText('ccui.idv.dpaStatus.reuseDetails');
    fireEvent.click(reuseBtn);

    expect(window.location.href).toBe('/gb/en/guest-details?reservationId=12345');
  });

  it('should persist cityName from addressLine4 when reusing details and cityName is empty', async () => {
    const url = 'gb/en/guest-details?reservationId=12345';
    Object.defineProperty(window, 'location', {
      value: {
        href: url,
      },
      writable: true,
    });

    const newProps = {
      ...props,
      area: Area.CCUI,
      defaultDataFromBooking: {
        cityName: '',
        addressLine4: 'Berlin',
      },
    };

    const { getByTestId, getByText } = render(<BookingDetailsController {...newProps} />);

    const ctaBtn = getByTestId('BookingDetailsController-CtaButton');
    fireEvent.click(ctaBtn);

    const reuseBtn = getByText('ccui.idv.dpaStatus.reuseDetails');
    fireEvent.click(reuseBtn);

    const formDetails = JSON.parse(window.localStorage.getItem('formDetails') as string);
    expect(formDetails.cityName).toBe('Berlin');
  });

  it('should persist reUseReservation in localStorage when isRemovePIIDataFromLocalStorageEnabled is true and reuse details is clicked', async () => {
    const url = 'gb/en/guest-details?reservationId=12345';
    Object.defineProperty(window, 'location', {
      value: {
        href: url,
      },
      writable: true,
    });

    const newProps = {
      ...props,
      area: Area.CCUI,
      basketReference: 'AWM-basket-ref',
      isRemovePIIDataFromLocalStorageEnabled: true,
    };
    newProps.idvData.dpaStatus = {
      dpaPassed: true,
      dpaOverride: true,
      eCnpPassword: '',
    };

    const { getByTestId, getByText } = render(<BookingDetailsController {...newProps} />);

    const ctaBtn = getByTestId('BookingDetailsController-CtaButton');
    fireEvent.click(ctaBtn);

    const reuseBtn = getByText('ccui.idv.dpaStatus.reuseDetails');
    fireEvent.click(reuseBtn);

    const reUseReservation = JSON.parse(window.localStorage.getItem('reUseReservation') as string);
    expect(reUseReservation.id).toBe('AWM-basket-ref');
  });

  describe('AB Testing - For Analytics (Ancillaries Tabs)', () => {
    beforeEach(() => {
      document.cookie.split(';').forEach((cookie) => {
        const name = cookie.split('=')[0].trim();
        document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/`;
      });
    });

    afterEach(() => {
      if (window.piConfig) {
        delete window.piConfig;
      }
    });
    it('should not set ANCILLARIES_TABS A/B Test cookie when `window.piConfig.ancillaries` is not defined when handleAmendBooking is called', async () => {
      Object.defineProperty(window, 'piConfig', {
        value: {
          mode: 'variant',
          expiryInMinutes: 30,
          cookieName: 'ancillaries_tabs',
          configName: 'ancillaries',
        },
        writable: true,
      });
      const newProps = {
        ...props,
        area: Area.BB,
        isAmendPage: true,
        manageBookingData: { ...manageBookingData, isAmendable: true },
        dpaInfo: { dpaPassed: true, dpaOverride: false },
        sourceSystem: SOURCE_SYSTEM.OPERA,
        bookingSurname: 'George',
        arrivalDate: '2023-12-20',
        bookingType: BOOKING_TYPE.UPCOMING,
        isAmendSuccessful: false,
      };
      const { getByText } = render(<BookingDetailsController {...newProps} />);
      const ammendBtn = getByText('dashboard.bookings.amendButton');
      fireEvent.click(ammendBtn);
      expect(document.cookie).toBeFalsy();
    });

    it('should set cookie for scrollable tabs if not present - when when handleAmendBooking is called', async () => {
      Object.defineProperty(window, 'piConfig', {
        value: {
          mode: 'variant',
        },
        writable: true,
      });

      const newProps = {
        ...props,
        area: Area.BB,
        isAmendPage: true,
        manageBookingData: { ...manageBookingData, isAmendable: true },
        dpaInfo: { dpaPassed: true, dpaOverride: false },
        sourceSystem: SOURCE_SYSTEM.OPERA,
        bookingSurname: 'George',
        arrivalDate: '2023-12-20',
        bookingType: BOOKING_TYPE.UPCOMING,
        isAmendSuccessful: false,
      };
      const { getByText } = render(<BookingDetailsController {...newProps} />);
      const ammendBtn = getByText('dashboard.bookings.amendButton');
      fireEvent.click(ammendBtn);
      document.cookie = `ancillaries_tabs=variant`;
      expect(document.cookie).toContain('variant');
    });
  });
});
