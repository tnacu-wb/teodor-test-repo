import '@testing-library/jest-dom';
import preloadAll from 'jest-next-dynamic';
import React from 'react';

import { act, render, userEvent, waitFor } from '../../utils/test-utils';
import AccountToCompany, { Props as ComponentProps } from './AccountToCompany.component';
import AccountToCompanyContainer, { Props } from './AccountToCompany.container';

const mockHotelId = 'MANOLD';
const mockSelectedpaymentDetail = 'PAY_ON_ARRIVAL';
const mockSetIsTotalCostVisible = jest.fn();
const mockSetCompanyReferenceError = jest.fn();
const mockSetPreAuthorisedCharges = jest.fn();
const mockSetCompanyNumber = jest.fn();
const mockSetCompanyId = jest.fn();
const mockSetCompanyDetails = jest.fn();
const mockSetACCompanyReference = jest.fn();
const search = 'ccui.accountToCompany.search';
const accountNumberPlaceholder = 'ccui.accountToCompany.accountNumber.placeholder';
const accountNumberValidInput = '167003';
const companyName1 = 'Premier Aluminium Systems';
const companyName2 = 'Premier Business Audio';

const mockQueryRequest = jest.fn();

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
          restricted: false,
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
          restricted: true,
          restrictedReason: 'Not cleared',
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

const containerProps: Props = {
  hotelId: mockHotelId,
  selectedPaymentDetail: mockSelectedpaymentDetail,
  setIsTotalCostVisible: mockSetIsTotalCostVisible,
  setACCharges: mockSetPreAuthorisedCharges,
  setCompanyReferenceError: mockSetCompanyReferenceError,
  setCompanyNumber: mockSetCompanyNumber,
  setCompanyId: mockSetCompanyId,
  setCompanyDetails: mockSetCompanyDetails,
  setACCompanyReference: mockSetACCompanyReference,
};

const props: ComponentProps = {
  hotelId: mockHotelId,
  selectedPaymentDetail: mockSelectedpaymentDetail,
  setPreAuthorisedCharges: mockSetPreAuthorisedCharges,
  setIsTotalCostVisible: mockSetIsTotalCostVisible,
  setCompanyReferenceError: mockSetCompanyReferenceError,
  setCompanyNumber: mockSetCompanyNumber,
  setCompanyId: mockSetCompanyId,
  setCompanyDetails: mockSetCompanyDetails,
  setACCompanyReference: mockSetACCompanyReference,
};

describe('<AccountToCompanyContainer />', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
  });
  beforeAll(async () => {
    await preloadAll();
  });

  it('should render the container', async () => {
    const { getByTestId } = render(<AccountToCompanyContainer {...containerProps} />);

    await waitFor(() => {
      expect(getByTestId('accountToCompanyContainer')).toBeInTheDocument();
    });
  });

  it('should trigger change on checkbox click', async () => {
    const { getByTestId, getByPlaceholderText, getByRole, getAllByRole } = render(
      <AccountToCompanyContainer {...containerProps} />
    );

    const accountNumberField = getByPlaceholderText(accountNumberPlaceholder);

    await userEvent.type(accountNumberField, accountNumberValidInput, { delay: 0.1 });

    const searchButton = getByRole('button', { name: search });

    await act(async () => {
      userEvent.click(searchButton);
    });

    await waitFor(() => {
      expect(getByTestId('CompanySelection-ModalContent')).toBeInTheDocument();
      const firstCompany = getAllByRole('row')[1];
      userEvent.click(firstCompany);
    });

    const verifyCta = getByTestId('CompanySelection-ModalVerifyButton');

    await waitFor(() => {
      expect(verifyCta).toBeInTheDocument();
    });

    await act(async () => {
      userEvent.click(verifyCta);
    });

    const option1Label = getByTestId('AccountToCompanyPreAuthorisedCharges-option-1-item');

    await waitFor(() => {
      expect(option1Label).toBeInTheDocument();
    });

    await act(async () => {
      userEvent.click(option1Label);
    });

    await waitFor(() => {
      expect(mockSetPreAuthorisedCharges).toBeCalled();
    });
  });

  it('should render the search fields when there is no verified company', () => {
    const { getByTestId } = render(<AccountToCompany {...props} />);

    expect(getByTestId('AccountToCompanyDetails')).toBeInTheDocument();
  });

  it('should render the account information and pre authorized charge fields when there is verified company', async () => {
    const { getByTestId, getByPlaceholderText, getByRole, getAllByRole, queryByTestId } = render(
      <AccountToCompany {...props} />
    );

    const accountNumberField = getByPlaceholderText(accountNumberPlaceholder);

    await userEvent.type(accountNumberField, accountNumberValidInput, { delay: 0.1 });

    const searchButton = getByRole('button', { name: search });

    await act(async () => {
      userEvent.click(searchButton);
    });

    await waitFor(() => {
      expect(getByTestId('CompanySelection-ModalContent')).toBeInTheDocument();
      const firstCompany = getAllByRole('row')[1];
      userEvent.click(firstCompany);
    });

    const verifyCta = getByTestId('CompanySelection-ModalVerifyButton');

    await waitFor(() => {
      expect(verifyCta).toBeInTheDocument();
    });

    await act(async () => {
      userEvent.click(verifyCta);
    });

    await waitFor(() => {
      expect(queryByTestId('AccountToCompanyDetails')).not.toBeInTheDocument();
      expect(queryByTestId('AccountToCompanyFields')).toBeInTheDocument();
      expect(queryByTestId('AccountToCompanyPreAuthorisedCharges')).toBeInTheDocument();
    });
  });

  it('should call the setters when there is a verified company', async () => {
    const { getByTestId, getByPlaceholderText, getByRole, getAllByRole } = render(
      <AccountToCompany {...props} />
    );

    const accountNumberField = getByPlaceholderText(accountNumberPlaceholder);

    await userEvent.type(accountNumberField, accountNumberValidInput, { delay: 0.1 });

    const searchButton = getByRole('button', { name: search });

    await act(async () => {
      userEvent.click(searchButton);
    });

    await waitFor(() => {
      expect(getByTestId('CompanySelection-ModalContent')).toBeInTheDocument();
      const firstCompany = getAllByRole('row')[1];
      userEvent.click(firstCompany);
    });

    await waitFor(() => {
      const verifyCta = getByTestId('CompanySelection-ModalVerifyButton');
      expect(verifyCta).toBeInTheDocument();
      userEvent.click(verifyCta);
    });

    await waitFor(() => {
      expect(mockSetCompanyNumber).toHaveBeenCalledWith('341343');
      expect(mockSetCompanyId).toHaveBeenCalledWith('2569623');
    });
  });
});
