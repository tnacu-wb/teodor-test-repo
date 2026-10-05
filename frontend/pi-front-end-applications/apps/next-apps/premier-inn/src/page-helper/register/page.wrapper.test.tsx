import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import * as React from 'react';

import { render } from '../../utils/test-utils';
import { Page } from './index';

const queryClient = new ReactQuery.QueryClient();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({ locale: 'en' }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => ({}),
}));

jest.mock('@whitbread-eos/organisms', () => {
  const actual = jest.requireActual('@whitbread-eos/organisms');
  return {
    ...actual,
    PISearchContainer: () => {
      return <div data-testid="mocked-search-container">PI Search Container</div>;
    },
  };
});

const mockRouter = {
  push: jest.fn(),
  query: {
    ARRdd: '1',
    ARRmm: '11',
    ARRyyyy: '2025',
    NIGHTS: '2',
    ROOMS: '1',
    ADULT1: '1',
    CHILD1: '0',
    COT1: '0',
    INTTYP1: 'DB',
    slug: ['england', 'greater-london', 'london', 'london-beckton.html'],
  },
  locale: 'en',
  asPath: '/en/account/register',
};

const mockProps = {
  queryClient,
  router: mockRouter as any,
};

describe('Page', () => {
  it('should render register PI Page Wrapper', () => {
    const { getByTestId } = render(<Page {...mockProps} />);
    expect(getByTestId('RegisterPIPage-Wrapper')).toBeInTheDocument();
  });
});
