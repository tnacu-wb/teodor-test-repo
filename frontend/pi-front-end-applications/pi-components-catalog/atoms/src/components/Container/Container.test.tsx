import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import Container from './Container';

describe('Container component', () => {
  it('should render the component', () => {
    const { getByText } = render(<Container>Child</Container>);
    expect(getByText('Child')).toBeInTheDocument();
  });
});
