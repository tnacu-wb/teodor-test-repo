import '@testing-library/jest-dom';
import { Company } from '@whitbread-eos/api';
import React, { Dispatch, SetStateAction } from 'react';

import { fireEvent, render, userEvent, waitFor } from '../../utils/test-utils';
import CompanySearch from './CompanySearch.component';

const shortInput = 'm';
const longInput = 'asdfqwerasdfqwerasdfqwerasdfqwerasdfqwerasdfqwerasdfqwer';
const invalidInput = '<abc>[123]';
const validInput = 'abc123()+-&"£$?_!:*%';
const validInputUpdated = 'qwer1234';
const validIdInput = '12345';
const validIdInputUpdated = '678';
const companyName1 = 'Premier Aluminium Systems';
const companyName2 = 'Premier Business Audio';
const corpId = '15001003';

const setSearchDisabledMock = jest.fn();
const setContractRateCompanyMock = jest.fn();
const mockQueryRequest = jest.fn();
const verifiedCompanyMock: Company = {
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
  profileType: 'Business',
  negotiatedRateEnabled: true,
  restricted: false,
  language: 'EN',
};
const contractRateCompanyState: [Company | null, Dispatch<SetStateAction<Company | null>>] = [
  verifiedCompanyMock,
  setContractRateCompanyMock,
];
const emptyContractRateCompanyState: [Company | null, Dispatch<SetStateAction<Company | null>>] = [
  null,
  setContractRateCompanyMock,
];

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
          profileType: 'Business',
          negotiatedRateEnabled: true,
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
          active: true,
          profileType: 'Business',
          negotiatedRateEnabled: true,
          language: 'EN',
        },
      ],
    },
  },
  refetch: jest.fn(),
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const mockQueryRequestResponseCompanyId: any = {
  isError: false,
  isFetching: false,
  error: { message: '' },
  data: {
    companyProfileById: {
      name: companyName1,
      address: {
        addressLine1: '4th floor',
        addressLine2: '120 Holborn',
        addressLine3: 'London',
        country: 'UK',
        postalCode: 'EC1N 2TD',
      },
      telephoneNumber: '020 7806 5480',
      corpId: '15001003',
      companyId: '2569623',
      active: true,
      profileType: 'Business',
      negotiatedRateEnabled: true,
      language: 'EN',
    },
  },
  refetch: jest.fn(),
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => mockQueryRequest(),
}));

const props = {
  showCompanyIdInput: false,
  setSearchDisabled: setSearchDisabledMock,
  contractRateCompanyState: emptyContractRateCompanyState,
};

