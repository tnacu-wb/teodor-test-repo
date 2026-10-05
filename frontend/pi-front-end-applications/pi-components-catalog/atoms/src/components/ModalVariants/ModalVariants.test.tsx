import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import ModalVariants from './ModalVariants.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useScreenSize: () => ({ isLessThanMobile: false }),
}));

describe('ModalVariants', () => {
  const props = {
    isOpen: true,
    onClose: jest.fn(),
  };
  const variantProps = {
    title: '',
  };

  it('should render ModalVariants with default variant', () => {
    const { getByTestId } = render(
      <ModalVariants
        variant="default"
        dataTestId="defaultVariant"
        variantProps={variantProps}
        {...props}
      >
        <p>Test Child</p>
      </ModalVariants>
    );
    expect(getByTestId('defaultVariant-ModalContent')).toBeInTheDocument();
  });

  it('should render ModalVariants with gallery variant', () => {
    const { getByTestId } = render(
      <ModalVariants
        variant="gallery"
        dataTestId="galleryVariant"
        variantProps={variantProps}
        {...props}
      >
        <p>Test Child</p>
      </ModalVariants>
    );
    expect(getByTestId('galleryVariant-ModalContent')).toBeInTheDocument();
  });

  it('should render ModalVariants with info variant', () => {
    const { getByTestId } = render(
      <ModalVariants variant="info" dataTestId="infoVariant" variantProps={variantProps} {...props}>
        <p>Test Child</p>
      </ModalVariants>
    );
    expect(getByTestId('infoVariant-ModalContent')).toBeInTheDocument();
  });

  it('should render ModalVariants with cookie variant', () => {
    const { getByTestId } = render(
      <ModalVariants
        variant="cookie"
        dataTestId="cookieVariant"
        variantProps={variantProps}
        {...props}
      >
        <p>Test Child</p>
      </ModalVariants>
    );
    expect(getByTestId('cookieVariant-ModalContent')).toBeInTheDocument();
  });

  it('should render ModalVariants with login variant', () => {
    const { getByTestId } = render(
      <ModalVariants
        variant="login"
        dataTestId="loginVariant"
        variantProps={variantProps}
        {...props}
      >
        <p>Test Child</p>
      </ModalVariants>
    );
    expect(getByTestId('loginVariant-ModalContent')).toBeInTheDocument();
  });

  it('should render ModalVariants with data variant', () => {
    const { getByTestId } = render(
      <ModalVariants variant="data" dataTestId="dataVariant" variantProps={variantProps} {...props}>
        <p>Test Child</p>
      </ModalVariants>
    );
    expect(getByTestId('dataVariant-ModalContent')).toBeInTheDocument();
  });

  it('should render ModalVariants without variant', () => {
    const { getByTestId } = render(
      <ModalVariants
        variant="cookie"
        dataTestId="cookieVariant"
        variantProps={variantProps}
        {...props}
      >
        <p>Test Child</p>
      </ModalVariants>
    );
    expect(getByTestId('cookieVariant-ModalContent')).toBeInTheDocument();
  });
});
