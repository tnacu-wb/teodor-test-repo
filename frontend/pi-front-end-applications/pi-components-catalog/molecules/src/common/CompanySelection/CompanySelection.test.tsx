import '@testing-library/jest-dom';

import { render, userEvent } from '../../utils/test-utils';
import CompanySelection from './CompanySelection.component';
import { COMPANY_MODAL_TYPE } from './modalType.enum';

const mockCloseHandler = jest.fn();
const mockVerifiedHandler = jest.fn();
const finalFocusElement = { current: document.createElement('input') };
const mockCompanies = [
  {
    name: 'Premier Aluminium Systems',
    address: {
      addressLine1: '4th floor',
      addressLine2: '120 Holborn',
      addressLine3: '',
      country: '',
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
    name: 'Premier Business Audio',
    address: {
      addressLine1: '4th floor',
      addressLine2: '120 Holborn',
      addressLine3: '',
      country: '',
      postalCode: 'EC1N 2TD',
    },
    telephoneNumber: '020 7806 5481',
    corpId: 'XDJ233DNH',
    companyId: '2569624',
    active: true,
    profileType: 'Business',
    negotiatedRateEnabled: true,
    language: 'EN',
  },
];

const mockProps = {
  showCompanySelectionModal: true,
  companies: mockCompanies,
  finalFocusRef: finalFocusElement,
  onClose: mockCloseHandler,
  onCompanyVerified: mockVerifiedHandler,
  companyModalType: COMPANY_MODAL_TYPE.ACCOUNT_TO_COMPANY,
} as any;

describe('CompanySelection', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should display the company list modal on initial render', () => {
    const { getByText, getByRole, getAllByRole } = render(<CompanySelection {...mockProps} />);

    expect(getByText('ccui.companyModals.selectCompany')).toBeInTheDocument();
    expect(getByRole('table')).toBeInTheDocument();
    expect(getAllByRole('row')).toHaveLength(3);
    expect(getByRole('button', { name: 'ccui.companyModals.close' })).toBeInTheDocument();
  });

  it('should display all the required table columns in the table', () => {
    const { getByText } = render(<CompanySelection {...mockProps} />);

    expect(getByText('ccui.companyModals.companyName')).toBeInTheDocument();
    expect(getByText('ccui.companyModals.address')).toBeInTheDocument();
    expect(getByText('ccui.companyModals.tel')).toBeInTheDocument();
    expect(getByText('ccui.companyModals.corpID')).toBeInTheDocument();
    expect(getByText('ccui.companyModals.companyID')).toBeInTheDocument();
  });

  it('should display all the company informatoin rows', () => {
    const { getByText } = render(<CompanySelection {...mockProps} />);

    mockCompanies.forEach((company) => {
      expect(getByText(company.name)).toBeInTheDocument();
      expect(getByText(company.telephoneNumber)).toBeInTheDocument();
      expect(getByText(company.corpId)).toBeInTheDocument();
      expect(getByText(company.companyId)).toBeInTheDocument();
    });
  });

  it('should not display the table when no companies are available', () => {
    const { queryByText, queryByRole, queryAllByRole } = render(
      <CompanySelection {...mockProps} companies={[]} />
    );

    expect(queryByRole('table')).not.toBeInTheDocument();
    expect(queryAllByRole('row')).toHaveLength(0);
    expect(queryByText('ccui.companyModals.companyName')).not.toBeInTheDocument();
    expect(queryByText('ccui.companyModals.address')).not.toBeInTheDocument();
    expect(queryByText('ccui.companyModals.tel')).not.toBeInTheDocument();
    expect(queryByText('ccui.companyModals.corpID')).not.toBeInTheDocument();
    expect(queryByText('ccui.companyModals.companyID')).not.toBeInTheDocument();
  });

  it('should incoke the close handler when clicking on Cancel CTA', () => {
    const { getByRole } = render(<CompanySelection {...mockProps} />);

    const cancelBtn = getByRole('button', { name: 'ccui.companyModals.close' });
    userEvent.click(cancelBtn);

    expect(mockCloseHandler).toHaveBeenCalledTimes(1);
  });

  it('should display the company profile modal when clicking on a company from the list', async () => {
    const { getByText, getByRole, getAllByRole } = render(<CompanySelection {...mockProps} />);

    const firstCompany = getAllByRole('row')[1];
    await userEvent.click(firstCompany);

    expect(getByText('ccui.companyModals.companyProfile')).toBeInTheDocument();
    expect(getByText(mockCompanies[0].name)).toBeInTheDocument();
    expect(getByText(mockCompanies[0].companyId)).toBeInTheDocument();
    expect(getByText(mockCompanies[0].corpId)).toBeInTheDocument();
    expect(getByText(mockCompanies[0].language)).toBeInTheDocument();
    expect(getByText(mockCompanies[0].profileType)).toBeInTheDocument();

    expect(getByRole('button', { name: 'ccui.companyModals.close' })).toBeInTheDocument();
    expect(getByRole('button', { name: 'ccui.companyModals.verifyConfirm' })).toBeInTheDocument();
  });

  it('should display the company list modal on render', async () => {
    const { getByText, getByRole, getAllByRole } = render(<CompanySelection {...mockProps} />);

    const firstCompany = getAllByRole('row')[1];
    await userEvent.click(firstCompany);

    expect(getByText('ccui.companyModals.companyProfile')).toBeInTheDocument();
    const cancelBtn = getByRole('button', { name: 'ccui.companyModals.close' });
    await userEvent.click(cancelBtn);

    expect(getByText('ccui.companyModals.selectCompany')).toBeInTheDocument();
    expect(getByRole('table')).toBeInTheDocument();
    expect(getAllByRole('row')).toHaveLength(3);
    expect(getByRole('button', { name: 'ccui.companyModals.close' })).toBeInTheDocument();
  });

  it('should incoke the verify company handler when clVerify & Confirm CTA is clicked', async () => {
    const { getByRole, getByText, getAllByRole } = render(<CompanySelection {...mockProps} />);

    const firstCompany = getAllByRole('row')[1];
    await userEvent.click(firstCompany);

    expect(getByText('ccui.companyModals.companyProfile')).toBeInTheDocument();
    const verifyBtn = getByRole('button', { name: 'ccui.companyModals.verifyConfirm' });
    await userEvent.click(verifyBtn);

    expect(mockVerifiedHandler).toHaveBeenCalledTimes(0);
  });
});
