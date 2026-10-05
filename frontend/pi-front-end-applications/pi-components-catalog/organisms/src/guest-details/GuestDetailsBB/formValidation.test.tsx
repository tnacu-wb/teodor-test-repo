import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { BUSINESS_BOOKER_USER_ROLES } from '@whitbread-eos/api';
import { Form } from '@whitbread-eos/atoms';
import { useCookieForABTesting } from '@whitbread-eos/utils';
import React from 'react';

// Add this import
import { act, fireEvent, render, userEvent, waitFor } from '../../utils/test-utils';
import { guestDetailsBBFormConfig } from './guestDetailsBBFormConfig';

// Mock useCookieForABTesting
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCookieForABTesting: jest.fn(),
}));

const getFormState = jest.fn();
const onSubmit = jest.fn();
const validationLabels = {
  titleError: 'titleError',
  firstNameRequiredError: 'firstNameRequiredError',
  firstNameMinError: 'firstNameMinError',
  firstNameInvalidError: 'firstNameInvalidError',
  lastNameRequiredError: 'lastNameRequiredError',
  lastNameInvalidError: 'lastNameInvalidError',
  emailInvalidError: 'emailInvalidError',
};
const labels = {
  title: 'TitleLabel',
  firstName: 'FirstNameLabel',
  lastName: 'LastNameLabel',
  email: 'EmailLabel',
};
const t = jest.fn();
const numberOfRooms = 1;
const generalProps = {
  isAccompanyingGuestDetailsEnabled: false,
};

const Component = (generalProps: any) => {
  const defaultValues = {
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
  };

  // Mock the return value of useCookieForABTesting
  (useCookieForABTesting as jest.Mock).mockReturnValue(true);

  return (
    <QueryClientProvider client={new QueryClient()}>
      <Form
        data-testid={'Form'}
        {...guestDetailsBBFormConfig({
          autoComplete: '',
          guestList: {
            bbGuestDetails: [],
          },
          queryClient: new QueryClient(),
          setGuestUser: jest.fn(),
          getFormState,
          defaultValues,
          onSubmit,
          baseDataTestIdGuestDetails: 'GuestDetailsBBContainer',
          baseDataTestIdAccompayningGuestDetails: 'AccompanyingGuestDetailsBBContainerTest',
          t,
          labels,
          validationLabels,
          numberOfRooms,
          isDynamicSearchVisible: false,
          isAccompanyingGuestDetailsEnabled: generalProps.isAccompanyingGuestDetailsEnabled,
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
        })}
      />
    </QueryClientProvider>
  );
};

