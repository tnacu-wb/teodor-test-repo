import { render } from '@testing-library/react';

import Promotion from './Promotion.component';

const mockedProps = {
  inputPlaceholder: '',
};

describe('Promotion', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<Promotion {...mockedProps} />);
    expect(getByTestId('DropdownComp-promotion-menuButton')).toBeInTheDocument();
  });
});
