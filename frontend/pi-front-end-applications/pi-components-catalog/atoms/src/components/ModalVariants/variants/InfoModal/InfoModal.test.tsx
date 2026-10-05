import '@testing-library/jest-dom';

import { fireEvent, render, within } from '../../../../utils/test-utils';
import InfoModal from './InfoModal.component';

describe('InfoModal', () => {
  it('should render a InfoModal', () => {
    const props = {
      isOpen: true,
      onClose: jest.fn(),
      variantProps: {
        title: '',
      },
    };
    const { getByText } = render(
      <InfoModal {...props}>
        <p>Test Child</p>
      </InfoModal>
    );

    expect(getByText('Test Child')).toBeInTheDocument();
  });

  it('handle action from onClose button works', () => {
    const props = {
      isOpen: true,
      onClose: jest.fn(),
      variantProps: {
        title: '',
      },
    };
    const { getByRole } = render(
      <InfoModal {...props}>
        <p>test</p>
      </InfoModal>
    );
    const closeButton = getByRole('button', { name: 'Close' });
    fireEvent.click(closeButton);
    expect(props.onClose).toBeCalledTimes(1);
  });

  it('should render a InfoModal what is close', () => {
    const props = {
      isOpen: false,
      onClose: jest.fn(),
      size: 'xl',
      variantProps: {
        title: '',
      },
    };

    const { queryByText } = render(
      <InfoModal {...props}>
        <p>Test Child</p>
      </InfoModal>
    );

    expect(queryByText('Title Header')).toBeFalsy();
    expect(queryByText('Test Child')).toBeFalsy();
  });

  it('should display the dataTestId if the prop has been added to the component', () => {
    const props = {
      isOpen: false,
      onClose: jest.fn(),
      size: 'xl',
      variantProps: {
        title: '',
      },
      dataTestId: 'test',
    };

    const { queryByTestId } = render(
      <InfoModal {...props}>
        <p>Test</p>
      </InfoModal>
    );
    expect(queryByTestId('test')).toBeFalsy();
  });

  it('should render a InfoModal with a delimiter', () => {
    const props = {
      isOpen: true,
      onClose: jest.fn(),
      variantProps: {
        title: '',
        delimiter: true,
      },
      dataTestId: 'test',
    };
    const { queryByTestId } = render(
      <InfoModal {...props}>
        <p>Test Child</p>
      </InfoModal>
    );
    expect(queryByTestId('test-Delimiter')).toBeTruthy();
  });

  it('should apply container props when rendered in a portal container', () => {
    const containerElement = document.createElement('div');
    document.body.appendChild(containerElement);

    const props = {
      isOpen: true,
      onClose: jest.fn(),
      variantProps: {
        title: '',
      },
      dataTestId: 'test',
      portalProps: {
        containerRef: {
          current: containerElement,
        },
      },
    };

    render(
      <InfoModal {...props}>
        <p>Test Child</p>
      </InfoModal>
    );

    expect(within(containerElement).getByTestId('test-ModalContent')).toBeInTheDocument();

    containerElement.remove();
  });
});
