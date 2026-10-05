import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';
import { BUSINESS_BOOKER_USER_ROLES, EmployeeStatus } from '@whitbread-eos/api';
import { act } from 'react-dom/test-utils';

import { UserRoleForm } from './UserRoleForm';

global.HTMLElement.prototype.scrollIntoView = jest.fn();

const mockProps = {
  onSubmit: () => true,
  infoTooltipIcon: '/',
  arrowIcon: '/',
  notificationInfo: '/',
  employeeStatus: '',
  isEditEmployeePage: true,
  formRef: { current: document.createElement('form') },
  language: 'en',
  errorIcon: '/',
  showLastTMError: false,
  setShowLastTMError: () => {
    return;
  },
  isMainContact: false,
};

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('UserRoleForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.showLastTMError = false;
  });

  it('should render UserRoleForm component EmployeeStatus = Active and show last TM error', async () => {
    mockProps.employeeStatus = EmployeeStatus.Active;
    mockProps.showLastTMError = true;
    const { getByTestId } = render(<UserRoleForm {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('User-Role-Heading')).toBeInTheDocument();
      expect(getByTestId('userRole-Error-Tooltip')).toBeInTheDocument();
    });
  });

  it('should render UserRoleForm component EmployeeStatus = Active', async () => {
    mockProps.employeeStatus = EmployeeStatus.Active;

    const { getByTestId } = render(<UserRoleForm {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('User-Role-Heading')).toBeInTheDocument();
    });
    await waitFor(() => {
      expect(getByTestId('User-Status-Sub-Heading')).toBeInTheDocument();
    });
    await waitFor(() => {
      expect(getByTestId('User-Role-Sub-Heading')).toBeInTheDocument();
    });
  });
  it('should render UserRoleForm component EmployeeStatus = Inactive', async () => {
    mockProps.employeeStatus = EmployeeStatus.Inactive;
    const { getByTestId } = render(<UserRoleForm {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('User-Role-Heading')).toBeInTheDocument();
    });
    await waitFor(() => {
      expect(getByTestId('User-Status-Sub-Heading')).toBeInTheDocument();
    });
    await waitFor(() => {
      expect(getByTestId('User-Role-Sub-Heading')).toBeInTheDocument();
    });
  });
  it('should render UserRoleForm component EmployeeStatus = Deactivated', async () => {
    mockProps.employeeStatus = EmployeeStatus.Deactivated;
    const { getByTestId } = render(<UserRoleForm {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('User-Role-Heading')).toBeInTheDocument();
    });
    await waitFor(() => {
      expect(getByTestId('User-Status-Sub-Heading')).toBeInTheDocument();
    });
    await waitFor(() => {
      expect(getByTestId('User-Role-Sub-Heading')).toBeInTheDocument();
    });
  });
  it('should render UserRoleForm component on add page', async () => {
    mockProps.employeeStatus = EmployeeStatus.Active;
    mockProps.isEditEmployeePage = false;
    const { getByTestId } = render(<UserRoleForm {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('User-Role-Heading')).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(getByTestId('User-Role-Sub-Heading')).toBeInTheDocument();
    });
  });
  it('should render UserRoleForm component on add page and click the tooltip', async () => {
    mockProps.employeeStatus = EmployeeStatus.Active;
    mockProps.isEditEmployeePage = false;
    const { getByTestId } = render(<UserRoleForm {...mockProps} />);

    await act(async () => {
      fireEvent.click(getByTestId('User-Role-Info-Tooltip-Icon'));
    });

    await waitFor(() => {
      expect(getByTestId('User-Role-Info-Tooltip')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.mouseUp(getByTestId('User-Role-Info-Tooltip-Icon'));
    });
  });

  it('should show error when trying to deactivate a main contact user', async () => {
    const props = {
      ...mockProps,
      employeeStatus: EmployeeStatus.Active,
      isMainContact: true,
      isEditEmployeePage: true,
    };
    const { getByTestId, findByText } = render(<UserRoleForm {...props} />);

    const statusSelectButton = getByTestId('userStatus-IB-Form-Select-Button');
    await act(async () => {
      fireEvent.click(statusSelectButton);
    });

    const deactivatedOption = await findByText('userMgmt.account.status.deactivated');
    await act(async () => {
      fireEvent.click(deactivatedOption);
    });

    const errorMessage = await findByText('userMgmt.employee.edit.error.isMainContact');
    expect(errorMessage).toBeInTheDocument();
  });

  it('should show error when trying to purge a main contact user', async () => {
    const props = {
      ...mockProps,
      employeeStatus: EmployeeStatus.Deactivated,
      isMainContact: true,
      isEditEmployeePage: true,
    };
    const { getByTestId, findByText } = render(<UserRoleForm {...props} />);

    const statusSelectButton = getByTestId('userStatus-IB-Form-Select-Button');
    await act(async () => {
      fireEvent.click(statusSelectButton);
    });

    const deleteOption = await findByText('userMgmt.account.status.delete');
    await act(async () => {
      fireEvent.click(deleteOption);
    });

    const errorMessage = await findByText('userMgmt.employee.edit.error.isMainContact');
    expect(errorMessage).toBeInTheDocument();
  });

  it('should not show error when deactivating a non-main contact user', async () => {
    const props = {
      ...mockProps,
      employeeStatus: EmployeeStatus.Active,
      isMainContact: false,
      isEditEmployeePage: true,
    };
    const { getByTestId, findByText, queryByText } = render(<UserRoleForm {...props} />);

    const statusSelectButton = getByTestId('userStatus-IB-Form-Select-Button');
    await act(async () => {
      fireEvent.click(statusSelectButton);
    });

    const deactivatedOption = await findByText('userMgmt.account.status.deactivated');
    await act(async () => {
      fireEvent.click(deactivatedOption);
    });

    await new Promise((resolve) => setTimeout(resolve, 100));

    expect(queryByText('userMgmt.employee.edit.error.isMainContact')).not.toBeInTheDocument();
  });

  it('should successfully submit form for non-main contact user', async () => {
    const mockOnSubmit = jest.fn();
    const props = {
      ...mockProps,
      employeeStatus: EmployeeStatus.Active,
      isMainContact: false,
      isEditEmployeePage: true,
      onSubmit: mockOnSubmit,
    };

    const { getByTestId, findByText } = render(<UserRoleForm {...props} />);

    const statusSelectButton = getByTestId('userStatus-IB-Form-Select-Button');
    await act(async () => {
      fireEvent.click(statusSelectButton);
    });

    const deactivatedOption = await findByText('userMgmt.account.status.deactivated');
    await act(async () => {
      fireEvent.click(deactivatedOption);
    });

    const form = getByTestId('user-role-form');
    await act(async () => {
      fireEvent.submit(form);
    });

    expect(mockOnSubmit).toHaveBeenCalledWith({
      employeeStatus: EmployeeStatus.Deactivated,
      accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
    });
  });
});
