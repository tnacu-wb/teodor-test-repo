import '@testing-library/jest-dom';

import { fireEvent, render } from '../../../../utils/test-utils';
import { mockOverrideReasonsResponse } from './../mockResponse';
import AgentOverrideModal, { Props } from './AgentOverrideModal.component';

const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockProps: Props = {
  getFormState: jest.fn(),
  defaultValues: {
    selectReason: '',
    managerName: '',
    callerName: '',
  },
  onSubmit: jest.fn(),
  reasons: mockOverrideReasonsResponse.data.cancellationReasons.cancellationReasons,
  error: '',
  isVisible: true,
  onClose: jest.fn(),
  t: jest.fn(),
};

describe('AgentOverrideModal', () => {
  beforeEach(() => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });
  });

  it('should render AgentOverrideModal', () => {
    const { getByTestId } = render(<AgentOverrideModal {...mockProps} />);
    expect(getByTestId('AgentOverrideModal-Container')).toBeInTheDocument();
  });

  it('should render the AgentOverrideModal title', () => {
    const { getByTestId } = render(<AgentOverrideModal {...mockProps} />);
    expect(getByTestId('ModalHeader')).toBeInTheDocument();
  });

  it('should display a dropdown of reasons', () => {
    const { getByTestId } = render(<AgentOverrideModal {...mockProps} />);

    expect(getByTestId('AgentOverrideModal-SelectReason')).toBeInTheDocument();
  });

  it('should display the manager field when special reason is applied', () => {
    const { getByTestId, getByText } = render(<AgentOverrideModal {...mockProps} />);
    const reasonDropdownBtn = getByTestId(
      'DropdownComp-AgentOverrideModal-SelectReason-InnerDropdown-menuButton'
    );

    fireEvent.click(reasonDropdownBtn);
    const specialReason = getByText('MA Commercial Decision');
    expect(specialReason).toBeInTheDocument();

    fireEvent.click(specialReason);
    expect(getByTestId('input-managerName')).toBeInTheDocument();
  });

  it('should be false by default when managerApprovalNeeded is undefiend', () => {
    const modifiedProps = {
      ...mockProps,
      reasons: mockProps.reasons.map((reason) => {
        if (reason.description === 'Legal Appointments') {
          return {
            ...reason,
            managerApprovalNeeded: null,
          };
        }
        return reason;
      }),
    };
    const { getByTestId, getByText } = render(<AgentOverrideModal {...modifiedProps} />);
    const reasonDropdownBtn = getByTestId(
      'DropdownComp-AgentOverrideModal-SelectReason-InnerDropdown-menuButton'
    );

    fireEvent.click(reasonDropdownBtn);
    const specialReason = getByText('Legal Appointments');

    fireEvent.click(specialReason);
    expect(specialReason).toBeInTheDocument();
  });

  it('should have a Submit button', () => {
    const { getByTestId } = render(<AgentOverrideModal {...mockProps} />);
    expect(getByTestId('AgentOverrideModal-Submit-Button')).toBeInTheDocument();
  });

  it('should have a Close button', () => {
    const { getByRole } = render(<AgentOverrideModal {...mockProps} />);
    expect(getByRole('button', { name: 'Close' })).toBeInTheDocument();
  });

  it('should close AgentOverrideModal when pressing Close btn', async () => {
    const { getByRole } = render(<AgentOverrideModal {...mockProps} />);
    const cancelBtn = getByRole('button', { name: 'Close' });
    fireEvent.click(cancelBtn);
    expect(mockProps.onClose).toBeCalledTimes(1);
  });

  it('should display error message if there is one', async () => {
    const { getByText } = render(
      <AgentOverrideModal {...{ ...mockProps, error: 'Error message' }} />
    );
    expect(getByText('Error message')).toBeInTheDocument();
  });
});
