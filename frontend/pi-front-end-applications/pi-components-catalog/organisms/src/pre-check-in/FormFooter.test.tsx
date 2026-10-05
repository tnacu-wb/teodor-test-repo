import '@testing-library/jest-dom';
import React from 'react';

import { render, userEvent } from '../utils/test-utils';
import FormFooter, { dispatchFormSubmitEvent } from './FormFooter';

enum SUBMIT_TYPE {
  SAVE = 'save',
  SUBMIT = 'submit',
}

describe('PreRegister Booking details PI footer', () => {
  it('should render Pre register page footer skeleton', () => {
    const { getByTestId } = render(<FormFooter setSubmitType={jest.fn()} />);

    expect(getByTestId('reg-form-submit-btn')).toBeInTheDocument();
  });
});
describe('handleFormSubmit', () => {
  it('should set the submit type and dispatch form submit event', () => {
    const setSubmitTypeMock = jest.fn();
    const dispatchEventMock = jest.fn();

    document.getElementById = jest.fn().mockReturnValueOnce({
      dispatchEvent: dispatchEventMock,
    });

    const { getByTestId } = render(<FormFooter setSubmitType={setSubmitTypeMock} isGerman />);

    userEvent.click(getByTestId('submit-reg-form'));
    expect(setSubmitTypeMock).toHaveBeenCalledWith(SUBMIT_TYPE.SAVE);

    userEvent.click(getByTestId('reg-form-submit-btn'));
    expect(setSubmitTypeMock).toHaveBeenCalledWith(SUBMIT_TYPE.SUBMIT);
  });

  it('should not dispatch the submit event if the form element is not found', () => {
    document.getElementById = jest.fn().mockReturnValueOnce(null);

    const dispatchEventMock = jest.fn();
    document.dispatchEvent = dispatchEventMock;

    dispatchFormSubmitEvent();

    expect(dispatchEventMock).not.toHaveBeenCalled();
  });
});
