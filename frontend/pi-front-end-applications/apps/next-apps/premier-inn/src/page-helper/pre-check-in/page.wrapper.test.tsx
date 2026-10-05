import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import * as React from 'react';

import { render } from '../../utils/test-utils';
import { Page } from './index';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    query: {},
  }),
}));

const queryClient = new ReactQuery.QueryClient();
jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMultiStyleConfig: jest.fn(() => ({ field: { _focus: {} } })),
}));

describe('Page', () => {
  it('should render Pre Check-in Page Wrapper', async () => {
    const { findByTestId } = render(<Page queryClient={queryClient} />);
    expect(await findByTestId('PreCheckInPage-Form')).toBeInTheDocument();
  });
});
