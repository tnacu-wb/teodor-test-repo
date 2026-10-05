import NavItem from '.';

import { render } from '../../../../utils/test-utils';

describe('NavItem', () => {
  it('should render a NavItem corectly', function () {
    const { getByText } = render(<NavItem title="Nav Item Test" />);
    getByText('Nav Item Test');
  });
});