describe('Render Form', () => {
  it('should render Form with GD components correctly', async () => {
    const { queryByTestId } = render(<Component {...generalProps} />);

    // guest details section
    expect(queryByTestId('GuestDetailsBBContainer-Form')).toBeTruthy();
    expect(queryByTestId('GuestDetailsBBContainer-Form-Container')).toBeTruthy();
    expect(
      queryByTestId('DropdownComp-GuestDetailsBBContainer-Form-TitleDropdown-entireList')
    ).toBeTruthy();
    expect(queryByTestId('input-bbGuestDetails[0][firstName]')).toBeTruthy();
    expect(queryByTestId('input-bbGuestDetails[0][lastName]')).toBeTruthy();
    expect(queryByTestId('input-bbGuestDetails[0][emailAddress]')).toBeTruthy();
  });

  it('should throw error for title field if it is emtpy', async () => {
    const { getByTestId } = render(<Component {...generalProps} />);

    await act(async () => {
      fireEvent.click(
        getByTestId('DropdownComp-GuestDetailsBBContainer-Form-TitleDropdown-menuButton')
      );
    });

    expect(
      getByTestId('DropdownComp-GuestDetailsBBContainer-Form-TitleDropdown-entireList')
    ).toBeInTheDocument();
  });

  it('should throw error for first name field if character is not valid', async () => {
    const { getByText, getByTestId } = render(<Component {...generalProps} />);
    const input = getByTestId('input-bbGuestDetails[0][firstName]');

    await act(async () => {
      userEvent.type(input, '1');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(getByText(validationLabels.firstNameInvalidError)).toBeInTheDocument();
    });
  });

  it('should throw error for first name field if input is empty', async () => {
    const { getByText, getByTestId } = render(<Component {...generalProps} />);
    const input = getByTestId('input-bbGuestDetails[0][firstName]');

    await act(async () => {
      userEvent.clear(input);
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(getByText(validationLabels.firstNameRequiredError)).toBeInTheDocument();
    });
  });

  it('should throw error for first name field', async () => {
    const { getByText, getByTestId } = render(<Component {...generalProps} />);
    const input = getByTestId('input-bbGuestDetails[0][firstName]');

    await act(async () => {
      userEvent.type(input, 'a');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(getByText(validationLabels.firstNameMinError)).toBeInTheDocument();
    });
  });

  it('should throw error for first name field if input is too long (21 chars)', async () => {
    const { getByText, getByTestId } = render(<Component {...generalProps} />);
    const input = getByTestId('input-bbGuestDetails[0][firstName]');

    await act(async () => {
      userEvent.type(input, 'abcdeabcdeabcdeabcdea');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(getByText(validationLabels.firstNameRequiredError)).toBeInTheDocument();
    });
  });

  it('should throw error for first name field if input has 20 characters without spaces before the first letter and after the last letter', async () => {
    const { getByTestId, queryByText } = render(<Component {...generalProps} />);
    const input = getByTestId('input-bbGuestDetails[0][firstName]');

    await act(async () => {
      userEvent.type(input, '   abcdeabcdeabcdeabcde        ');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(queryByText(validationLabels.firstNameRequiredError)).not.toBeInTheDocument();
    });
  });

  it('should throw error for last name field', async () => {
    const { getByText, getByTestId } = render(<Component {...generalProps} />);
    const input = getByTestId('input-bbGuestDetails[0][lastName]');

    await act(async () => {
      userEvent.type(input, '2');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(getByText(validationLabels.lastNameInvalidError)).toBeInTheDocument();
    });
  });

  it('should not throw error for last name field if data has minimum 1 letter', async () => {
    const { queryByText, getByTestId } = render(<Component {...generalProps} />);
    const input = getByTestId('input-bbGuestDetails[0][lastName]');

    await act(async () => {
      userEvent.type(input, 'a');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(queryByText(validationLabels.lastNameInvalidError)).not.toBeInTheDocument();
    });
  });

  it('should throw error for email field if you enter invalid email address', async () => {
    const { getByText, getByTestId } = render(<Component {...generalProps} />);
    const input = getByTestId('input-bbGuestDetails[0][emailAddress]');

    await act(async () => {
      userEvent.type(input, 'invalidemail@');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(getByText(validationLabels.emailInvalidError)).toBeInTheDocument();
    });
  });

  it('should not throw error for email field if you enter a valid email address', async () => {
    const { queryByText, getByTestId } = render(<Component {...generalProps} />);
    const input = getByTestId('input-bbGuestDetails[0][emailAddress]');

    await act(async () => {
      userEvent.type(input, 'valid@email.com');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(queryByText(validationLabels.emailInvalidError)).not.toBeInTheDocument();
    });
  });

  it('should not throw error for email field if you enter an empty string', async () => {
    const { queryByText, getByTestId } = render(<Component {...generalProps} />);
    const input = getByTestId('input-bbGuestDetails[0][emailAddress]');

    await act(async () => {
      userEvent.clear(input);
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(queryByText(validationLabels.emailInvalidError)).not.toBeInTheDocument();
    });
  });

  it('should not call on submit if title field is empty', async () => {
    const { getByText, getByTestId } = render(<Component {...generalProps} />);
    const firstNameInput = getByTestId('input-bbGuestDetails[0][firstName]');
    const lastNameInput = getByTestId('input-bbGuestDetails[0][lastName]');

    const submitBtn = getByText('Submit');
    await act(async () => {
      userEvent.type(firstNameInput, 'firstName');
      userEvent.type(lastNameInput, 'lastName');
      userEvent.click(submitBtn);
    });

    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalledTimes(0);
    });
  });
});

