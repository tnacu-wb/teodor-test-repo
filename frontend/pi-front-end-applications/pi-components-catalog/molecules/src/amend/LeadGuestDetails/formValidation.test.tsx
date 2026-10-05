import '@testing-library/jest-dom';
import { Button, Form } from '@whitbread-eos/atoms';

import { render, userEvent, waitFor } from '../../utils/test-utils';
import { leadGuestFormConfig } from './leadGuestFormConfig';

const getFormState = jest.fn();
const onSubmit = jest.fn();
const setGuestDetails = jest.fn();
const setIsLocationRequired = jest.fn();
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
  emailAddress: 'EmailLabel',
  guestTitle: '',
  email: '',
  addressLine1: '',
  addressLine2: '',
  addressLine3: '',
  postalCode: '',
  city: '',
  country: '',
};

const Component = () => {
  const defaultValues = {
    title: '',
    firstName: '',
    lastName: '',
    emailAddress: '',
    addressLine1: '',
    addressLine2: '',
    addressLine3: '',
    postalCode: '',
    city: '',
    country: '',
  };

  const baseTestId = 'amend';

  return (
    <>
      <Form
        data-testid={'Form'}
        {...leadGuestFormConfig({
          getFormState,
          defaultValues,
          onSubmit,
          baseTestId,
          labels,
          validationLabels,
          isEdit: false,
          setGuestDetails,
          setIsLocationRequired,
        })}
      />
      <Button
        size="full"
        variant="secondary"
        data-testid="add-room-button"
        form="leadGuestDetailsForm"
        type="submit"
      >
        Add a room
      </Button>
    </>
  );
};

describe('Render Form', () => {
  it('should render Form with all components correctly', async () => {
    const { queryByTestId } = render(<Component />);
    expect(queryByTestId('amend-Form')).toBeTruthy();
    expect(queryByTestId('DropdownComp-amend-Form-titleDropdown-menuButton')).toBeTruthy();
    expect(queryByTestId('input-firstName')).toBeTruthy();
    expect(queryByTestId('input-lastName')).toBeTruthy();
    expect(queryByTestId('input-emailAddress')).toBeTruthy();
  });

  it('should throw error for title field if it is emtpy', async () => {
    const { getByTestId } = render(<Component />);
    userEvent.click(getByTestId('DropdownComp-amend-Form-titleDropdown-menuButton'));
    expect(getByTestId('DropdownComp-amend-Form-titleDropdown-entireList')).toBeInTheDocument();
  });

  it('should throw error for first name field if character is not valid', async () => {
    const { getByText, getByTestId } = render(<Component />);
    const input = getByTestId('input-firstName');
    userEvent.type(input, '1');
    userEvent.tab();
    await waitFor(() => {
      expect(getByText(validationLabels.firstNameInvalidError)).toBeInTheDocument();
    });
  });

  it('should throw error for first name field if input is empty', async () => {
    const { getByText, getByTestId } = render(<Component />);
    const input = getByTestId('input-firstName');
    userEvent.type(input, ' ');
    userEvent.tab();
    await waitFor(() => {
      expect(getByText(validationLabels.firstNameRequiredError)).toBeInTheDocument();
    });
  });

  it('should throw error for first name field', async () => {
    const { getByText, getByTestId } = render(<Component />);
    const input = getByTestId('input-firstName');
    userEvent.type(input, 'a');
    userEvent.tab();
    await waitFor(() => {
      expect(getByText(validationLabels.firstNameMinError)).toBeInTheDocument();
    });
  });

  it('should throw error for first name field if input is too long (21 chars)', async () => {
    const { getByText, getByTestId } = render(<Component />);
    const input = getByTestId('input-firstName');
    userEvent.type(input, 'abcdeabcdeabcdeabcdea');
    userEvent.tab();
    await waitFor(() => {
      expect(getByText(validationLabels.firstNameRequiredError)).toBeInTheDocument();
    });
  });

  it('should not throw error for first name field if input has 20 characters without spaces before the first letter and after the last letter', async () => {
    const { queryByText, getByTestId } = render(<Component />);
    const input = getByTestId('input-firstName');
    userEvent.type(input, '   abcdeabcdeabcdeabcde        ');
    userEvent.tab();
    await waitFor(() => {
      expect(queryByText(validationLabels.firstNameRequiredError)).not.toBeInTheDocument();
    });
  });

  it('should throw error for last name field', async () => {
    const { getByText, getByTestId } = render(<Component />);
    const input = getByTestId('input-lastName');
    userEvent.type(input, '2');
    userEvent.tab();
    await waitFor(() => {
      expect(getByText(validationLabels.lastNameInvalidError)).toBeInTheDocument();
    });
  });

  it('should not throw error for last name field if data has minimum 1 letter', async () => {
    const { queryByText, getByTestId } = render(<Component />);
    const input = getByTestId('input-lastName');
    userEvent.type(input, 'a');
    userEvent.tab();
    await waitFor(() => {
      expect(queryByText(validationLabels.lastNameInvalidError)).not.toBeInTheDocument();
    });
  });

  it('should throw error for email field if you enter invalid email address', async () => {
    const { getByText, getByTestId } = render(<Component />);
    const input = getByTestId('input-emailAddress');
    userEvent.type(input, 'invalidemail@');
    userEvent.tab();
    await waitFor(() => {
      expect(getByText(validationLabels.emailInvalidError)).toBeInTheDocument();
    });
  });

  it('should not throw error for email field if you enter a valid email address', async () => {
    const { queryByText, getByTestId } = render(<Component />);
    const input = getByTestId('input-emailAddress');
    userEvent.type(input, 'valid@email.com');
    userEvent.tab();
    await waitFor(() => {
      expect(queryByText(validationLabels.emailInvalidError)).not.toBeInTheDocument();
    });
  });

  it('should not throw error for email field if you enter an empty string', async () => {
    const { queryByText, getByTestId } = render(<Component />);
    const input = getByTestId('input-emailAddress');
    userEvent.type(input, ' ');
    userEvent.tab();
    await waitFor(() => {
      expect(queryByText(validationLabels.emailInvalidError)).not.toBeInTheDocument();
    });
  });

  it('should not call on submit if title field is empty', async () => {
    const { getByTestId } = render(<Component />);
    const firstNameInput = getByTestId('input-firstName');
    const lastNameInput = getByTestId('input-lastName');

    const submitBtn = getByTestId('add-room-button');
    await waitFor(() => {
      userEvent.type(firstNameInput, 'firstName');
      userEvent.type(lastNameInput, 'lastName');
      userEvent.click(submitBtn);
    });

    expect(onSubmit).toHaveBeenCalledTimes(0);
  });
});
