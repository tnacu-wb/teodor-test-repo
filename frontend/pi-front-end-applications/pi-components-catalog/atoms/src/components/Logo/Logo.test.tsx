import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import Logo from './Logo.component';

describe('Logo Component', () => {
  it('should render the Logo with default props', () => {
    const { getByTestId } = render(
      <ChakraProvider>
        <Logo />
      </ChakraProvider>
    );
    getByTestId('logo-container-pi');
  });
  it('should render the Logo and anchor tag should be present', () => {
    const { getByTestId } = render(
      <ChakraProvider>
        <Logo href="www.google.com" />
      </ChakraProvider>
    );
    expect(getByTestId('logo-test-id').closest('a')).toHaveAttribute('href', 'www.google.com');
  });
  it('has the exact variant', () => {
    const { getByTestId } = render(
      <ChakraProvider>
        <Logo variant="pi-simple" />
      </ChakraProvider>
    );
    getByTestId('logo-container-pi-simple');
  });
  it('has the exact transform', () => {
    const { getByTestId } = render(
      <ChakraProvider>
        <Logo variant="pi-simple" transform="mobile" />
      </ChakraProvider>
    );
    const wrapper = getByTestId('logo-container-pi-simple');
    expect(wrapper).toHaveStyle('transform: mobile');
  });

  it('should set alt text on the logo image when href, src and alt are provided', () => {
    const { getByAltText } = render(
      <ChakraProvider>
        <Logo
          href="/gb/en/home.html"
          src="https://secure2.premierinn.com/logo.png"
          alt="Premier Inn Rest Easy"
        />
      </ChakraProvider>
    );
    expect(getByAltText('Premier Inn Rest Easy')).toBeInTheDocument();
  });

  it('should not set alt attribute on the logo image when alt is not provided', () => {
    const { getByRole } = render(
      <ChakraProvider>
        <Logo href="/gb/en/home.html" src="https://secure2.premierinn.com/logo.png" />
      </ChakraProvider>
    );
    expect(getByRole('img')).not.toHaveAttribute('alt');
  });
});
