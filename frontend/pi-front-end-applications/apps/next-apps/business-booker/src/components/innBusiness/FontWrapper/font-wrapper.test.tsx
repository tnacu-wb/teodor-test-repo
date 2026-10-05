import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import FontWrapper from './font-wrapper';

describe('FontWrapper', () => {
  it('renders without crashing', () => {
    const { container } = render(<FontWrapper baseUrl="https://example.com" />);

    expect(container).toBeInTheDocument();
  });
});
