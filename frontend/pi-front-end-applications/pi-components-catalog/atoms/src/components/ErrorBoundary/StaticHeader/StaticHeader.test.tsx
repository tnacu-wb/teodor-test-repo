import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import StaticHeader from './StaticHeader.component';

describe('StaticHeader', function () {
  it('Should render StaticHeader', () => {
    const { getByTestId } = render(
      <ChakraProvider>
        <StaticHeader />{' '}
      </ChakraProvider>
    );
    expect(getByTestId('StaticHeader-Wrapper-Error')).toBeInTheDocument();
    expect(getByTestId('StaticHeader-Logo')).toBeInTheDocument();
  });
});
