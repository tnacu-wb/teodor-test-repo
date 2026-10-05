import '@testing-library/jest-dom';
import React from 'react';

import { render, userEvent } from '../../../utils/test-utils';
import { FORM_BUTTON_TYPES } from '.././';
import { FormButtonsProps } from '../formTypes';
import FormButtons from './FormButtons.component';

describe('Form Buttons', () => {
  it('should render the component', () => {
    const props: FormButtonsProps = {
      buttonsContainerStyles: undefined,
      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: 'Submit',
          action: jest.fn(),
          styles: {
            color: 'blue',
          },
          props: {
            variant: 'primary',
            size: 'sm',
          },
        },
      ],
    };

    const { getByText } = render(<FormButtons {...props} />);
    const text = getByText(/submit/i, { selector: 'button' });
    expect(text).toBeInTheDocument();
  });

  it('should render multiple buttons if given', () => {
    const props: FormButtonsProps = {
      buttonsContainerStyles: undefined,
      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: 'Submit',
          action: jest.fn(),
          styles: {
            color: 'blue',
          },
          props: {
            variant: 'primary',
            size: 'sm',
          },
        },
        {
          type: FORM_BUTTON_TYPES.BUTTON,
          label: 'Standard',
          action: jest.fn(),
          styles: {
            color: 'red',
          },
          props: {
            variant: 'primary',
            size: 'sm',
          },
        },
        {
          type: FORM_BUTTON_TYPES.RESET,
          label: 'Reset',
          action: jest.fn(),
          styles: {
            color: 'green',
          },
          props: {
            variant: 'primary',
            size: 'sm',
          },
        },
      ],
    };

    const { getAllByRole } = render(<FormButtons {...props} />);
    const buttons = getAllByRole('button');
    expect(buttons).toHaveLength(3);
  });

  it('the button should not call the submit, it should be handled by the form', async () => {
    const onSubmit = jest.fn();

    const props: FormButtonsProps = {
      buttonsContainerStyles: undefined,
      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: 'Submit',
          action: onSubmit,
          styles: {
            color: 'blue',
          },
          props: {
            variant: 'primary',
            size: 'sm',
          },
        },
      ],
    };

    const { getByText } = render(<FormButtons {...props} />);
    const submitButton = getByText('Submit', { selector: 'button' });
    expect(submitButton).toBeInTheDocument();

    await userEvent.click(submitButton);
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('should trigger the reset', async () => {
    const onSubmit = jest.fn();
    const onReset = jest.fn();

    const props: FormButtonsProps = {
      buttonsContainerStyles: undefined,
      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: 'Submit',
          action: onSubmit,
          styles: {
            color: 'blue',
          },
          props: {
            variant: 'primary',
            size: 'sm',
          },
        },
        {
          type: FORM_BUTTON_TYPES.RESET,
          label: 'Reset',
          action: onReset,
          styles: {
            color: 'red',
          },
          props: {
            variant: 'primary',
            size: 'sm',
          },
        },
      ],
    };

    const { getByText } = render(<FormButtons {...props} />);
    const resetButton = getByText('Reset', { selector: 'button' });
    expect(resetButton).toBeInTheDocument();

    await userEvent.click(resetButton);
    expect(onReset).toHaveBeenCalledTimes(1);
    expect(onSubmit).toBeCalledTimes(0);
  });
});
