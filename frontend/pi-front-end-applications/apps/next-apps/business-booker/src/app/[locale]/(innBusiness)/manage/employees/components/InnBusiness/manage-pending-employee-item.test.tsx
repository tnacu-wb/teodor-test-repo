import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';
import { AccessLevel, EmployeeDetails } from '@whitbread-eos/api';

import { ManagePendingEmployeeItem } from './manage-pending-employee-item';

const mockApproveRejectEmployee = jest.fn();

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    formatIBAssetsUrl: serverUtils.formatIBAssetsUrl,
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    approveRejectEmployee: (...args: any[]) => mockApproveRejectEmployee(...args),
  };
});

const mockUseRouter = {
  refresh: jest.fn(),
};

jest.mock('next/navigation', () => ({
  useRouter: () => mockUseRouter,
}));

const mockEmployee: EmployeeDetails = {
  emailAddress: 'test',
  firstName: 'test',
  lastName: 'test',
  title: 'test',
};

const mockIcons = {};

const token = 'test-token';

const dataTestId = 'ManagePendingEmployeeItem';

describe('ManagePendingEmployeeItem', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render', () => {
    const { getByTestId } = render(
      <ManagePendingEmployeeItem
        employee={mockEmployee}
        icons={mockIcons}
        token={token}
        dataTestId={dataTestId}
      />
    );

    expect(getByTestId('ManagePendingEmployeeItem-listItem')).toBeInTheDocument();
  });

  it('should have default values', () => {
    const { getByTestId } = render(
      <ManagePendingEmployeeItem
        employee={mockEmployee}
        icons={mockIcons}
        token={token}
        dataTestId={dataTestId}
      />
    );

    expect(getByTestId('ManagePendingEmployeeItem-email')).toHaveTextContent(
      mockEmployee.emailAddress
    );
    expect(getByTestId('ManagePendingEmployeeItem-select-IB-Form-Select-Button')).toHaveTextContent(
      'userMgmt.manageEmployees.innBusiness.role.selfBooker'
    );
    expect(getByTestId('ManagePendingEmployeeItem-reject')).toBeInTheDocument();
    expect(getByTestId('ManagePendingEmployeeItem-approve')).toBeInTheDocument();
  });

  it('should have correct default values for business pay manager', () => {
    const { getByTestId } = render(
      <ManagePendingEmployeeItem
        employee={mockEmployee}
        icons={mockIcons}
        token={token}
        dataTestId={dataTestId}
        accessLevel={AccessLevel.BusinessPayManager}
      />
    );

    expect(getByTestId('ManagePendingEmployeeItem-email')).toHaveTextContent(
      mockEmployee.emailAddress
    );
    expect(getByTestId('ManagePendingEmployeeItem-select-IB-Form-Select-Button')).toHaveTextContent(
      'userMgmt.manageEmployees.innBusiness.role.bpUser'
    );
    expect(getByTestId('ManagePendingEmployeeItem-reject')).toBeInTheDocument();
    expect(getByTestId('ManagePendingEmployeeItem-approve')).toBeInTheDocument();
  });

  it('should approve employee', async () => {
    const { getByTestId } = render(
      <ManagePendingEmployeeItem
        employee={mockEmployee}
        icons={mockIcons}
        token={token}
        dataTestId={dataTestId}
      />
    );

    const approveButton = getByTestId('ManagePendingEmployeeItem-approve');

    await waitFor(() => {
      approveButton.click();
    });

    expect(mockApproveRejectEmployee).toHaveBeenCalledWith(
      {
        accessLevel: 'SELF',
        approved: true,
        email: mockEmployee.emailAddress,
        language: 'en',
      },
      token
    );
    expect(mockUseRouter.refresh).toHaveBeenCalled();
  });

  it('should reject employee', async () => {
    const { getByTestId } = render(
      <ManagePendingEmployeeItem
        employee={mockEmployee}
        icons={mockIcons}
        token={token}
        dataTestId={dataTestId}
      />
    );

    const rejectButton = getByTestId('ManagePendingEmployeeItem-reject');

    await waitFor(() => {
      rejectButton.click();
    });

    expect(mockApproveRejectEmployee).toHaveBeenCalledWith(
      {
        accessLevel: 'SELF',
        approved: false,
        email: mockEmployee.emailAddress,
        language: 'en',
      },
      token
    );
    expect(mockUseRouter.refresh).toHaveBeenCalled();
  });
});
