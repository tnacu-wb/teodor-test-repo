import '@testing-library/jest-dom';

import { act, render, userEvent, waitFor } from '../../../../utils/test-utils';
import AccountToCompanyDetails from './AccountToCompanyDetails.component';

const title = 'ccui.accountToCompany.title';
const description = 'ccui.accountToCompany.description';
const search = 'ccui.accountToCompany.search';
const accountNumberPlaceholder = 'ccui.accountToCompany.accountNumber.placeholder';
const companyNamePlaceholder = 'ccui.accountToCompany.companyName.placeholder';
const accountNumberInvalidError = 'ccui.accountToCompany.accountNumber.invalidCharactersError';
const companyNameInvalidError = 'ccui.accountToCompany.companyName.invalidCharactersError';
const accountNumberMinCharactersError = 'ccui.accountToCompany.accountNumber.minCharactersError';
const companyNameMinCharactersError = 'ccui.accountToCompany.companyName.minCharactersError';
const accountNumberMaxCharactersError = 'ccui.accountToCompany.accountNumber.maxCharactersError';
const companyNameMaxCharactersError = 'ccui.accountToCompany.companyName.maxCharactersError';
const fieldsEmptyError = 'ccui.accountToCompany.fieldsEmptyError';
const accountNumberInvalidInput = '123QW()';
const accountNumberValidInput = '12345-123';
const accountNumberLongInput = '12345678912345678912345';
const companyNameInvalidInput = 'qwer<>~';
const companyNameValidInput = 'premier_';
const companyNameLongInput = 'qwerqwer12341234qwerqwer123412341234qwerqwer1234qwer1234';

const companyName1 = 'Premier Aluminium Systems';
const companyName2 = 'Premier Business Audio';
const noResultsError = 'ccui.accountToCompany.search.noResults';
const technicalError = 'ccui.accountToCompany.search.technicalError';

const mockQueryRequest = jest.fn();

const hotelId = 'MANOLD';
const payNow = 'PAY_NOW';
const payOnArrival = 'PAY_ON_ARRIVAL';

const mockQueryRequestResponse = {
  isError: false,
  isFetching: false,
  error: { message: '' },
  data: {
    searchCompanies: {
      companies: [
        {
          name: companyName1,
          address: {
            addressLine1: '4th floor',
            addressLine2: '120 Holborn',
            addressLine3: 'London',
            country: 'UK',
            postalCode: 'EC1N 2TD',
          },
          telephoneNumber: '020 7806 5480',
          corpId: 'XDJ233DNG',
          companyId: '2569623',
          active: true,
          arNumber: '341343',
          profileType: 'Business',
          restricted: true,
          restrictedReason: 'Not cleared',
          language: 'EN',
        },
        {
          name: companyName2,
          address: {
            addressLine1: '4th floor',
            addressLine2: '120 Holborn',
            addressLine3: 'London',
            country: 'UK',
            postalCode: 'EC1N 2TD',
          },
          telephoneNumber: '020 7806 5480',
          corpId: 'XDJ233DNG',
          companyId: '2569624',
          arNumber: '341345',
          active: true,
          profileType: 'Business',
          restricted: false,
          language: 'EN',
        },
      ],
    },
  },
  refetch: jest.fn(),
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockQueryRequest(),
}));

const mockProps = {
  hotelId,
  selectedPaymentDetail: payOnArrival,
  setVerifiedCompany: jest.fn(),
};

const renderComponent = (selectedPaymentDetail?: string) => {
  const overiddenProp = selectedPaymentDetail ? { selectedPaymentDetail } : {};
  return render(<AccountToCompanyDetails {...mockProps} {...overiddenProp} />);
};

