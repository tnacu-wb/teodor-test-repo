import '@testing-library/jest-dom';
import { render, fireEvent } from '@testing-library/react';

import { FormButtonsProps } from '../formTypes';
import FormButton from './FormButton.component';

jest.mock('@whitbread-eos/utils/restaurants', () => ({
  useAppData: jest.fn(),
}));

const mockUseQueryRequest = jest.requireMock('@whitbread-eos/utils/restaurants');
const mockAction = jest.fn();

const props: FormButtonsProps = {
  formStepOneCompleted: false,
  isEnquiry: false,
  buttons: [
    {
      type: 'button',
      label: ['Continue', 'Send Booking Enquiry', 'Book'],
      testid: 'button-test-id',
      props: {
        variant: 'primary',
        size: 'sm',
      },
      action: mockAction,
    },
  ],
};

const renderFormButton = (props: FormButtonsProps) => {
  const { getByTestId } = render(<FormButton {...props} />);
  return {
    button: getByTestId('button-test-id'),
  };
};

const renderAndClickButton = (props: FormButtonsProps, restaurantBrandName: string) => {
  mockUseQueryRequest.useAppData.mockReturnValue({ restaurantBrandName });

  const { button } = renderFormButton({
    ...props,
    buttons: [{ ...props.buttons[0], type: 'submit' }],
    formStepOneCompleted: true,
    isEnquiry: true,
  });

  expect(button).toBeInTheDocument();
  expect(button).toHaveTextContent('Send Booking Enquiry');

  fireEvent.click(button);
  expect(mockAction).toHaveBeenCalled();
};

describe('Form Buttons Component', () => {
  it('renders buttons with different labels when formStepOneCompleted is true and isEnquiry false', () => {
    const updatedProps = {
      ...props,
      formStepOneCompleted: true,
      isEnquiry: false,
    };
    const { button } = renderFormButton(updatedProps);
    expect(button).toBeInTheDocument();
    expect(button).toHaveTextContent('Book');
  });

  it('invokes action when type is not FORM_BUTTON_TYPES.SUBMIT', () => {
    const updatedProps: FormButtonsProps = {
      ...props,
      buttons: [
        {
          ...props.buttons[0],
          type: 'reset',
        },
      ],
    };
    const { button } = renderFormButton(updatedProps);
    fireEvent.click(button);
    expect(mockAction).toHaveBeenCalled();
  });

  it('renders buttons with correct labels and invokes action on click', () => {
    const { button } = renderFormButton(props);
    expect(button).toBeInTheDocument();
    expect(button).toHaveTextContent('Continue');
    fireEvent.click(button);
    expect(mockAction).toHaveBeenCalled();
  });

  it('renders buttons with correct labels and invokes action on click for final submission of enquiry for enquiry with whitbreadinns', () => {
    renderAndClickButton(props, 'whitbreadinns');
  });

  it('renders buttons with correct labels and invokes action on click for final submission of enquiry for enquiry with other brands', () => {
    renderAndClickButton(props, 'beefeater');
  });
});
