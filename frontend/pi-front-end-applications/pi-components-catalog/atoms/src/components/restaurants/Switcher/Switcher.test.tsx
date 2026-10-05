import '@testing-library/jest-dom';

import { render, fireEvent, act } from '../../../utils/test-utils';
import Switcher from './Switcher.component';

const onChangeMock = jest.fn();
const props = {
  onChange: onChangeMock,
  isChecked: true,
};
describe('Switcher', () => {
  it('should render the component', () => {
    const { getByRole } = render(<Switcher {...props} />);

    const checkbox = getByRole('checkbox');

    expect(checkbox).toBeInTheDocument();
  });
  it('should execute onchange', async () => {
    const { getByRole } = render(<Switcher {...{ ...props, onChange: onChangeMock }} />);

    const switchbtn = getByRole('checkbox');
    await act(async () => {
      fireEvent.click(switchbtn);
    });
    expect(onChangeMock).toHaveBeenCalled();
  });

  it('should enable the switch if it is pressed and after pressing it again, it should be disabled', () => {
    const { getByRole } = render(<Switcher />);
    const checkbox = getByRole('checkbox');
    expect(checkbox).not.toBeChecked();

    fireEvent.click(checkbox);
    expect(checkbox).toBeChecked();

    fireEvent.click(checkbox);
    expect(checkbox).not.toBeChecked();
  });
});
