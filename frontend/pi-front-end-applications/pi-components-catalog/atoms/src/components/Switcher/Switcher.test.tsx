import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import Switcher from './Switcher.component';

describe('Switcher', () => {
  it('should render the component', () => {
    const { getByRole } = render(<Switcher />);

    const checkbox = getByRole('checkbox');

    expect(checkbox).toBeInTheDocument();
  });
  it('should enable the switcher if it is pressed and after pressing it again, it should be disabled', () => {
    const { getByRole } = render(<Switcher />);
    const checkbox = getByRole('checkbox');
    expect(checkbox).not.toBeChecked();

    fireEvent.click(checkbox);
    expect(checkbox).toBeChecked();

    fireEvent.click(checkbox);
    expect(checkbox).not.toBeChecked();
  });
});
