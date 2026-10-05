import '@testing-library/jest-dom';

import ComfortableArmchair from '../../assets/icons/ComfortableArmchair';
import { render } from '../../utils/test-utils';
import Icon from './Icon.component';

jest.mock('next/image', () => ({
  __esModule: true,
  default: function MockImage({
    src,
    alt,
    ...props
  }: {
    src: string;
    alt: string;
    [key: string]: unknown;
  }) {
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const React = require('react');
    return React.createElement('img', { src, alt, ...props });
  },
}));

describe('Icon Component', () => {
  it('should render the icon wrapper', () => {
    const { getByTestId } = render(<Icon svg={<ComfortableArmchair />} />);
    expect(getByTestId('svg-container')).toBeInTheDocument();
  });

  it('should display the svg component it receives', () => {
    const { getByTestId } = render(<Icon svg={<ComfortableArmchair data-testid="svg-item" />} />);
    expect(getByTestId('svg-item')).toBeInTheDocument();
  });

  it('should display the component when it receives an url', () => {
    const { getByTestId } = render(
      <Icon src="https://secure2.premierinn.com/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg" />
    );
    expect(getByTestId('svg-container')).toBeInTheDocument();
    expect(getByTestId('svg-container')).toHaveClass('chakra-image');
  });

  it('should set the alt attribute on the image when src and alt are provided', () => {
    const { getByAltText } = render(
      <Icon src="https://secure2.premierinn.com/example/image.png" alt="Premier Inn logo" />
    );
    expect(getByAltText('Premier Inn logo')).toBeInTheDocument();
  });

  it('should not set alt attribute when alt is not provided', () => {
    const { getByTestId } = render(<Icon src="https://secure2.premierinn.com/example/image.png" />);
    expect(getByTestId('svg-container')).not.toHaveAttribute('alt');
  });

  it('should render next/image when useNextImage is true', () => {
    const { getByRole } = render(
      <Icon
        src="https://secure2.premierinn.com/example/image.png"
        alt="Premier Inn logo"
        useNextImage
        height="4rem"
        width="10rem"
      />
    );
    const img = getByRole('img');
    expect(img).toHaveAttribute('src', 'https://secure2.premierinn.com/example/image.png');
    expect(img).toHaveAttribute('alt', 'Premier Inn logo');
  });
});
