import PageLoader from '.';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';

describe('PageLoader component', () => {
  it('should render the component', () => {
    const { getByText } = render(<PageLoader text="Loading..." />);
    expect(getByText('Loading...')).toBeInTheDocument();
  });
});