describe('CompanyName', () => {
  beforeEach(() => {
    mockQueryRequest.mockReturnValue(mockQueryRequestResponse);
    jest.clearAllMocks();
  });

  it('should render the company name input field', () => {
    const { getByPlaceholderText } = render(<CompanySearch {...props} />);
    expect(getByPlaceholderText('ccui.search.companyName.placeholder')).toBeInTheDocument();
  });

  it('should render the CTA button when the name input field is focused', () => {
    const { getByRole, getByPlaceholderText } = render(<CompanySearch {...props} />);
    const nameInput = getByPlaceholderText('ccui.search.companyName.placeholder');
    fireEvent.focus(nameInput);

    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    expect(ctaButton).toBeInTheDocument();
    expect(ctaButton).toBeEnabled();
  });

  it('should render the error message when no name input is entered', async () => {
    const { getByRole, getAllByRole, getByPlaceholderText } = render(<CompanySearch {...props} />);
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    await userEvent.type(input, '');

    const ctaButton = getByRole('button');
    await userEvent.click(ctaButton);
    await waitFor(() => expect(getAllByRole('alert')[0]).toBeInTheDocument());
  });

  it('should render the error message when invalid characters entered on name input', async () => {
    const { getAllByRole, getByPlaceholderText } = render(<CompanySearch {...props} />);

    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.type(input, invalidInput);
    await waitFor(() => expect(getAllByRole('alert')[0]).toBeInTheDocument());
  });

  it('should not render error message when valid characters entered on name input', async () => {
    const { queryByRole, queryByText, getByPlaceholderText } = render(<CompanySearch {...props} />);

    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.type(input, validInput);
    await waitFor(() => expect(queryByRole('alert')).not.toBeInTheDocument());
    await waitFor(() => expect(queryByText('ccui.company.nocompanies')).not.toBeInTheDocument());
  });

  it('should render the error message when name input does not meet minimum character limit', async () => {
    const { getByRole, getAllByRole, getAllByText, getByPlaceholderText } = render(
      <CompanySearch {...props} />
    );
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.type(input, shortInput);
    const ctaButton = getByRole('button');
    userEvent.click(ctaButton);

    await waitFor(() => expect(getAllByRole('alert')[0]).toBeInTheDocument());
    await waitFor(() =>
      expect(getAllByText('ccui.company.minCharacterLengthError')[0]).toBeInTheDocument()
    );
  });

  it('should render the error message when name input exceeds character limit', async () => {
    const { getAllByRole, getAllByText, getByPlaceholderText } = render(
      <CompanySearch {...props} />
    );
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.type(input, longInput);

    await waitFor(() => expect(getAllByRole('alert')[0]).toBeInTheDocument());
    await waitFor(() =>
      expect(getAllByText('ccui.company.characterLengthError')[0]).toBeInTheDocument()
    );
  });

  it('should update the show check company state correctly when name input field is focused', async () => {
    const { getByPlaceholderText, queryByRole, getByRole } = render(<CompanySearch {...props} />);
    const ctaButton = queryByRole('button');
    expect(ctaButton).not.toBeInTheDocument();

    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    await waitFor(() => fireEvent.focus(input));
    await waitFor(() =>
      expect(getByRole('button', { name: 'ccui.company.checkCompany' })).toBeInTheDocument()
    );
  });

  it('should update the search button state correctly when name input field is focused', () => {
    const { getByPlaceholderText } = render(<CompanySearch {...props} />);
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.click(input);

    expect(setSearchDisabledMock).toHaveBeenCalledWith(true);
  });

  it('should fetch companies on clicking Check company CTA and show a modal wih company list', async () => {
    const { getByPlaceholderText, getByRole, getAllByRole, getByTestId } = render(
      <CompanySearch {...props} />
    );

    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.click(input);
    userEvent.type(input, validInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    userEvent.click(ctaButton);

    await waitFor(() => {
      expect(getByTestId('CompanySelection-ModalContent')).toBeInTheDocument();
      expect(getByRole('table')).toBeInTheDocument();
      expect(getAllByRole('row')).toHaveLength(3);
    });
  });

  it('should allow the Company name input to be interactive after closing the modal', async () => {
    const { getByPlaceholderText, getByRole, getByTestId } = render(<CompanySearch {...props} />);

    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.click(input);
    userEvent.type(input, validInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    await waitFor(() => {
      expect(ctaButton).toBeInTheDocument();
      userEvent.click(ctaButton);
    });
    await waitFor(() => {
      expect(getByTestId('CompanySelection-ModalContent')).toBeInTheDocument();
    });
    const closeIcon = getByTestId('CompanySelection-ModalCloseIcon');
    userEvent.click(closeIcon);
    await waitFor(() => {
      expect(getByPlaceholderText('ccui.search.companyName.placeholder')).toBeInTheDocument();
    });
    userEvent.type(input, validInputUpdated);

    await waitFor(() => {
      expect(getByPlaceholderText('ccui.search.companyName.placeholder')).toHaveValue(
        `${validInput}${validInputUpdated}`
      );
    });
  });

  it('should populate the verified company name in the input field', async () => {
    const { getByPlaceholderText, getByRole, getByTestId, getAllByRole } = render(
      <CompanySearch {...props} />
    );
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.click(input);
    userEvent.type(input, validInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    await waitFor(() => {
      expect(ctaButton).toBeInTheDocument();
      userEvent.click(ctaButton);
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
      expect(getByPlaceholderText('ccui.search.companyName.placeholder')).toHaveValue(companyName1);
    });
  });

  it('should update verified company state after a company is verified', async () => {
    const { getByPlaceholderText, getByRole, getByTestId, getAllByRole } = render(
      <CompanySearch {...props} />
    );
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.click(input);
    userEvent.type(input, validInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    await waitFor(() => {
      expect(ctaButton).toBeInTheDocument();
      userEvent.click(ctaButton);
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
      expect(setContractRateCompanyMock).toHaveBeenCalled();
    });
  });

  it('should not display Check company CTA after a company is verified', async () => {
    const { getByPlaceholderText, getByRole, getByTestId, getAllByRole } = render(
      <CompanySearch {...props} />
    );
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.click(input);
    userEvent.type(input, validInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    await waitFor(() => {
      expect(ctaButton).toBeInTheDocument();
      userEvent.click(ctaButton);
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
      expect(ctaButton).not.toBeInTheDocument();
    });
  });

  it('should display Check company CTA when input is changed after a company is verified', async () => {
    const { getByPlaceholderText, getByRole, getByText, getByTestId, getAllByRole } = render(
      <CompanySearch {...props} />
    );
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.click(input);
    await waitFor(() => userEvent.type(input, validInput));
    const ctaButton = getByText('ccui.company.checkCompany');
    await waitFor(() => {
      expect(ctaButton).toBeInTheDocument();
      userEvent.click(ctaButton);
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
      fireEvent.focus(input);
      userEvent.type(input, 'test');
    });
    await waitFor(() => {
      expect(getByRole('button')).toBeInTheDocument();
    });
  });

  it('should not display Check company CTA when input is not changed after a company is verified and search button gets disabled', async () => {
    const { getByPlaceholderText, queryByRole, getByRole, getByTestId, getAllByRole } = render(
      <CompanySearch {...props} />
    );
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.click(input);
    userEvent.type(input, validInput);
    const ctaButton = getByRole('button');
    await waitFor(() => {
      expect(ctaButton).toBeInTheDocument();
      userEvent.click(ctaButton);
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
    userEvent.type(input, validInput);
    userEvent.clear(input);
    userEvent.type(input, companyName1);

    await waitFor(() => {
      expect(queryByRole('button')).not.toBeInTheDocument();
      expect(setSearchDisabledMock).toHaveBeenCalledWith(false);
    });
  });

  it('should disable Search CTA when input is changed after a company is verified', async () => {
    const { getByPlaceholderText, getByRole, getByTestId, getAllByRole } = render(
      <CompanySearch {...props} />
    );
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');
    userEvent.click(input);
    userEvent.type(input, validInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    userEvent.click(ctaButton);
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
    userEvent.type(input, validInput);

    await waitFor(() => {
      expect(setSearchDisabledMock).toHaveBeenCalledWith(true);
    });
  });

  it('should fetch companies on clicking Check company CTA and show an error message when no matching companies found', async () => {
    mockQueryRequestResponse.data = { searchCompanies: { companies: [] } };
    const { getByPlaceholderText, getByRole, getAllByRole, getAllByText } = render(
      <CompanySearch {...props} />
    );
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');

    userEvent.click(input);
    userEvent.type(input, validInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    userEvent.click(ctaButton);

    await waitFor(() => expect(getAllByRole('alert')[0]).toBeInTheDocument());
    await waitFor(() => expect(getAllByText('ccui.company.nocompanies')[0]).toBeInTheDocument());
  });

  it('should fetch companies on clicking Check company CTA and show an error message when there is an error response', async () => {
    ((mockQueryRequestResponse.data = { searchCompanies: { companies: [] } }),
      (mockQueryRequestResponse.isError = true));

    const { getByPlaceholderText, getByRole, getAllByRole, getAllByText } = render(
      <CompanySearch {...props} />
    );
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');

    userEvent.click(input);
    userEvent.type(input, validInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    userEvent.click(ctaButton);

    await waitFor(() => expect(getAllByRole('alert')[0]).toBeInTheDocument());
    await waitFor(() => expect(getAllByText('ccui.company.technicalerror')[0]).toBeInTheDocument());
  });

  it('should update the value when verified company is available', async () => {
    const { getByPlaceholderText } = render(
      <CompanySearch
        {...props}
        showCompanyIdInput={false}
        contractRateCompanyState={contractRateCompanyState}
      />
    );
    const input = getByPlaceholderText('ccui.search.companyName.placeholder');

    await waitFor(() => {
      waitFor(() => expect(input).toHaveValue('Premier Aluminium Systems'));
    });
  });
});

describe('CompanyId', () => {
  beforeEach(() => {
    mockQueryRequest.mockReturnValue(mockQueryRequestResponseCompanyId);
    jest.clearAllMocks();
  });

  it('should render the company id input field', () => {
    const { getByPlaceholderText } = render(<CompanySearch {...props} showCompanyIdInput={true} />);
    expect(getByPlaceholderText('ccui.search.companyId.placeholder')).toBeInTheDocument();
  });

  it('should render the CTA button when the id input field is focused', () => {
    const { getByRole, getByPlaceholderText } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const idInput = getByPlaceholderText('ccui.search.companyId.placeholder');
    fireEvent.focus(idInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    expect(ctaButton).toBeInTheDocument();
    expect(ctaButton).toBeEnabled();
  });

  it('should render the error message when no id input is entered', async () => {
    const { getByRole, getAllByRole, getByPlaceholderText } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    await userEvent.type(input, '');

    const ctaButton = getByRole('button');
    await userEvent.click(ctaButton);
    await waitFor(() => expect(getAllByRole('alert')[0]).toBeInTheDocument());
  });

  it('should render the error message when invalid characters entered on id input', async () => {
    const { getAllByRole, getByPlaceholderText } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );

    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.type(input, 'test');
    await waitFor(() => expect(getAllByRole('alert')[0]).toBeInTheDocument());
  });

  it('should not render error message when valid characters entered on id input', async () => {
    const { queryByRole, queryByText, getByPlaceholderText } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );

    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.type(input, '1234');

    await waitFor(() => expect(queryByRole('alert')).not.toBeInTheDocument());
    await waitFor(() => expect(queryByText('ccui.company.nocompanies')).not.toBeInTheDocument());
  });

  it('should render the error message when id input does not meet minimum character limit', async () => {
    const { getByRole, getAllByRole, getAllByText, getByPlaceholderText } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.type(input, '1');
    const ctaButton = getByRole('button');
    userEvent.click(ctaButton);

    await waitFor(() => expect(getAllByRole('alert')[0]).toBeInTheDocument());
    await waitFor(() =>
      expect(getAllByText('ccui.company.minCharacterLengthError')[0]).toBeInTheDocument()
    );
  });

  it('should render the error message when id input exceeds character limit', async () => {
    const { getAllByRole, getAllByText, getByPlaceholderText } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const longInput = '111111111111111111111111111111111111111111111111111';
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.type(input, longInput);

    await waitFor(() => expect(getAllByRole('alert')[0]).toBeInTheDocument());
    await waitFor(() =>
      expect(getAllByText('ccui.company.characterLengthError')[0]).toBeInTheDocument()
    );
  });

  it('should update the show check company state correctly when id input field is focused', async () => {
    const { getByPlaceholderText, queryByRole, getByRole } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const ctaButton = queryByRole('button');
    expect(ctaButton).not.toBeInTheDocument();

    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    await waitFor(() => fireEvent.focus(input));
    await waitFor(() =>
      expect(getByRole('button', { name: 'ccui.company.checkCompany' })).toBeInTheDocument()
    );
  });

  it('should update the search button state correctly when id input field is focused', () => {
    const { getByPlaceholderText } = render(<CompanySearch {...props} showCompanyIdInput={true} />);
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.click(input);

    expect(setSearchDisabledMock).toHaveBeenCalledWith(true);
  });

  it('should fetch companies on clicking Check company CTA and show a modal wih company list', async () => {
    const { getByPlaceholderText, getByRole, getAllByRole, getByTestId } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );

    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.click(input);
    userEvent.type(input, validIdInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    userEvent.click(ctaButton);
    userEvent.click(input);

    await waitFor(() => {
      expect(getByTestId('CompanySelection-ModalContent')).toBeInTheDocument();
      expect(getByRole('table')).toBeInTheDocument();
      expect(getAllByRole('row')).toHaveLength(2);
    });
  });

  it('should allow the CompanyId name input to be interactive after closing the modal', async () => {
    const { getByPlaceholderText, getByRole, getByTestId } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );

    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.click(input);
    userEvent.type(input, validIdInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    await waitFor(() => {
      expect(ctaButton).toBeInTheDocument();
      userEvent.click(ctaButton);
    });
    await waitFor(() => {
      expect(getByTestId('CompanySelection-ModalContent')).toBeInTheDocument();
    });
    const closeIcon = getByTestId('CompanySelection-ModalCloseIcon');
    userEvent.click(closeIcon);
    await waitFor(() => {
      expect(getByPlaceholderText('ccui.search.companyId.placeholder')).toBeInTheDocument();
    });
    userEvent.type(input, validIdInputUpdated);

    await waitFor(() => {
      expect(getByPlaceholderText('ccui.search.companyId.placeholder')).toHaveValue(
        `${validIdInput}${validIdInputUpdated}`
      );
    });
  });

  it('should keep the company id in the input field', async () => {
    const { getByPlaceholderText, getByRole, getByTestId, getAllByRole } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.click(input);
    userEvent.type(input, validIdInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    await waitFor(() => {
      expect(ctaButton).toBeInTheDocument();
      userEvent.click(ctaButton);
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
      expect(getByPlaceholderText('ccui.search.companyId.placeholder')).toHaveValue(validIdInput);
    });
  });

  it('should update verified company state after a company is verified', async () => {
    const { getByPlaceholderText, getByRole, getByTestId, getAllByRole } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.click(input);
    userEvent.type(input, validIdInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    await waitFor(() => {
      expect(ctaButton).toBeInTheDocument();
      userEvent.click(ctaButton);
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
      expect(setContractRateCompanyMock).toHaveBeenCalled();
    });
  });

  it('should not display Check company CTA after a company is verified', async () => {
    const { getByPlaceholderText, getByRole, getByTestId, getAllByRole } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.click(input);
    userEvent.type(input, validIdInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    await waitFor(() => {
      expect(ctaButton).toBeInTheDocument();
      userEvent.click(ctaButton);
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
      expect(ctaButton).not.toBeInTheDocument();
    });
  });

  it('should display Check company CTA when input is changed after a company is verified', async () => {
    const { getByPlaceholderText, getByRole, getByText, getByTestId, getAllByRole } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.click(input);
    await waitFor(() => userEvent.type(input, validIdInput));
    const ctaButton = getByText('ccui.company.checkCompany');
    await waitFor(() => {
      expect(ctaButton).toBeInTheDocument();
      userEvent.click(ctaButton);
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
      fireEvent.focus(input);
      userEvent.type(input, 'test');
    });
    await waitFor(() => {
      expect(getByRole('button')).toBeInTheDocument();
    });
  });

  it('should not display Check company CTA when input is not changed after a company is verified and search button gets disabled', async () => {
    const { getByPlaceholderText, queryByRole, getByRole, getByTestId, getAllByRole } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.click(input);
    userEvent.type(input, validIdInput);
    const ctaButton = getByRole('button');
    await waitFor(() => {
      expect(ctaButton).toBeInTheDocument();
      userEvent.click(ctaButton);
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
    userEvent.type(input, validIdInput);
    userEvent.clear(input);
    userEvent.type(input, corpId);

    await waitFor(() => {
      expect(queryByRole('button')).not.toBeInTheDocument();
      expect(setSearchDisabledMock).toHaveBeenCalledWith(false);
    });
  });

  it('should disable Search CTA when input is changed after a company is verified', async () => {
    const { getByPlaceholderText, getByRole, getByTestId, getAllByRole } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');
    userEvent.click(input);
    userEvent.type(input, validIdInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    userEvent.click(ctaButton);
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
    userEvent.type(input, validIdInput);

    await waitFor(() => {
      expect(setSearchDisabledMock).toHaveBeenCalledWith(true);
    });
  });

  it('should fetch companies on clicking Check company CTA and show an error message when no matching companies found', async () => {
    mockQueryRequestResponseCompanyId.data = { companyProfileById: { companyId: null } };
    const { getByPlaceholderText, getByRole, getAllByRole, getAllByText } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');

    await userEvent.click(input);
    await userEvent.type(input, validIdInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    await userEvent.click(ctaButton);

    await waitFor(() => expect(getAllByRole('alert')[0]).toBeInTheDocument());
    await waitFor(() => expect(getAllByText('ccui.company.nocompanies')[0]).toBeInTheDocument());
  });

  it('should fetch companies on clicking Check company CTA and show an error message when there is an error response', async () => {
    ((mockQueryRequestResponseCompanyId.data = { companyProfileById: { companyId: null } }),
      (mockQueryRequestResponseCompanyId.isError = true));

    const { getByPlaceholderText, getByRole, getAllByRole, getAllByText } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');

    userEvent.click(input);
    userEvent.type(input, validIdInput);
    const ctaButton = getByRole('button', { name: 'ccui.company.checkCompany' });
    userEvent.click(ctaButton);

    await waitFor(() => expect(getAllByRole('alert')[0]).toBeInTheDocument());
    await waitFor(() => expect(getAllByText('ccui.company.technicalerror')[0]).toBeInTheDocument());
  });

  it('should update the value when verified company is available', async () => {
    const { getByPlaceholderText } = render(
      <CompanySearch
        {...props}
        showCompanyIdInput={true}
        contractRateCompanyState={contractRateCompanyState}
      />
    );
    const input = getByPlaceholderText('ccui.search.companyId.placeholder');

    await waitFor(() => {
      waitFor(() => expect(input).toHaveValue('Premier Aluminium Systems'));
    });
  });

  it('should hide CompanyId input when feature flag is off', async () => {
    mockQueryRequestResponseCompanyId.data = { companyProfileById: { companyId: null } };
    const { queryByPlaceholderText } = render(<CompanySearch {...props} />);
    const input = queryByPlaceholderText('ccui.search.companyId.placeholder');
    expect(input).toBeNull();
  });

  it('should display CompanyId input when feature flag is on', async () => {
    mockQueryRequestResponseCompanyId.data = { companyProfileById: { companyId: null } };
    const { queryByPlaceholderText } = render(
      <CompanySearch {...props} showCompanyIdInput={true} />
    );
    const input = queryByPlaceholderText('ccui.search.companyId.placeholder');
    expect(input).toBeInTheDocument();
  });

  it('should disable the other input whenever one input is clicked', async () => {
    mockQueryRequestResponseCompanyId.data = { companyProfileById: { companyId: null } };
    const { getByPlaceholderText } = render(<CompanySearch {...props} showCompanyIdInput={true} />);
    const idInput = getByPlaceholderText('ccui.search.companyId.placeholder');
    const nameInput = getByPlaceholderText('ccui.search.companyName.placeholder');
    expect(nameInput).toBeInTheDocument();

    await waitFor(() => {
      expect(idInput).toBeInTheDocument();
      fireEvent.focus(idInput);
      expect(nameInput).toBeDisabled();

      fireEvent.blur(idInput);
      fireEvent.focus(nameInput);
      expect(idInput).toBeDisabled();
    });
  });
  it('should clear the other input whenever one input is filled', async () => {
    mockQueryRequestResponseCompanyId.data = { companyProfileById: { companyId: null } };
    const { getByPlaceholderText } = render(<CompanySearch {...props} showCompanyIdInput={true} />);
    const idInput = getByPlaceholderText('ccui.search.companyId.placeholder') as HTMLInputElement;
    const nameInput = getByPlaceholderText(
      'ccui.search.companyName.placeholder'
    ) as HTMLInputElement;
    expect(nameInput).toBeInTheDocument();
    const nameValue = 'test';
    const idValue = '1234';

    await waitFor(() => {
      userEvent.type(nameInput, nameValue);
      expect(nameInput.value).toBe(nameValue);

      fireEvent.blur(nameInput);
      userEvent.type(idInput, idValue);
      expect(nameInput.value).toBe('');

      fireEvent.blur(idInput);
      userEvent.type(nameInput, nameValue);
      expect(idInput.value).toBe('');
    });
  });
});
