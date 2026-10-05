import '@testing-library/jest-dom';

import { render, screen } from '../../../utils/test-utils';
import Discount from './Discount.component';

const props = {
  value: '10',
};

describe('A2C Discount component', () => {
  it('should render the component', () => {
    render(<Discount {...props} />);
    expect(screen.getByDisplayValue(props.value)).toBeInTheDocument();
  });
});