describe('<AccountToCompanyDetails />', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
  });

  it('should render the component', async () => {
    const { getByRole, getAllByRole, getByPlaceholderText, getByText } = renderComponent();

    await waitFor(() => {
      expect(getByText(title)).toBeInTheDocument();
      expect(getByText(description)).toBeInTheDocument();
      expect(getAllByRole('textbox')).toHaveLength(2);
      expect(getByPlaceholderText(accountNumberPlaceholder)).toBeInTheDocument();
      expect(getByPlaceholderText(companyNamePlaceholder)).toBeInTheDocument();
      expect(getByRole('button', { name: search })).toBeInTheDocument();
    });
  });

  it('should show an error message for account number field when invalid input is entered', async () => {
    const { getByPlaceholderText, getByText } = renderComponent();

    const accountNumberField = getByPlaceholderText(accountNumberPlaceholder);

    await userEvent.type(accountNumberField, accountNumberInvalidInput, { delay: 0.1 });

    await waitFor(() => {
      expect(getByText(accountNumberInvalidError)).toBeInTheDocument();
    });
  });

  it('should show an error message for account number field when input exceeds character limit', async () => {
    const { getByPlaceholderText, getByText } = renderComponent();

    const accountNumberField = getByPlaceholderText(accountNumberPlaceholder);

    await userEvent.type(accountNumberField, accountNumberLongInput, { delay: 0.1 });

    await waitFor(() => {
      expect(getByText(accountNumberMaxCharactersError)).toBeInTheDocument();
    });
  });

  it('should not show an error message for account number field when valid input is entered', async () => {
    const { getByPlaceholderText, queryByText } = renderComponent();
    const accountNumberField = getByPlaceholderText(accountNumberPlaceholder);

    await userEvent.type(accountNumberField, accountNumberValidInput, { delay: 0.1 });

    await waitFor(() => {
      expect(accountNumberField).not.toBeInvalid();
      expect(queryByText(accountNumberInvalidError)).not.toBeInTheDocument();
      expect(queryByText(accountNumberMaxCharactersError)).not.toBeInTheDocument();
    });
  });

  it('should show an error message for company name field when invalid input is entered', async () => {
    const { getByPlaceholderText, getByText } = renderComponent();

    const companyNameField = getByPlaceholderText(companyNamePlaceholder);

    await userEvent.type(companyNameField, companyNameInvalidInput, { delay: 0.1 });

    await waitFor(() => {
      expect(getByText(companyNameInvalidError)).toBeInTheDocument();
    });
  });

  it('should show an error message for company name field when input exceeds character limit', async () => {
    const { getByPlaceholderText, getByText } = renderComponent();
    const companyNameField = getByPlaceholderText(companyNamePlaceholder);

    await userEvent.type(companyNameField, companyNameLongInput, { delay: 0.1 });

    await waitFor(() => {
      expect(getByText(companyNameMaxCharactersError)).toBeInTheDocument();
    });
  });

  it('should not show an error message for company name field when valid input is entered', async () => {
    const { getByPlaceholderText, queryByText } = renderComponent();

    const companyNameField = getByPlaceholderText(companyNamePlaceholder);

    await userEvent.type(companyNameField, companyNameValidInput, { delay: 0.1 });

    await waitFor(() => {
      expect(companyNameField).not.toBeInvalid();
      expect(queryByText(companyNameInvalidError)).not.toBeInTheDocument();
      expect(queryByText(companyNameMaxCharactersError)).not.toBeInTheDocument();
    });
  });

  it('should not show an error message for company name field when input is cleared', async () => {
    const { getByPlaceholderText, queryByText } = renderComponent();

    const companyNameField = getByPlaceholderText(companyNamePlaceholder);

    await userEvent.type(companyNameField, companyNameInvalidInput, { delay: 0.1 });

    await act(async () => {
      userEvent.clear(companyNameField);
    });

    await waitFor(() => {
      expect(companyNameField).not.toBeInvalid();
      expect(queryByText(companyNameInvalidError)).not.toBeInTheDocument();
    });
  });

  it('should show an error message when search is performed with both the fields are empty', async () => {
    const { getByRole, queryByRole, queryByText } = renderComponent();

    const searchButton = getByRole('button', { name: search });

    await act(async () => {
      userEvent.click(searchButton);
    });

    await waitFor(() => {
      expect(queryByRole('alert')).toBeInTheDocument();
      expect(queryByText(fieldsEmptyError)).toBeInTheDocument();
    });
  });

  it('should show an error message for account number field when search is performed with invalid inpput', async () => {
    const { getByPlaceholderText, getByText, getByRole } = renderComponent();

    const accountNumberField = getByPlaceholderText(accountNumberPlaceholder);

    await userEvent.type(accountNumberField, '1', { delay: 0.1 });

    const searchButton = getByRole('button', { name: search });

    await act(async () => {
      userEvent.click(searchButton);
    });

    await waitFor(() => {
      expect(getByText(accountNumberMinCharactersError)).toBeInTheDocument();
    });
  });

  it('should show an error message for company name field when search is performed with invalid input', async () => {
    const { getByPlaceholderText, getByText, getByRole } = renderComponent();

    const companyNameField = getByPlaceholderText(companyNamePlaceholder);

    await userEvent.type(companyNameField, 'P', { delay: 0.1 });

    const searchButton = getByRole('button', { name: search });

    await act(() => {
      userEvent.click(searchButton);
    });

    await waitFor(() => {
      expect(getByText(companyNameMinCharactersError)).toBeInTheDocument();
    });
  });

  it('should disable Search CTA when selected payment detail is not PAY_ON_ARRIVAL', async () => {
    const { getByRole } = renderComponent(payNow);

    const searchButton = getByRole('button', { name: search });

    await waitFor(() => {
      expect(searchButton).toBeDisabled();
    });
  });

  it('should enable Search CTA when selected payment detail is PAY_ON_ARRIVAL', async () => {
    const { getByRole } = renderComponent(payOnArrival);

    const searchButton = getByRole('button', { name: search });

    await waitFor(() => {
      expect(searchButton).not.toBeDisabled();
    });
  });

  it('should not show any error message when query to fetch companies is successful', async () => {
    const { getByPlaceholderText, getByRole, queryByRole, queryByText } =
      renderComponent(payOnArrival);

    const companyNameField = getByPlaceholderText(companyNamePlaceholder);

    await userEvent.type(companyNameField, companyNameValidInput, { delay: 0.1 });

    const searchButton = getByRole('button', { name: search });

    await act(async () => {
      userEvent.click(searchButton);
    });

    await waitFor(() => {
      expect(queryByRole('alert')).not.toBeInTheDocument();
      expect(queryByText(noResultsError)).not.toBeInTheDocument();
      expect(queryByText(technicalError)).not.toBeInTheDocument();
    });
  });

  it('should show no results error message when query to fetch companies return no matching companies', async () => {
    const emptyData = {
      searchCompanies: {
        companies: [],
      },
    };
    mockQueryRequest.mockReturnValue({ ...mockQueryRequestResponse, data: emptyData });

    const { getByPlaceholderText, getByRole, queryByRole, queryByText } =
      renderComponent(payOnArrival);

    const companyNameField = getByPlaceholderText(companyNamePlaceholder);

    await userEvent.type(companyNameField, companyNameValidInput, { delay: 0.1 });

    const searchButton = getByRole('button', { name: search });

    await act(async () => {
      userEvent.click(searchButton);
    });

    await waitFor(() => {
      expect(queryByRole('alert')).toBeInTheDocument();
      expect(queryByText(noResultsError)).toBeInTheDocument();
      expect(queryByText(technicalError)).not.toBeInTheDocument();
    });
  });

  it('should show technical error message when query to fetch companies fails due to a network issue', async () => {
    mockQueryRequest.mockReturnValue({ ...mockQueryRequestResponse, isError: true });
    const { getByPlaceholderText, getByRole, queryByRole, queryByText } =
      renderComponent(payOnArrival);

    const companyNameField = getByPlaceholderText(companyNamePlaceholder);

    await userEvent.type(companyNameField, companyNameValidInput, { delay: 0.1 });

    const searchButton = getByRole('button', { name: search });

    await act(async () => {
      userEvent.click(searchButton);
    });

    await waitFor(() => {
      expect(queryByRole('alert')).toBeInTheDocument();
      expect(queryByText(noResultsError)).not.toBeInTheDocument();
      expect(queryByText(technicalError)).toBeInTheDocument();
    });
  });
});
