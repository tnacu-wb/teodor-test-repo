import { render } from '@testing-library/react';

import Fonts from './Fonts';

describe('Fonts component', () => {
  it('renders without crashing', () => {
    const { container } = render(<Fonts />);
    expect(container).toBeTruthy();
  });
});
