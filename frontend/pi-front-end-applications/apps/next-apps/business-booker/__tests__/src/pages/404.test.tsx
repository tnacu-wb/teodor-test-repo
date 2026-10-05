import NotFoundPage from '~pages/404';
import { render } from '~utils/test-utils';

describe('404 page', () => {
  it('should match the snapshot', () => {
    const { container } = render(<NotFoundPage />);
    expect(container).toMatchSnapshot();
  });
});