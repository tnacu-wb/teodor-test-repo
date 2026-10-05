import '@testing-library/jest-dom';

import { render } from '../../../../utils/test-utils';
import LoginModal from './LoginModal.component';

describe('LoginModal', () => {
  it('should render a LoginModal', () => {
    const props = {
      isOpen: true,
      onClose: jest.fn(),
      variantProps: {
        title: 'Title Header',
      },
    };
    const { getByText } = render(
      <LoginModal {...props}>
        <p>Test Child</p>
      </LoginModal>
    );
    expect(getByText('Title Header')).toBeInTheDocument();
    expect(getByText('Test Child')).toBeInTheDocument();
  });

  it('should render a LoginModal that is closed', () => {
    const props = {
      isOpen: false,
      onClose: jest.fn(),
      variantProps: {
        title: 'Title Header',
      },
    };

    const { queryByText } = render(
      <LoginModal {...props}>
        <p>Test Child</p>
      </LoginModal>
    );

    expect(queryByText('Title Header')).toBeFalsy();
    expect(queryByText('Test Child')).toBeFalsy();
  });

  it('should display the dataTestId if the prop has been added to the component', () => {
    const props = {
      isOpen: false,
      onClose: jest.fn(),
      variantProps: {
        title: 'Title Header',
      },
      dataTestId: 'test',
    };

    const { queryByTestId } = render(
      <LoginModal {...props}>
        <p>Test w</p>
      </LoginModal>
    );
    expect(queryByTestId('test-ModalBody')).toBeFalsy();
  });

  it('should display the modal go back button if the props have been added to the component', () => {
    const props = {
      isOpen: true,
      onClose: jest.fn(),
      variantProps: {
        title: 'Title Header',
        onGoBack: jest.fn(),
        goBackButtonText: 'Go back',
      },
      dataTestId: 'test',
    };

    const { getByTestId, getByText } = render(
      <LoginModal {...props}>
        <p>Test w</p>
      </LoginModal>
    );
    expect(getByTestId('test-ModalGoBackButton')).toBeInTheDocument();
    expect(getByText('Go back')).toBeInTheDocument();
  });

  it('should display the modal footer if the prop has been added to the component', () => {
    const props = {
      isOpen: true,
      onClose: jest.fn(),
      variantProps: {
        title: 'Title Header',
        footer: 'test footer',
      },
      dataTestId: 'test',
    };

    const { getByTestId, getByText } = render(
      <LoginModal {...props}>
        <p>Test w</p>
      </LoginModal>
    );
    expect(getByTestId('test-ModalFooter')).toBeInTheDocument();
    expect(getByText('test footer')).toBeInTheDocument();
  });
});
