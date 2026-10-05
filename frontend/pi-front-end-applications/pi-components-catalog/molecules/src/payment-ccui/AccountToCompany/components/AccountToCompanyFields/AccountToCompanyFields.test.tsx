import '@testing-library/jest-dom';
import React from 'react';

import { act, render, userEvent, waitFor } from '../../../../utils/test-utils';
import AccountToCompanyFields from './AccountToCompanyFields.component';

const title = 'ccui.accountToCompanyFields.title';
const companyNamePlaceholder = 'ccui.accountToCompanyFields.placeholderName';
const arNumberPlaceholder = 'ccui.accountToCompanyFields.placeholderNumber';
const addressPlaceholder = 'ccui.accountToCompanyFields.placeholderAddress';
const postalCodePlaceholder = 'ccui.accountToCompanyFields.placeholderPostcode';
const notRestrictedTitle = 'ccui.accountToCompanyFields.notRestrictedTitle';
const notRestrictedDescription = 'ccui.accountToCompanyFields.notRestrictedDescription';
const changeCompany = 'ccui.accountToCompanyFields.changeCompany';

const companyName1 = 'Premier Aluminium Systems';
const mockSetVerifiedCompany = jest.fn();
const validCompany = {
  name: companyName1,
  address: {
    addressLine1: '4th floor',
    addressLine2: '120 Holborn',
    addressLine3: '',
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
  negotiatedRateEnabled: false,
  language: 'EN',
};

const defaultProps = {
  verifiedCompany: validCompany,
  setVerifiedCompany: mockSetVerifiedCompany,
};

describe('<AccountToCompanyFields />', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should render the component title', async () => {
    const { getByText } = render(<AccountToCompanyFields {...defaultProps} />);

    await waitFor(() => {
      expect(getByText(title)).toBeInTheDocument();
    });
  });
  it('should have company information in the readonly fields', async () => {
    const { getByPlaceholderText } = render(<AccountToCompanyFields {...defaultProps} />);

    await waitFor(() => {
      expect(getByPlaceholderText(companyNamePlaceholder)).toHaveValue(companyName1);
      expect(getByPlaceholderText(arNumberPlaceholder)).toHaveValue('341343');
      expect(getByPlaceholderText(addressPlaceholder)).toHaveValue('4th floor, 120 Holborn, UK');
      expect(getByPlaceholderText(postalCodePlaceholder)).toHaveValue('EC1N 2TD');
    });
  });
  it('should have company information in the readonly fields with empty string', async () => {
    defaultProps.verifiedCompany.arNumber = '';
    const { getByPlaceholderText } = render(<AccountToCompanyFields {...defaultProps} />);

    await waitFor(() => {
      expect(getByPlaceholderText(companyNamePlaceholder)).toHaveValue(companyName1);
      expect(getByPlaceholderText(arNumberPlaceholder)).toHaveValue('');
    });
  });

  it('should show company not restricted notification', async () => {
    const { getByRole, getByText } = render(<AccountToCompanyFields {...defaultProps} />);

    await waitFor(() => {
      expect(getByRole('status')).toBeInTheDocument();
      expect(getByText(notRestrictedTitle)).toBeInTheDocument();
      expect(getByText(notRestrictedDescription)).toBeInTheDocument();
    });
  });

  it('should show change company CTA', async () => {
    const { getByRole } = render(<AccountToCompanyFields {...defaultProps} />);

    await waitFor(() => {
      expect(getByRole('button', { name: changeCompany })).toBeInTheDocument();
    });
  });

  it('should reset the verified company on clicking change company CTA', async () => {
    const { getByRole } = render(<AccountToCompanyFields {...defaultProps} />);

    const changeCompanyCTA = getByRole('button', { name: changeCompany });

    await act(async () => {
      userEvent.click(changeCompanyCTA);
    });

    await waitFor(() => {
      expect(mockSetVerifiedCompany).toBeCalledWith(null);
    });
  });
});
