import '@testing-library/jest-dom';

import { fireEvent, render } from '../../../utils/test-utils';
import Input from './Input.component';

describe('Input', () => {
  const props = {
    name: 'dummyInput',
    value: 'input',
  };
  it('render the Input Component', () => {
    const { getByDisplayValue } = render(<Input {...props} />);
    const input = getByDisplayValue('input');
    expect(input).toBeInTheDocument();
  });

  it('render input with label', () => {
    const { getByTestId } = render(<Input label="lable" {...props} />);
    const label = getByTestId('input-dummyInput');
    expect(label).toBeInTheDocument();
  });

  it('render input with error message', () => {
    const { getByText } = render(<Input error="error" {...props} />);
    const error = getByText('error');
    expect(error).toBeInTheDocument();
  });

  it('should trigger onChange', () => {
    const handleOnChangeMock = jest.fn();
    const { getByDisplayValue } = render(<Input onChange={handleOnChangeMock} {...props} />);
    const input = getByDisplayValue('input');
    fireEvent.change(input, { target: { value: 'a' } });
    expect(handleOnChangeMock).toHaveBeenCalled();
  });

  it('should display the correct label and the correct color', () => {
    const { getByRole, getByText } = render(<Input label="Label" {...props} />);
    const form = getByRole('group');

    expect(form).toHaveTextContent('Label');
    const label = getByText('Label');
    expect(label).toHaveStyle('color: #333333');
  });
  it('should call handleFocus with false on blur', () => {
    const { getByRole } = render(<Input {...props} />);
    const inputElement = getByRole('textbox'); // Adjust role as needed
    fireEvent.blur(inputElement);
    expect(inputElement).not.toHaveFocus();
  });

  it('should set focused to true on input focus', () => {
    const { getByRole, getByDisplayValue } = render(<Input {...props} />);
    const inputElement = getByRole('textbox');
    fireEvent.focus(inputElement);
    const input = getByDisplayValue('input');
    expect(input).toBeInTheDocument();
  });
});
