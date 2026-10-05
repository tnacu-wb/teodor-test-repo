import '@testing-library/jest-dom';
import React from 'react';

import { act, render, userEvent, waitFor } from '../../../../utils/test-utils';
import AccountToCompanyPreAuthorisedCharges from './AccountToCompanyPreAuthorisedCharges.component';

const title = 'ccui.accountToCompanyCharges.title';
const companyreferenceTitle = 'ccui.accountToCompanyReference.title';
const companyreferenceDescription = 'ccui.accountToCompanyReference.description';
const companyreferencePlaceholder = 'ccui.accountToCompanyReference.placeholder';
const companyreferenceMaxCharactersError = 'ccui.accountToCompanyReference.maxCharactersError';
const companyreferenceInvalidCharactersError =
  'ccui.accountToCompanyReference.invalidCharactersError';
const companyReferenceInvalidInput = 'asdM<>';
const companyReferenceLongInput = 'ccui.accountToCompanyCharges.title.accountToCompanyCharges';
const companyReferenceValidInput = 'asd';

const mocksetPreAuthorisedCharges = jest.fn();
const mockSetCompanyReferenceError = jest.fn();
const mockSetACCompanyReference = jest.fn();

const initCharges = [
  { id: 1, label: 'premierInnBreakfast', key: 'ccui.accountToCompanyCharges.option1' },
  { id: 2, label: 'carParking', key: 'ccui.accountToCompanyCharges.option2' },
];

const props = {
  setPreAuthorisedCharges: mocksetPreAuthorisedCharges,
  setCompanyReferenceError: mockSetCompanyReferenceError,
  setACCompanyReference: mockSetACCompanyReference,
  initialCharges: initCharges,
  initialCompanyReference: null,
  isFromChangePaymentBIC: false,
};

