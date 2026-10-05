import '@testing-library/jest-dom';

import { render } from '../../../../utils/test-utils';
import CookieModal from './CookieModal.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useScreenSize: () => ({ isLessThanMobile: false }),
}));

describe('CookieModal', () => {
  it('should render a CookieModal', () => {
    const props = {
      isOpen: true,
      onClose: jest.fn(),
      variantProps: {
        title: 'Title Header',
      },
    };
    const { getByText } = render(
      <CookieModal {...props}>
        <p>Test Child</p>
      </CookieModal>
    );
    expect(getByText('Title Header')).toBeInTheDocument();
    expect(getByText('Test Child')).toBeInTheDocument();
  });

  it('should render a CookieModal that is closed', () => {
    const props = {
      isOpen: false,
      onClose: jest.fn(),
      variantProps: {
        title: 'Title Header',
      },
      size: 'xl',
    };

    const { queryByText } = render(
      <CookieModal {...props}>
        <p>Test Child</p>
      </CookieModal>
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
      size: 'xl',
      dataTestId: 'test',
    };

    const { queryByTestId } = render(
      <CookieModal {...props}>
        <p>Test w</p>
      </CookieModal>
    );
    expect(queryByTestId('test-ModalBody')).toBeFalsy();
  });
});
