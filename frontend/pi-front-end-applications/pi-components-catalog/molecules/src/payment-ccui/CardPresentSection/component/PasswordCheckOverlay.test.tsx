import '@testing-library/jest-dom';

import { fireEvent, render, waitFor } from '../../../utils/test-utils';
import PasswordCheckOverlay from './PasswordCheckOverlay';

const mockSetIsOverlayOpen = jest.fn();

const props = {
  isOpenOverlay: true,
  setIsOpenOverlay: mockSetIsOverlayOpen,
};

describe('<PasswordCheckOverlay />', () => {
  it('should not render the first modal', () => {
    const { getByTestId } = render(<PasswordCheckOverlay {...props} />);
    expect(getByTestId('passwordCheckOverlay_passwordCheck-ModalContent')).not.toBeVisible();
  });
  it('should render the  modal', async () => {
    props.isOpenOverlay = true;
    const { getByTestId } = render(<PasswordCheckOverlay {...props} />);

    await waitFor(() => {
      expect(getByTestId('passwordCheckOverlay_passwordCheck-ModalContent')).toBeVisible();
    });
  });
  it('should close the  modal when clicking on ok button', async () => {
    props.isOpenOverlay = true;
    const { getByTestId } = render(<PasswordCheckOverlay {...props} />);

    await waitFor(() => {
      expect(getByTestId('passwordCheckOverlay_passwordCheck-ModalContent')).toBeVisible();
    });

    const okButton = getByTestId('passwordCheckOverlay_okButton');

    expect(okButton).toBeInTheDocument();

    fireEvent.click(okButton);

    expect(mockSetIsOverlayOpen).toBeCalled();
  });
});
