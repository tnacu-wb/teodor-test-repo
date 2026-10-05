import React from 'react';

import { render } from '../../utils/test-utils';
import Main from './Main.component';

let mockPathname = 'homepage';

jest.mock('next/navigation', () => ({
  usePathname: () => {
    return mockPathname;
  },
}));

const mockProps = {
  children: <div>test</div>,
};

describe('Main Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render Main component', () => {
    const { getByTestId } = render(<Main {...mockProps} />);

    expect(getByTestId('Main')).toBeInTheDocument();
  });

  it('should render Main component with level 2 sidebar', () => {
    mockPathname = 'manage/cards';
    const { getByTestId } = render(<Main {...mockProps} />);

    expect(getByTestId('Main')).toBeInTheDocument();
  });
});
