import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { Form } from '@whitbread-eos/atoms';

import { fireEvent, render, userEvent, waitFor, act } from '../../utils/test-utils';
import { BBLeadGuestDetailsFormConfig } from './BBLeadGuestDetailsFormConfig';

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
  guestTitle: 'guestTitle',
};
const t = jest.fn();
const numberOfRooms = 1;

const Component = () => {
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
  };

  const baseTestId = 'GuestDetailsBBContainerTest';

  return (
    <QueryClientProvider client={new QueryClient()}>
      <Form
        data-testid={'Form'}
        {...BBLeadGuestDetailsFormConfig({
          isEdit: false,
          guestList: {
            bbGuestDetails: [],
          },
          queryClient: new QueryClient(),
          setGuestUser: jest.fn(),
          getFormState,
          defaultValues,
          onSubmit,
          baseTestId,
          t,
          labels,
          validationLabels,
          numberOfRooms,
          isDynamicSearchVisible: false,
        })}
      />
    </QueryClientProvider>
  );
};

describe('Render Form', () => {
  it('should render Form with all components correctly', async () => {
    const { queryByTestId } = render(<Component />);
    expect(queryByTestId('GuestDetailsBBContainerTest-Form')).toBeTruthy();
    expect(queryByTestId('GuestDetailsBBContainerTest-Form-Container')).toBeTruthy();
    expect(
      queryByTestId('DropdownComp-GuestDetailsBBContainerTest-Form-TitleDropdown-entireList')
    ).toBeTruthy();
    expect(queryByTestId('input-bbGuestDetails[0][firstName]')).toBeTruthy();
    expect(queryByTestId('input-bbGuestDetails[0][lastName]')).toBeTruthy();
    expect(queryByTestId('input-bbGuestDetails[0][emailAddress]')).toBeTruthy();
  });

  it('should throw error for title field if it is emtpy', async () => {
    const { getByTestId } = render(<Component />);

    await act(async () => {
      fireEvent.click(
        getByTestId('DropdownComp-GuestDetailsBBContainerTest-Form-TitleDropdown-menuButton')
      );
    });

    expect(
      getByTestId('DropdownComp-GuestDetailsBBContainerTest-Form-TitleDropdown-entireList')
    ).toBeInTheDocument();
  });

  it('should throw error for first name field if character is not valid', async () => {
    const { getByText, getByTestId } = render(<Component />);
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
    const { getByText, getByTestId } = render(<Component />);
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
    const { getByText, getByTestId } = render(<Component />);
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
    const { getByText, getByTestId } = render(<Component />);
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
    const { queryByText, getByTestId } = render(<Component />);
    const input = getByTestId('input-bbGuestDetails[0][firstName]');

    await act(async () => {
      userEvent.type(input, '   abcdeabcdeabcdeabcde        ');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(queryByText(validationLabels.firstNameRequiredError)).toBeInTheDocument();
    });
  });

  it('should throw error for last name field', async () => {
    const { getByText, getByTestId } = render(<Component />);
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
    const { queryByText, getByTestId } = render(<Component />);
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
    const { getByText, getByTestId } = render(<Component />);
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
    const { queryByText, getByTestId } = render(<Component />);
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
    const { queryByText, getByTestId } = render(<Component />);
    const input = getByTestId('input-bbGuestDetails[0][emailAddress]');

    await act(async () => {
      userEvent.clear(input);
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(queryByText(validationLabels.emailInvalidError)).not.toBeInTheDocument();
    });
  });
});
