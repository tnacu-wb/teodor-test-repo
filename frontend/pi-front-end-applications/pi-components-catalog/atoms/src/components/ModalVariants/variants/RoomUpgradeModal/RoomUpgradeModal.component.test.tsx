import '@testing-library/jest-dom';

import { render, screen, userEvent } from '../../../../utils/test-utils';
import RoomUpgradeModal from './RoomUpgradeModal.component';

const defaultProps = {
  isOpen: true,
  onClose: jest.fn(),
  dataTestId: 'hdp_roomUpgradeModalPopup',
  variantProps: {
    title: 'Upgrade Room',
    priceText: '£29/night',
    imageSrc: 'url.png',
    description: 'Room upgrade description',
  },
  children: <div data-testid="modal-children">Modal Content</div>,
  landscapeModal: {
    primary: true,
    secondary: true,
  },
  isLoading: true,
};

describe('RoomUpgradeModal', () => {
  it('renders modal with title and priceText', () => {
    render(<RoomUpgradeModal {...defaultProps} />);
    expect(screen.getByText('Upgrade Room')).toBeInTheDocument();
    expect(screen.getByText('£29/night')).toBeInTheDocument();
  });

  it('calls onClose when close button is clicked', async () => {
    render(<RoomUpgradeModal {...defaultProps} />);
    const closeButton = screen.getByTestId('hdp_roomUpgradeModalPopup-ModalCloseButton');
    await userEvent.click(closeButton);
    expect(defaultProps.onClose).toHaveBeenCalled();
  });

  it('does not render priceText if not provided', () => {
    const props = {
      ...defaultProps,
      landscapeModal: {
        primary: true,
        secondary: false,
      },
      variantProps: { title: 'Upgrade Room', imageSrc: 'url.png' },
    };
    render(<RoomUpgradeModal {...props} />);
    expect(screen.queryByText('£29/night')).not.toBeInTheDocument();
  });

  it('renders with custom dataTestId prefix', () => {
    render(<RoomUpgradeModal {...defaultProps} dataTestId="hdp_roomUpgradeModalPopup" />);
    expect(screen.getByTestId('hdp_roomUpgradeModalPopup-ModalCloseButton')).toBeInTheDocument();
    expect(screen.getByTestId('hdp_roomUpgradeModalPopup-ModalBody')).toBeInTheDocument();
  });

  it('renders primary and secondary buttons when labels are provided', () => {
    const onPrimaryAction = jest.fn();
    const onSecondaryAction = jest.fn();
    const props = {
      ...defaultProps,
      landscapeModal: {
        primary: false,
        secondary: false,
      },
      variantProps: {
        ...defaultProps.variantProps,
        title: 'Upgrade Room',
        primaryButtonLabel: 'Upgrade',
        secondaryButtonLabel: 'Cancel',
        onPrimaryAction,
        onSecondaryAction,
      },
    };
    render(<RoomUpgradeModal {...props} />);
    expect(screen.getByTestId('hdp_roomUpgradeModalPopup-PrimaryButton')).toBeInTheDocument();
    expect(screen.getByTestId('hdp_roomUpgradeModalPopup-SecondaryButton')).toBeInTheDocument();
    expect(screen.getByText('Upgrade')).toBeInTheDocument();
    expect(screen.getByText('Cancel')).toBeInTheDocument();
  });

  it('calls onPrimaryAction and onSecondaryAction when buttons are clicked', async () => {
    const onPrimaryAction = jest.fn();
    const onSecondaryAction = jest.fn();
    const props = {
      ...defaultProps,
      landscapeModal: {
        primary: false,
        secondary: true,
      },
      variantProps: {
        ...defaultProps.variantProps,
        title: 'Upgrade Room',
        primaryButtonLabel: 'Upgrade',
        secondaryButtonLabel: 'Cancel',
        description: 'Room upgrade description',
        onPrimaryAction,
        onSecondaryAction,
      },
    };
    render(<RoomUpgradeModal {...props} />);
    await userEvent.click(screen.getByTestId('hdp_roomUpgradeModalPopup-PrimaryButton'));
    expect(onPrimaryAction).toHaveBeenCalled();
    await userEvent.click(screen.getByTestId('hdp_roomUpgradeModalPopup-SecondaryButton'));
    expect(onSecondaryAction).toHaveBeenCalled();
  });
});
