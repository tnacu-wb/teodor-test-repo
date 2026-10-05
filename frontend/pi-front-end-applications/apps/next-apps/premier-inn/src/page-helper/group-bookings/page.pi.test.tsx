import '@testing-library/jest-dom';
import { analytics, formatDate } from '@whitbread-eos/utils';
import React from 'react';

import { render, userEvent, screen, waitFor, fireEvent } from '../../utils/test-utils';
import { Page as GroupBookings } from './index';

const mockGroupBookingTicketNumber = 'CAS-12345';
const mockGroupBookingIncidentNumber = 'INC-12345';
const mockCreateGroupBookingMutation = jest.fn();
const mockedMutationResponse = {
  mutation: {
    mutate: mockCreateGroupBookingMutation,
  },
  isSuccess: false,
  isLoading: false,
  isError: false,
  data: {},
};

const mockedMutationRequest = jest.fn();
const mockGroupBookingHotelsRequest = jest.fn();

const mockedGroupBookingHotelsResponse = {
  isSuccess: true,
  isError: false,
  data: {
    hotels: [
      { code: 'ABETIR', title: 'Aberdare', brand: 'PI' },
      { code: 'ABECOC', title: 'Aberdeen (Anderson Drive)', brand: 'PI' },
      { code: 'BERCIT', title: 'Berlin city', brand: 'PID' },
    ],
  },
};

const mockedGroupBookingHotelsFailResponse = {
  isSuccess: false,
  isError: true,
  data: {},
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => ({
    locale: 'en',
  }),
  analytics: {
    update: jest.fn(),
  },
  useRestQueryRequest: () => mockGroupBookingHotelsRequest(),
  useMutationRequest: () => mockedMutationRequest(),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  SEO: () => <div></div>,
}));

jest.mock('next/router', () => ({
  useRouter() {
    return {
      query: { key: '' },
    };
  },
}));