describe('<AccountToCompanyPreAuthorisedCharges />', () => {
  beforeAll(() => {
    jest.clearAllMocks();
  });
  afterAll(() => {
    jest.resetAllMocks();
  });
  it('should render the section title', async () => {
    const { getByText } = render(<AccountToCompanyPreAuthorisedCharges {...props} />);

    await waitFor(() => {
      expect(getByText(title)).toBeInTheDocument();
    });
  });

  it('should render the options', async () => {
    const { getByTestId } = render(<AccountToCompanyPreAuthorisedCharges {...props} />);

    const option1 = getByTestId(`AccountToCompanyPreAuthorisedCharges-option-1`);

    await waitFor(() => {
      expect(option1).toBeInTheDocument();
      expect(option1).not.toBeChecked();
    });
  });

  it('should not render the options if isFromChangePaymentBIC is true', async () => {
    const { getByText } = render(
      <AccountToCompanyPreAuthorisedCharges {...{ ...props, isFromChangePaymentBIC: true }} />
    );

    await waitFor(() => {
      expect(getByText(companyreferenceTitle)).toBeInTheDocument();
    });
  });
  it('should trigger change on checkbox click', async () => {
    const { getByTestId } = render(<AccountToCompanyPreAuthorisedCharges {...props} />);

    const option1Label = getByTestId(`AccountToCompanyPreAuthorisedCharges-option-1-item`);

    await waitFor(() => {
      expect(option1Label).toBeInTheDocument();
    });

    await act(async () => {
      userEvent.click(option1Label);
    });

    await waitFor(() => {
      expect(mocksetPreAuthorisedCharges).toBeCalled();
    });
  });

  it('should render the company reference section section title', async () => {
    const { getByText, getByPlaceholderText } = render(
      <AccountToCompanyPreAuthorisedCharges {...props} />
    );

    await waitFor(() => {
      expect(getByText(companyreferenceTitle)).toBeInTheDocument();
      expect(getByText(companyreferenceDescription)).toBeInTheDocument();
      expect(getByPlaceholderText(companyreferencePlaceholder)).toBeInTheDocument();
    });
  });

  it('should enable the company reference input when no charges is selected', async () => {
    const { getByPlaceholderText } = render(<AccountToCompanyPreAuthorisedCharges {...props} />);

    await waitFor(() => {
      expect(getByPlaceholderText(companyreferencePlaceholder)).toBeInTheDocument();
      expect(getByPlaceholderText(companyreferencePlaceholder)).not.toBeDisabled();
    });
  });

  it('should enable the company reference input when any charges is selected', async () => {
    const { getByPlaceholderText, getByTestId, rerender } = render(
      <AccountToCompanyPreAuthorisedCharges {...props} />
    );

    const option1Label = getByTestId(`AccountToCompanyPreAuthorisedCharges-option-1-item`);

    await waitFor(() => {
      expect(option1Label).toBeInTheDocument();
    });

    await act(async () => {
      userEvent.click(option1Label);
    });

    rerender(<AccountToCompanyPreAuthorisedCharges {...props} />);

    await waitFor(() => {
      expect(getByPlaceholderText(companyreferencePlaceholder)).not.toBeDisabled();
    });
  });

  it('should display an error message when invalid input is entered in company reference field', async () => {
    const { getByPlaceholderText, getByTestId, queryByText, rerender } = render(
      <AccountToCompanyPreAuthorisedCharges {...props} />
    );

    const option1Label = getByTestId(`AccountToCompanyPreAuthorisedCharges-option-1-item`);

    await waitFor(() => {
      expect(option1Label).toBeInTheDocument();
    });

    await act(async () => {
      userEvent.click(option1Label);
    });

    await waitFor(() => {
      rerender(<AccountToCompanyPreAuthorisedCharges {...props} />);
    });

    const companyReference = getByPlaceholderText(companyreferencePlaceholder);

    await waitFor(() => {
      expect(companyReference).not.toBeDisabled();
    });

    await act(async () => {
      userEvent.type(companyReference, companyReferenceInvalidInput, { delay: 0.1 });
    });

    await waitFor(() => {
      expect(queryByText(companyreferenceInvalidCharactersError)).toBeInTheDocument();
    });
  });

  it('should not display an error message when correct input is entered in company reference field', async () => {
    const { getByPlaceholderText, getByTestId, queryByText, rerender } = render(
      <AccountToCompanyPreAuthorisedCharges {...props} />
    );

    const option1Label = getByTestId(`AccountToCompanyPreAuthorisedCharges-option-1-item`);

    await waitFor(() => {
      expect(option1Label).toBeInTheDocument();
    });

    await act(async () => {
      userEvent.click(option1Label);
    });

    rerender(<AccountToCompanyPreAuthorisedCharges {...props} />);

    const companyReference = getByPlaceholderText(companyreferencePlaceholder);

    await waitFor(() => {
      expect(companyReference).not.toBeDisabled();
    });
    await act(async () => {
      userEvent.type(companyReference, companyReferenceValidInput, { delay: 0.1 });
    });

    await waitFor(() => {
      expect(companyReference).not.toBeInvalid();
      expect(mockSetCompanyReferenceError).lastCalledWith(false);
      expect(queryByText(companyreferenceInvalidCharactersError)).not.toBeInTheDocument();
      expect(queryByText(companyreferenceMaxCharactersError)).not.toBeInTheDocument();
    });
  });

  it('should display an error message when long input is entered in company reference field', async () => {
    const { getByPlaceholderText, getByTestId, queryByText, rerender } = render(
      <AccountToCompanyPreAuthorisedCharges {...props} />
    );

    const option1Label = getByTestId(`AccountToCompanyPreAuthorisedCharges-option-1-item`);

    await waitFor(() => {
      expect(option1Label).toBeInTheDocument();
    });

    await act(async () => {
      userEvent.click(option1Label);
    });

    await waitFor(() => {
      rerender(<AccountToCompanyPreAuthorisedCharges {...props} />);
    });

    const companyReference = getByPlaceholderText(companyreferencePlaceholder);

    await waitFor(() => {
      expect(companyReference).not.toBeDisabled();
    });

    await userEvent.type(companyReference, companyReferenceLongInput, { delay: 0.5 });

    await waitFor(() => {
      expect(queryByText(companyreferenceMaxCharactersError)).toBeInTheDocument();
    });
  });

  it('should call setter when input is entered in company reference field', async () => {
    const { getByPlaceholderText, getByTestId, rerender } = render(
      <AccountToCompanyPreAuthorisedCharges {...props} />
    );

    const option1Label = getByTestId(`AccountToCompanyPreAuthorisedCharges-option-1-item`);

    await waitFor(() => {
      expect(option1Label).toBeInTheDocument();
    });

    await act(async () => {
      userEvent.click(option1Label);
    });

    rerender(<AccountToCompanyPreAuthorisedCharges {...props} />);

    const companyReference = getByPlaceholderText(companyreferencePlaceholder);

    await waitFor(() => {
      expect(companyReference).not.toBeDisabled();
    });

    await act(async () => {
      userEvent.type(companyReference, companyReferenceValidInput, { delay: 0.1 });
    });

    await waitFor(() => {
      expect(mockSetACCompanyReference).toHaveBeenCalledWith(companyReferenceValidInput);
    });
  });
});
