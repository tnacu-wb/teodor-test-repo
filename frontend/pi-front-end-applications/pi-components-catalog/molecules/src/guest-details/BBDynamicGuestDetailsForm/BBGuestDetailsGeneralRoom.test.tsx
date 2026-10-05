import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import React from 'react';
import { FieldErrors, useForm } from 'react-hook-form';

import { fireEvent, render, waitFor, userEvent } from '../../utils/test-utils';
import BBGuestDetailsGeneralRoom from './BBGuestDetailsGeneralRoom';

const generalProps = {
  fieldType: {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'bbGuestDetails',
    dropdownOptions: [{ id: 'Mr', label: 'Mr' }],
    label: '',
    props: {},
  },
  testId: 'GuestDetailsBBContainer',
  componentName: 'bbGuestDetails',
  errors: {
    bbGuestDetails: { 0: { firstName: { message: 'error first name message' } } },
  },
  isDynamicSearchVisible: false,
};
const mockSetGuestUser = jest.fn();

const ComponentWithSingleRoom = (generalProps: any) => {
  const { control } = useForm();

  control._setErrors({
    [generalProps.componentName]: { 0: { firstName: { message: 'error first name message' } } },
  } as unknown as FieldErrors);
  const singleRoomProps: any = {
    control,
    formField: generalProps.fieldType,
    errors: generalProps.errors,
    numberOfRooms: 1,
    labels: {},
    roomNumber: 0,
    testid: generalProps.testId,
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
      bbAccompanyingGuestDetails: [
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
    setGuestUser: mockSetGuestUser,
    index: 0,
    reset: jest.fn(),
    isDynamicSearchVisible: generalProps.isDynamicSearchVisible,
    handleResetField: jest.fn(),
    componentName: generalProps.componentName,
  };

  return <BBGuestDetailsGeneralRoom {...singleRoomProps} />;
};

const ComponentWithMultipleRooms = (generalProps: any) => {
  const { control } = useForm();

  const multipleRoomsProps: any = {
    control,
    formField: generalProps.fieldType,
    errors: generalProps.props,
    numberOfRooms: 2,
    labels: {},
    roomNumber: 0,
    testid: generalProps.testId,
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
      bbAccompanyingGuestDetails: [
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
    setGuestUser: mockSetGuestUser,
    index: 0,
    reset: jest.fn(),
    isDynamicSearchVisible: generalProps.isDynamicSearchVisible,
    componentName: generalProps.componentName,
  };

  return (
    <QueryClientProvider client={new QueryClient()}>
      <BBGuestDetailsGeneralRoom {...multipleRoomsProps} />
    </QueryClientProvider>
  );
};

describe('General Guest Details BB', () => {
  it('should render the  Guest Details BB with single room', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <ComponentWithSingleRoom {...generalProps} />
      </QueryClientProvider>
    );
    expect(getByTestId('GuestDetailsBBContainer-Room-1')).toBeInTheDocument();
  });
  it('should render the  Guest Details BB with multiple rooms', () => {
    const { getByTestId } = render(<ComponentWithMultipleRooms {...generalProps} />);
    expect(getByTestId('GuestDetailsBBContainer-Room-1')).toBeInTheDocument();
  });

  it('should render the  Guest Details BB and should switch to dynamic mode for  GD section ', async () => {
    const { getByTestId, getByText, findByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <ComponentWithSingleRoom
          {...{
            ...generalProps,
          }}
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(getByText('error first name message')).toBeInTheDocument();
    });
    const button = getByTestId('GuestDetailsBBContainer-SwitchToDynamic');
    fireEvent.click(button);
    expect(await findByTestId('GuestDetailsBBContainer-DynamicGuestLead-1')).toBeInTheDocument();
  });

  it('should render the  Guest Details BB and should switch to dynamic mode for accompanying GD section ', async () => {
    const { getByTestId, getByText, findByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <ComponentWithSingleRoom
          {...{
            ...generalProps,
            fieldType: { ...generalProps.fieldType, name: 'bbAccompanyingGuestDetails' },
            testId: 'AccompanyingGuestDetailsBBContainer',
            componentName: 'bbAccompanyingGuestDetails',
            errors: {
              bbAccompanyingGuestDetails: {
                0: { firstName: { message: 'error first name message' } },
              },
            },
          }}
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(getByText('error first name message')).toBeInTheDocument();
    });
    const button = getByTestId('AccompanyingGuestDetailsBBContainer-SwitchToDynamic');
    fireEvent.click(button);
    expect(
      await findByTestId('AccompanyingGuestDetailsBBContainer-DynamicGuestLead-1')
    ).toBeInTheDocument();
  });

  it('should render the  Guest Details BB and should switch to dynamic mode for lead guest details bb ', async () => {
    const { getByTestId, getByText, findByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <ComponentWithSingleRoom
          {...{
            ...generalProps,
            fieldType: { ...generalProps.fieldType, name: 'guestDetailsBBForm' },
            testId: 'GuestDetailsBBForm',
            componentName: 'guestDetailsBBForm',
            errors: {
              guestDetailsBBForm: {
                0: { firstName: { message: 'error first name message' } },
              },
            },
          }}
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(getByText('error first name message')).toBeInTheDocument();
    });
    const button = getByTestId('GuestDetailsBBForm-SwitchToDynamic');
    fireEvent.click(button);
    expect(await findByTestId('GuestDetailsBBForm-DynamicGuestLead-1')).toBeInTheDocument();
  });

  it('should render the  Guest Details BB and should switch to manual mode for  GD section ', async () => {
    const { getByTestId, getByText, findByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <ComponentWithSingleRoom
          {...{
            ...generalProps,

            errors: {
              bbGuestDetails: ['err'],
            },
            isDynamicSearchVisible: true,
          }}
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(getByText('config.errorMessages.yourDetails.empSearch.invalid')).toBeInTheDocument();
    });
    const button = getByTestId('GuestDetailsBBContainer-SwitchToManual');
    fireEvent.click(button);
    expect(await findByTestId('GuestDetailsBBContainer-ManualGuestLead-1')).toBeInTheDocument();
  });

  it('should render the  Guest Details BB and should switch to manual mode for accompanying GD section ', async () => {
    const { getByTestId, getByText, findByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <ComponentWithSingleRoom
          {...{
            ...generalProps,
            fieldType: { ...generalProps.fieldType, name: 'bbAccompanyingGuestDetails' },
            testId: 'AccompanyingGuestDetailsBBContainer',
            componentName: 'bbAccompanyingGuestDetails',
            errors: {
              bbAccompanyingGuestDetails: ['err'],
            },
            isDynamicSearchVisible: true,
          }}
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(getByText('config.errorMessages.yourDetails.empSearch.invalid')).toBeInTheDocument();
    });

    const button = getByTestId('AccompanyingGuestDetailsBBContainer-SwitchToManual');
    fireEvent.click(button);
    expect(
      await findByTestId('AccompanyingGuestDetailsBBContainer-ManualGuestLead-1')
    ).toBeInTheDocument();
  });

  it('should render the  Guest Details BB and should switch to manual mode for lead GD section ', async () => {
    const { getByTestId, getByText, findByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <ComponentWithSingleRoom
          {...{
            ...generalProps,
            fieldType: { ...generalProps.fieldType, name: 'guestDetailsBBForm' },
            testId: 'GuestDetailsBBForm',
            componentName: 'guestDetailsBBForm',
            errors: {
              guestDetailsBBForm: ['err'],
            },
            isDynamicSearchVisible: true,
          }}
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(getByText('config.errorMessages.yourDetails.empSearch.invalid')).toBeInTheDocument();
    });
    const button = getByTestId('GuestDetailsBBForm-SwitchToManual');
    fireEvent.click(button);
    expect(await findByTestId('GuestDetailsBBForm-ManualGuestLead-1')).toBeInTheDocument();
  });

  it('should render the  Guest Details BB and call onChange function for every field ', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <ComponentWithSingleRoom
          {...{
            ...generalProps,
            errors: {},
          }}
        />
      </QueryClientProvider>
    );
    const inputFirstName = getByTestId('input-bbGuestDetails[0][firstName]');

    userEvent.click(inputFirstName);

    userEvent.type(inputFirstName, 'Ellie');
    expect(inputFirstName).toHaveValue('Ellie');

    const inputLastName = getByTestId('input-bbGuestDetails[0][lastName]');

    userEvent.click(inputLastName);

    userEvent.type(inputLastName, 'Smith');
    expect(inputLastName).toHaveValue('Smith');

    const inputEmail = getByTestId('input-bbGuestDetails[0][emailAddress]');

    userEvent.click(inputEmail);

    userEvent.type(inputEmail, 'test@gmail.com');
    expect(inputEmail).toHaveValue('test@gmail.com');
  });
});