describe('Group Bookings', () => {
  beforeEach(() => {
    window.HTMLElement.prototype.scrollIntoView = jest.fn();
    jest.clearAllMocks();
    mockGroupBookingHotelsRequest.mockImplementation(() => mockedGroupBookingHotelsResponse);
    mockedMutationRequest.mockImplementation(() => mockedMutationResponse);
  });

  it('should render Group Bookings page skeleton', async () => {
    const { getByTestId } = render(<GroupBookings />);

    expect(getByTestId('GroupBookingsPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('GroupBookingsPage-page-title')).toBeInTheDocument();
  });

  it('should fill the first name and last name', async () => {
    const { getByTestId } = render(<GroupBookings />);

    const inputFirstName = await waitFor(() => getByTestId('input-firstName'));
    fireEvent.change(inputFirstName, { target: { value: 'Stefan' } });

    const inputLastName = getByTestId('input-lastName');
    fireEvent.change(inputLastName, { target: { value: 'Ciora' } });

    expect(inputFirstName).toHaveValue('Stefan');
    expect(inputLastName).toHaveValue('Ciora');
  });

  it('should enter email and phone number', async () => {
    const { getByTestId } = render(<GroupBookings />);

    const inputEmail = await waitFor(() => getByTestId(/^input-emailAddress$/i));
    fireEvent.change(inputEmail, { target: { value: 'stefan.ciora@whitbread.com' } });

    const inputPhone = await waitFor(() => getByTestId('GroupBookingsPage-Mobile-phoneNumber'));
    fireEvent.change(inputPhone, { target: { value: '+40728954441' } });
    expect(inputEmail).toHaveValue('stefan.ciora@whitbread.com');
    expect(inputPhone).toHaveValue('+40728954441');
  });

  it('should call handleAccordionToggle and open Booking details section', async () => {
    const { getByText } = render(<GroupBookings />);
    const accordionBookingDetailsToggleButton = getByText('groupBooking.bookingDetails.title');
    userEvent.click(accordionBookingDetailsToggleButton);
    expect(
      await screen.findByText('groupBooking.bookingDetails.bookingType.title')
    ).toBeInTheDocument();
  });

  it('should display meal options', async () => {
    render(<GroupBookings />);
    expect(
      await screen.findByText('groupBooking.bookingDetails.packageType.breakfast')
    ).toBeInTheDocument();
    expect(
      await screen.findByText('groupBooking.bookingDetails.packageType.mealdeal')
    ).toBeInTheDocument();
  });

  it('should display the default additional info text for comments', async () => {
    const { getByText } = render(<GroupBookings />);
    expect(
      getByText('groupBooking.roomRequirements.additionalInfo.description')
    ).toBeInTheDocument();
  });

  it('should display the alt additional info text (youth group checkbox enabled) - for comments ', async () => {
    const { getByText } = render(<GroupBookings />);

    const accordionBookingDetailsToggleButton = getByText('groupBooking.bookingDetails.title');

    // open booking details section (accordion 2)
    await userEvent.click(accordionBookingDetailsToggleButton);

    const youthGroupChkBx = getByText(
      'groupBooking.bookingDetails.purposeOfStay.isSchoolOrYouthGroup'
    );

    // check youth group checkbox
    await userEvent.click(youthGroupChkBx);

    // open room requirements section (accordion 3)
    const accordionRoomReqToggleButton = getByText('groupBooking.roomRequirements.title');
    await userEvent.click(accordionRoomReqToggleButton);

    // show instead the alternative - additional info text (for youth group enabled)
    expect(
      getByText(/groupBooking.roomRequirements.additionalInfo.description.isSchoolOrYouthGroup/i)
    ).toBeInTheDocument();
  });

  it('should call handleAccordionToggle and open Room Requirements section', async () => {
    const { getByText } = render(<GroupBookings />);
    const accordionRoomReqToggleButton = getByText('groupBooking.roomRequirements.title');

    userEvent.click(accordionRoomReqToggleButton);
    expect(
      await screen.findByText('groupBooking.roomRequirements.rooms.title')
    ).toBeInTheDocument();
  });

  it('should enable plus counter if within max count ', async () => {
    const { getByText, getByTestId } = render(<GroupBookings />);

    const accordionRoomReqToggleButton = getByText('groupBooking.roomRequirements.title');
    await userEvent.click(accordionRoomReqToggleButton);

    const editableCounterFld = getByTestId(
      'GroupBookingsPage-AddSubtractControls-singleOccupancy-inputValue'
    );
    const plusBtn = getByTestId('GroupBookingsPage-AddSubtractControls-singleOccupancy-AddButton');
    const minusBtn = getByTestId(
      'GroupBookingsPage-AddSubtractControls-singleOccupancy-SubtractButton'
    );

    expect(editableCounterFld).toBeInTheDocument();
    expect(plusBtn).toBeInTheDocument();
    expect(plusBtn).toBeEnabled();
    expect(minusBtn).toBeInTheDocument();
    expect(minusBtn).toBeDisabled();

    editableCounterFld.focus();
    await userEvent.type(editableCounterFld, '10');
    expect(minusBtn).toBeEnabled();
    expect(plusBtn).toBeEnabled();
  });

  it('should disable plus counter if exceeds max count ', async () => {
    const { getByText, getByTestId } = render(<GroupBookings />);
    const accordionRoomReqToggleButton = getByText('groupBooking.roomRequirements.title');
    await userEvent.click(accordionRoomReqToggleButton);

    const editableCounterFld = getByTestId(
      'GroupBookingsPage-AddSubtractControls-singleOccupancy-inputValue'
    );
    const plusBtn = getByTestId('GroupBookingsPage-AddSubtractControls-singleOccupancy-AddButton');
    const minusBtn = getByTestId(
      'GroupBookingsPage-AddSubtractControls-singleOccupancy-SubtractButton'
    );
    await userEvent.type(editableCounterFld, '99');
    expect(plusBtn).toBeDisabled();
    expect(minusBtn).toBeEnabled();
  });

  it('should call handleAccordionToggle and re-open Contact details section', async () => {
    const { getByText } = render(<GroupBookings />);
    const accordionContactDetailsToggleButton = getByText('groupBooking.contactDetails.title');

    userEvent.click(accordionContactDetailsToggleButton);
    expect(
      await screen.findByText('groupBooking.contactDetails.yourContact.title')
    ).toBeInTheDocument();
  });

  it('should NOT display error notificaiton for hotels list', async () => {
    render(<GroupBookings />);
    expect(
      screen.queryByText('groupBooking.bookingDetails.bookingDetails.hotelSelection.listError')
    ).not.toBeInTheDocument();
  });

  it('should trigger mutation when submitting the form', async () => {
    const today = new Date();
    const tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);
    const { getByText } = render(
      <GroupBookings
        defaultValues={{
          title: 'Mr',
          firstName: 'Test',
          lastName: 'Booker',
          phoneNumber: '+445555555555',
          emailAddress: 'test-booker@test.com',
          BookerType: 'Personal',
          purposeOfStay: 'Business',
          reasonForVisit: 'Bus tour',
          packageType: 'BREAKFAST',
          isPackageTypeBf: true,
          hotels: 'Gatwick Airport',
          datepicker: [today, tomorrow],
          isSchoolOrYouth: false,
          isAccessibleRoom: false,
          isTravellingWithChild: false,
          comments: 'Test comments',
          singleOccupancy: 5,
          doubleOccupancy: 5,
          twinRooms: 0,
          familyOf21A1C: 0,
          familyOf32A1C: 0,
          familyOf31A2C: 0,
          familyOf42A2C: 0,
          accessibleSingle: 0,
          accessibleDouble: 0,
          accessibleTwin: 0,
          RoomTotalCount: 0,
        }}
      />
    );
    const accordionRoomReqToggleButton = getByText('groupBooking.roomRequirements.title');

    await userEvent.click(accordionRoomReqToggleButton);
    const submitButton = getByText('groupBooking.submit.button.label');

    await userEvent.click(submitButton);
    expect(mockCreateGroupBookingMutation).toHaveBeenCalledTimes(1);
    expect(analytics.update).toHaveBeenCalledWith({
      gbf: expect.objectContaining({
        booker: 'Personal',
        typeOfStay: 'Business',
        schoolGroup: false,
        reasonForVisit: 'Bus tour',
        checkInDate: formatDate(today.toISOString(), 'dd/MM/yyyy'),
        checkOutDate: formatDate(tomorrow.toISOString(), 'dd/MM/yyyy'),
        noOfNights: 1,
        packageType: 'BREAKFAST',
        childrenStaying: false,
        accessibleRoomRequired: false,
        totalRooms: 10,
        commentsSubmitted: true,
        singleOccupancy: 5,
        doubleOccupancy: 5,
        twinOccupancy: 0,
        familyOf21A1C: 0,
        familyOf32A1C: 0,
        familyOf31A2C: 0,
        familyOf42A2C: 0,
        accessibleSingle: 0,
        accessibleDouble: 0,
        accessibleTwin: 0,
        validation: '',
      }),
    });
  });

  it('should include reasonForVisitOther and companyName in the mutation payload if provided', async () => {
    const mockMutate = jest.fn();
    mockedMutationRequest.mockReturnValue({
      mutation: {
        mutate: mockMutate,
      },
      isSuccess: false,
      isLoading: false,
      isError: false,
      data: {},
    });

    const { getByText } = render(
      <GroupBookings
        defaultValues={{
          ...{
            title: 'Mr',
            firstName: 'Test',
            lastName: 'Booker',
            phoneNumber: '+445555555555',
            emailAddress: 'test-booker@test.com',
            BookerType: 'Personal',
            purposeOfStay: 'Business',
            reasonForVisit: 'Bus tour',
            companyName: 'Test Company',
            packageType: 'BREAKFAST',
            isPackageTypeBf: true,
            hotels: 'Gatwick Airport',
            datepicker: [new Date(), new Date()],
            isSchoolOrYouth: false,
            isAccessibleRoom: false,
            isTravellingWithChild: false,
            comments: 'Test comments',
            singleOccupancy: 5,
            doubleOccupancy: 5,
            twinRooms: 0,
            familyOf21A1C: 0,
            familyOf32A1C: 0,
            familyOf31A2C: 0,
            familyOf42A2C: 0,
            accessibleSingle: 0,
            accessibleDouble: 0,
            accessibleTwin: 0,
            RoomTotalCount: 10,
          },
          reasonForVisitOther: 'Custom reason',
          RoomTotalCount: 10,
        }}
      />
    );

    const submitButton = getByText('groupBooking.submit.button.label');
    await userEvent.click(submitButton);

    expect(mockMutate).toHaveBeenCalledWith(
      expect.objectContaining({
        createGroupBookingCriteria: expect.objectContaining({
          reasonForVisitOther: 'Custom reason',
          companyName: 'Test Company',
        }),
      })
    );
  });

  it('should disable submit when create group booking mutation is in progress', async () => {
    mockedMutationRequest.mockReturnValue({
      mutation: {
        mutate: mockCreateGroupBookingMutation,
      },
      isSuccess: false,
      isLoading: true,
      isError: false,
      data: {},
    });
    const { getByText } = render(<GroupBookings />);
    const accordionRoomReqToggleButton = getByText('groupBooking.roomRequirements.title');

    await userEvent.click(accordionRoomReqToggleButton);
    const submitButton = getByText('groupBooking.submit.button.label');

    expect(submitButton).toBeInTheDocument();
    expect(submitButton).toBeDisabled();
  });

  it('should display an error message when create group booking mutation fails', async () => {
    const analyticsUpdate = jest.spyOn(analytics, 'update');
    mockedMutationRequest.mockReturnValue({
      mutation: {
        mutate: mockCreateGroupBookingMutation,
      },
      isSuccess: false,
      isLoading: false,
      isError: true,
      data: {},
      error: { response: { errors: [{ message: 'Submission failed' }] } },
    });
    render(<GroupBookings />);

    await waitFor(() =>
      expect(analyticsUpdate).toHaveBeenCalledWith({
        gbf: expect.objectContaining({
          validation: 'Submission failed',
        }),
      })
    );
  });

  it('should display an empty string for error message when create group booking mutation fails', async () => {
    const analyticsUpdate = jest.spyOn(analytics, 'update');
    mockedMutationRequest.mockReturnValue({
      mutation: {
        mutate: mockCreateGroupBookingMutation,
      },
      isSuccess: false,
      isLoading: false,
      isError: true,
      data: {},
      error: { response: { errors: [{ message: '' }] } },
    });
    render(<GroupBookings />);

    await waitFor(() =>
      expect(analyticsUpdate).toHaveBeenCalledWith({
        gbf: expect.objectContaining({
          validation: 'groupBooking.confirmation.error',
        }),
      })
    );
  });

  it('should display a default error message when create mutation fails and no error response', async () => {
    const analyticsUpdate = jest.spyOn(analytics, 'update');
    window.__satelliteLoaded = true;
    window._satellite = {
      track: jest.fn(),
    };
    mockedMutationRequest.mockReturnValue({
      mutation: {
        mutate: mockCreateGroupBookingMutation,
      },
      isSuccess: false,
      isLoading: false,
      isError: true,
      data: {},
      error: {},
    });
    render(<GroupBookings />);
    await waitFor(() =>
      expect(analyticsUpdate).toHaveBeenCalledWith({
        gbf: expect.objectContaining({
          validation: 'groupBooking.confirmation.error',
        }),
      })
    );
    expect(window._satellite.track).toHaveBeenCalledWith('groupFormError');
  });

  it('should not submit the form if RoomTotalCount is less than 10', async () => {
    const today = new Date();
    const tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);
    const incompleteBookingAnalytics = jest.spyOn(analytics, 'update');
    const { getByText } = render(
      <GroupBookings
        defaultValues={{
          title: 'Mr',
          firstName: 'Test',
          lastName: 'Booker',
          phoneNumber: '+445555555555',
          emailAddress: 'test-booker@test.com',
          BookerType: 'Personal',
          purposeOfStay: 'Business',
          reasonForVisit: 'Bus tour',
          packageType: 'BREAKFAST',
          isPackageTypeBf: true,
          hotels: 'Gatwick Airport',
          datepicker: [today, tomorrow],
          isSchoolOrYouth: false,
          isAccessibleRoom: false,
          isTravellingWithChild: false,
          comments: 'Test comments',
          singleOccupancy: 5,
          doubleOccupancy: 0,
          twinRooms: 0,
          familyOf21A1C: 0,
          familyOf32A1C: 0,
          familyOf31A2C: 0,
          familyOf42A2C: 0,
          accessibleSingle: 0,
          accessibleDouble: 0,
          accessibleTwin: 0,
          RoomTotalCount: 5,
        }}
      />
    );
    const submitButton = getByText('groupBooking.submit.button.label');
    await userEvent.click(submitButton);

    expect(incompleteBookingAnalytics).toHaveBeenCalledWith({
      validation: 'Incomplete Fields Group Form Booking',
    });
    expect(mockCreateGroupBookingMutation).not.toHaveBeenCalled();
  });

  it('should display success confirmation component when create group booking mutation is successful', async () => {
    mockedMutationRequest.mockReturnValue({
      mutation: {
        mutate: mockCreateGroupBookingMutation,
      },
      isSuccess: true,
      isLoading: false,
      isError: false,
      data: {
        createGroupBooking: {
          ticketNumber: mockGroupBookingTicketNumber,
          incidentId: mockGroupBookingIncidentNumber,
        },
      },
    });
    const { queryByTestId, getByText } = render(<GroupBookings />);
    expect(queryByTestId('GroupBookingsPage-Form')).not.toBeInTheDocument();
    expect(queryByTestId('GroupBookingsConfirmation-Wrapper')).toBeInTheDocument();
    expect(getByText(mockGroupBookingTicketNumber)).toBeInTheDocument();
  });

  it('should display breakfast checkbox when german hotel is selected', async () => {
    const { getByText, queryByTestId } = render(<GroupBookings />);
    const accordionBookingDetailsToggleButton = getByText('groupBooking.bookingDetails.title');
    userEvent.click(accordionBookingDetailsToggleButton);
    expect(
      await screen.findByText('groupBooking.bookingDetails.bookingType.title')
    ).toBeInTheDocument();

    Element.prototype.scrollIntoView = () => null;
    const input = queryByTestId('HotelsDropdownPicker-locationPlaceholder');
    expect(input).toBeInTheDocument();
    userEvent.type(input, 'Berlin city');
    const option = await screen.findByText('Berlin city');
    expect(option).toBeInTheDocument();
    userEvent.click(option);
    Element.prototype.scrollIntoView = () => null;
    waitFor(() =>
      expect(screen.findByTestId('packageType-Breakfast-checkbox')).toBeInTheDocument()
    );
  });
});

describe('Group Bookings - hotels list fail response', () => {
  beforeEach(() => {
    mockGroupBookingHotelsRequest.mockImplementation(() => mockedGroupBookingHotelsFailResponse);
  });
  it('should display error notificaiton for hotels list error', async () => {
    render(<GroupBookings />);

    waitFor(() =>
      expect(
        screen.queryByText('groupBooking.bookingDetails.bookingDetails.hotelSelection.listError')
      ).toBeInTheDocument()
    );
  });
});