describe('Render Form with accompanying guest details', () => {
  // accompanying guest details section
  it('should render Form with accompanying components correctly', async () => {
    const { queryByTestId, getByTestId } = render(
      <Component {...{ ...generalProps, isAccompanyingGuestDetailsEnabled: true }} />
    );
    expect(getByTestId('AccompanyingGuestDetailsBBContainerTest-Form-Room-1')).toBeInTheDocument();
    expect(
      queryByTestId(
        'DropdownComp-AccompanyingGuestDetailsBBContainerTest-Form-TitleDropdown-entireList'
      )
    ).toBeTruthy();
    expect(queryByTestId('input-bbAccompanyingGuestDetails[0][firstName]')).toBeTruthy();
    expect(queryByTestId('input-bbAccompanyingGuestDetails[0][lastName]')).toBeTruthy();
    expect(queryByTestId('input-bbAccompanyingGuestDetails[0][emailAddress]')).toBeTruthy();
  });
  it('should throw error for title field if it is emtpy', async () => {
    const { getByTestId } = render(
      <Component {...{ ...generalProps, isAccompanyingGuestDetailsEnabled: true }} />
    );
    // accompanying guest details section
    await act(async () => {
      fireEvent.click(
        getByTestId(
          'DropdownComp-AccompanyingGuestDetailsBBContainerTest-Form-TitleDropdown-menuButton'
        )
      );
    });
    expect(
      getByTestId(
        'DropdownComp-AccompanyingGuestDetailsBBContainerTest-Form-TitleDropdown-entireList'
      )
    ).toBeInTheDocument();
  });

  it('should throw error for first name field for accompanying guest details', async () => {
    const { getByText, getByTestId } = render(
      <Component {...{ ...generalProps, isAccompanyingGuestDetailsEnabled: true }} />
    );
    const input = getByTestId('input-bbAccompanyingGuestDetails[0][firstName]');

    await act(async () => {
      userEvent.type(input, 'a');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(getByText(validationLabels.firstNameMinError)).toBeInTheDocument();
    });
  });

  it('should throw error for first name field if input is too long (21 chars)', async () => {
    const { getByText, getByTestId } = render(
      <Component {...{ ...generalProps, isAccompanyingGuestDetailsEnabled: true }} />
    );
    const input = getByTestId('input-bbAccompanyingGuestDetails[0][firstName]');

    await act(async () => {
      userEvent.type(input, 'abcdeabcdeabcdeabcdea');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(getByText(validationLabels.firstNameRequiredError)).toBeInTheDocument();
    });
  });

  it('should throw error for first name field if input has 20 characters without spaces before the first letter and after the last letter', async () => {
    const { queryByText, getByTestId } = render(
      <Component {...{ ...generalProps, isAccompanyingGuestDetailsEnabled: true }} />
    );
    const input = getByTestId('input-bbAccompanyingGuestDetails[0][firstName]');

    await act(async () => {
      userEvent.type(input, '   abcdeabcdeabcdeabcde        ');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(queryByText(validationLabels.firstNameRequiredError)).not.toBeInTheDocument();
    });
  });

  it('should throw error for email field if you enter invalid email address', async () => {
    const { getByText, getByTestId } = render(
      <Component {...{ ...generalProps, isAccompanyingGuestDetailsEnabled: true }} />
    );
    const input = getByTestId('input-bbAccompanyingGuestDetails[0][emailAddress]');

    await act(async () => {
      userEvent.type(input, 'invalidemail@');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(getByText(validationLabels.emailInvalidError)).toBeInTheDocument();
    });
  });

  it('should not throw error for email field if you enter a valid email address', async () => {
    const { queryByText, getByTestId } = render(
      <Component {...{ ...generalProps, isAccompanyingGuestDetailsEnabled: true }} />
    );
    const input = getByTestId('input-bbAccompanyingGuestDetails[0][emailAddress]');

    await act(async () => {
      userEvent.type(input, 'valid@email.com');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(queryByText(validationLabels.emailInvalidError)).not.toBeInTheDocument();
    });
  });

  it('should not throw error for email field if you enter an empty string', async () => {
    const { queryByText, getByTestId } = render(
      <Component {...{ ...generalProps, isAccompanyingGuestDetailsEnabled: true }} />
    );
    const input = getByTestId('input-bbAccompanyingGuestDetails[0][emailAddress]');

    await act(async () => {
      userEvent.clear(input);
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(queryByText(validationLabels.emailInvalidError)).not.toBeInTheDocument();
    });
  });
});
