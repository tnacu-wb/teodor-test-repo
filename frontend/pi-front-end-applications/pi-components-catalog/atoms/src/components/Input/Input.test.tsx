import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import Input from './Input.component';

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key, options) => {
      if (key === 'cardNotPresent.memorableWordRequired') return 'Required';

      return options?.defaultValue ?? key;
    },
  }),
}));

describe('Input', () => {
  it('render the Input Component', () => {
    const props = {
      name: 'input',
      value: 'input',
      onChange: () => jest.fn(),
    };
    const { getByDisplayValue } = render(<Input {...props} />);
    const input = getByDisplayValue('input');
    expect(input).toBeInTheDocument();
  });

  it('render input with label', () => {
    const props = {
      name: 'input',
      label: 'label',
      value: 'input',
      onChange: () => jest.fn(),
    };
    const { getByText } = render(<Input {...props} />);
    const label = getByText('label');
    expect(label).toBeInTheDocument();
  });

  it('render input with helper text', () => {
    const props = {
      name: 'input',
      helperText: 'helper text',
      value: 'input',
      onChange: () => jest.fn(),
    };
    const { getByText } = render(<Input {...props} />);
    const helperText = getByText('helper text');
    expect(helperText).toBeInTheDocument();
  });

  it('render input with char limit', () => {
    const props = {
      name: 'input',
      charLimit: 10,
      value: 'input',
      onChange: () => jest.fn(),
    };
    const { getByText } = render(<Input {...props} />);
    const charLimit = getByText('5/10');
    expect(charLimit).toBeInTheDocument();
  });

  it('render input with error message', () => {
    const props = {
      name: 'input',
      error: 'error',
      value: 'input',
      onChange: () => jest.fn(),
    };
    const { getByText } = render(<Input {...props} />);
    const error = getByText('error');
    expect(error).toBeInTheDocument();
  });

  it('render input tooltip', () => {
    const props = {
      name: 'input',
      charLimit: 10,
      error: 'error',
      useTooltip: true,
      value: 'input',
      onChange: () => jest.fn(),
    };
    const { getByRole } = render(<Input {...props} />);
    const tooltip = getByRole('tooltip');
    expect(tooltip).toBeInTheDocument();
  });

  it('render custom tooltip', () => {
    const props = {
      name: 'input',
      charLimit: 10,
      error: 'custom error',
      useCustomTooltip: true,
      value: 'input',
      onChange: () => jest.fn(),
    };
    const { getByText } = render(<Input {...props} />);
    const error = getByText('custom error');
    expect(error).toBeInTheDocument();
  });

  it('render disabled input, label hidden, char limit hidden, helper text visibile', () => {
    const props = {
      name: 'input',
      label: 'label',
      value: 'input',
      helperText: 'helper text',
      charLimit: 10,
      isDisabled: true,
      onChange: () => jest.fn(),
    };
    const { getByDisplayValue, queryByText } = render(<Input {...props} />);
    const input = getByDisplayValue('input');
    const label = queryByText('label');
    const charLimit = queryByText('5/10');
    const helperText = queryByText('helper text');

    expect(input).toBeDisabled();
    expect(label).not.toBeInTheDocument();
    expect(charLimit).not.toBeInTheDocument();
    expect(helperText).toBeInTheDocument();
  });

  it('should trigger onChange', () => {
    const handleOnChange = jest.fn();
    const props = {
      name: 'input',
      value: 'input',
    };
    const { getByDisplayValue } = render(<Input onChange={handleOnChange} {...props} />);
    const input = getByDisplayValue('input');
    fireEvent.change(input, { target: { value: 'a' } });
    expect(handleOnChange).toHaveBeenCalled();
  });
  it('should display the helperText', () => {
    const props = {
      name: 'input',
      value: 'input',
      helperText: 'helper text',
      error: 'Error message',
      isDisabled: true,
      onChange: () => jest.fn(),
    };
    const { getByText } = render(<Input {...props} />);
    const helper = getByText('helper text');

    expect(helper).toBeInTheDocument();
  });
  it('should display the value at 0 if it is not defined', () => {
    const props = {
      name: 'input',
      charLimit: 20,
      isDisabled: false,
      onChange: () => jest.fn(),
    };
    const { getByText } = render(<Input {...props} />);
    const charLimit = getByText('0/20');

    expect(charLimit).toBeInTheDocument();
  });
  it('should not display the charLimit if the input is disabled', () => {
    const props = {
      name: 'input',
      value: 'value',
      charLimit: 20,
      isDisabled: true,
      onChange: () => jest.fn(),
    };
    const { queryByText } = render(<Input {...props} />);
    const charLimit = queryByText('5/20');

    expect(charLimit).not.toBeInTheDocument();
  });
  it('should display the correct label and the correct color', () => {
    const props = {
      name: 'input',
      label: 'Label',
      onChange: () => jest.fn(),
    };
    const { getByRole, getByText } = render(<Input {...props} />);
    const form = getByRole('group');

    expect(form).toHaveTextContent('Label');
    const label = getByText('Label');
    expect(label).toHaveStyle('color: darkGrey1');
  });
  it('should display the correct label color if the error is defined', () => {
    const props = {
      name: 'input',
      label: 'Label',
      error: 'Error',
      onChange: () => jest.fn(),
    };
    const { getByText } = render(<Input {...props} />);

    const label = getByText('Label');
    expect(label).toHaveStyle('color: error');
  });

  it('should display the error icon if the error is defined and the icon is shown', () => {
    const props = {
      name: 'input',
      label: 'Label',
      error: 'Error',
      showIcon: true,
      onChange: () => jest.fn(),
    };
    const { getByTestId } = render(<Input {...props} />);

    const icon = getByTestId('inputIconError');
    expect(icon).toBeInTheDocument;
  });

  it('should hide the success icon if there is no value and no error', () => {
    const props = {
      name: 'input',
      label: 'Label',
      showIcon: true,
      onChange: () => jest.fn(),
    };
    const { queryByTestId } = render(<Input {...props} />);

    const icon = queryByTestId('inputIconSuccess');
    expect(icon).not.toBeInTheDocument;
  });

  it('should display the success icon if there is a value and no error', () => {
    const props = {
      name: 'input',
      label: 'Label',
      value: 'a value',
      showIcon: true,
      onChange: () => jest.fn(),
    };
    const { getByTestId } = render(<Input {...props} />);

    const icon = getByTestId('inputIconSuccess');
    expect(icon).toBeInTheDocument;
  });

  describe('required field accessibility', () => {
    it('should set aria-required and a cleaned accessible label when the label ends with *', () => {
      const props = {
        name: 'input',
        label: 'First name *',
        value: '',
        onChange: () => jest.fn(),
      };

      const { getByTestId } = render(<Input {...props} />);
      const input = getByTestId('input-input');
      const label = getByTestId('input-input-label');

      expect(input).toHaveAccessibleName('First name Required');
      expect(input).toHaveAttribute('aria-label', 'First name Required');
      expect(input).not.toHaveAttribute('required');
      expect(label).toHaveTextContent('First name *');
    });

    it('should use the placeholder text as the accessible label fallback when a required label is not provided', () => {
      const props = {
        name: 'input',
        placeholderText: 'Postcode *',
        value: '',
        onChange: () => jest.fn(),
      };

      const { getByTestId } = render(<Input {...props} />);
      const input = getByTestId('input-input');

      expect(input).toHaveAccessibleName('Postcode Required');
      expect(input).toHaveAttribute('aria-label', 'Postcode Required');
      expect(input).not.toHaveAttribute('required');
    });

    it('should mark the input as aria-required when isInputAriaRequired is passed explicitly', () => {
      const props = {
        name: 'input',
        label: 'First name',
        isInputAriaRequired: true,
        value: '',
        onChange: () => jest.fn(),
      };

      const { getByTestId } = render(<Input {...props} />);
      const input = getByTestId('input-input');

      expect(input).toHaveAccessibleName('First name Required');
      expect(input).toHaveAttribute('aria-label', 'First name Required');
      expect(input).not.toHaveAttribute('required');
    });

    it('should not add required accessibility attributes when the field is optional', () => {
      const props = {
        name: 'input',
        label: 'First name',
        placeholderText: 'First name',
        value: '',
        onChange: () => jest.fn(),
      };

      const { getByTestId } = render(<Input {...props} />);
      const input = getByTestId('input-input');

      expect(input).not.toHaveAccessibleName();
      expect(input).not.toHaveAttribute('aria-label');
      expect(input).not.toHaveAttribute('required');
    });
  });
});
