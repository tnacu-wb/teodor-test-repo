import '@testing-library/jest-dom';

import { fireEvent, render } from '../../../../utils/test-utils';
import DefaultModal from './DefaultModal.component';

const mockScreenSize = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useScreenSize: () => mockScreenSize(),
}));

describe('DefaultModal', () => {
  beforeEach(() => {
    mockScreenSize.mockReturnValue({
      isLessThanMd: false,
    });
  });
  it('should render a DefaultModal', () => {
    const props = {
      isOpen: true,
      onClose: jest.fn(),
      variantProps: {
        title: '',
      },
    };
    const { getByText } = render(
      <DefaultModal {...props}>
        <p>Test Child</p>
      </DefaultModal>
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
      <DefaultModal {...props}>
        <p>test</p>
      </DefaultModal>
    );
    const closeButton = getByRole('button', { name: 'Close' });
    fireEvent.click(closeButton);
    expect(props.onClose).toBeCalledTimes(1);
  });

  it('should render a DefaultModal what is close', () => {
    const props = {
      isOpen: false,
      onClose: jest.fn(),
      size: 'xl',
      variantProps: {
        title: '',
      },
    };

    const { queryByText } = render(
      <DefaultModal {...props}>
        <p>Test Child</p>
      </DefaultModal>
    );

    expect(queryByText('Title Header')).toBeFalsy();
    expect(queryByText('Test Child')).toBeFalsy();
  });

  it('should display the dataTestId if the prop has been added to the component', () => {
    const props = {
      isOpen: true,
      onClose: jest.fn(),
      size: 'xl',
      variantProps: {
        title: '',
      },
      dataTestId: 'test',
    };

    const { queryByTestId } = render(
      <DefaultModal {...props}>
        <p>Test</p>
      </DefaultModal>
    );

    expect(queryByTestId('test-ModalBody')).toBeTruthy();
  });
});
