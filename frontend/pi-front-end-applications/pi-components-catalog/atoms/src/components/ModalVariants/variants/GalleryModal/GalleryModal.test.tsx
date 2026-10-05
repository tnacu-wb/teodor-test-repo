import '@testing-library/jest-dom';

import { fireEvent, render, within } from '../../../../utils/test-utils';
import GalleryModal from './GalleryModal.component';

describe('GalleryModal', () => {
  it('should render a GalleryModal', () => {
    const props = {
      isOpen: true,
      onClose: jest.fn(),
      variantProps: {
        title: '',
      },
    };
    const { getByText } = render(
      <GalleryModal {...props}>
        <p>Test Child</p>
      </GalleryModal>
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
      <GalleryModal {...props}>
        <p>test</p>
      </GalleryModal>
    );
    const closeButton = getByRole('button', { name: 'Close' });
    fireEvent.click(closeButton);
    expect(props.onClose).toBeCalledTimes(1);
  });

  it('should render a GalleryModal what is close', () => {
    const props = {
      isOpen: false,
      onClose: jest.fn(),
      size: 'xl',
      variantProps: {
        title: '',
      },
    };

    const { queryByText } = render(
      <GalleryModal {...props}>
        <p>Test Child</p>
      </GalleryModal>
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
      <GalleryModal {...props}>
        <p>Test</p>
      </GalleryModal>
    );
    expect(queryByTestId('test-ModalBody')).toBeFalsy();
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
      <GalleryModal {...props}>
        <p>Test Child</p>
      </GalleryModal>
    );

    expect(within(containerElement).getByTestId('test-ModalContent')).toBeInTheDocument();

    containerElement.remove();
  });
});
