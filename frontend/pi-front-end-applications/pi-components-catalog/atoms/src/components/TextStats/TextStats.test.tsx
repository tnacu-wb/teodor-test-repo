import 'react';

import { render } from '../../utils/test-utils';
import TextStats from './index';

const mockStats = [
  {
    count: 27,
    text: 'Upcoming',
  },
  {
    count: 20,
    text: 'Checked in',
  },
  {
    count: 26,
    text: 'Past',
  },
  {
    count: 34,
    text: 'Cancelled',
  },
];

describe('TextStats tests', () => {
  it('should render TextStats component', () => {
    const { getByTestId, getByText } = render(<TextStats stats={mockStats} />);
    expect(getByTestId('textstats-summary')).toBeInTheDocument();
    expect(getByText('27 Upcoming')).toBeInTheDocument();
    expect(getByText('20 Checked in')).toBeInTheDocument();
    expect(getByText('26 Past')).toBeInTheDocument();
    expect(getByText('34 Cancelled')).toBeInTheDocument();
  });
  it('should render TextStats component and match the nr of elements with the mock nr of keys', () => {
    const { getAllByTestId } = render(<TextStats stats={mockStats} />);
    const foundElements = getAllByTestId('textstats-element', { exact: false });
    expect(foundElements.length).toBe(4);
  });
});
