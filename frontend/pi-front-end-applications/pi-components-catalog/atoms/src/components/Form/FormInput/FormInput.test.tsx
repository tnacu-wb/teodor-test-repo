import '@testing-library/jest-dom';
import { useForm } from 'react-hook-form';

import { render, userEvent } from '../../../utils/test-utils';
import { FORM_FIELD_TYPES } from '../formConstants';
import { FieldsType, FormFieldProps } from '../formTypes';
import FormInput from './FormInput.component';

const Component = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.INPUT_TEXT,
    name: 'fieldName',
    label: 'Field Name',
  };

  const props: FormFieldProps = {
    control,
    formField: fieldType,
    errors,
    getValues(name: string | undefined): any {
      return name || 'value';
    },
  };

  return <FormInput {...props} />;
};

const EmailInput = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.INPUT_EMAIL,
    name: 'emailField',
    label: 'Email Field',
  };

  const props: FormFieldProps = {
    control,
    formField: fieldType,
    errors,
    getValues(name: string | undefined): any {
      return name || 'value';
    },
  };

  return <FormInput {...props} />;
};

const PasswordInput = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.INPUT_PASSWORD,
    name: 'passwordField',
    label: 'Password Field',
  };

  const props: FormFieldProps = {
    control,
    formField: fieldType,
    errors,
    getValues(name: string | undefined): any {
      return name || 'value';
    },
  };

  return <FormInput {...props} />;
};

describe('Form Input', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<Component />);
    const text = getByTestId('input-fieldName');

    expect(text).toBeInTheDocument();
  });

  it('should change the text input value', async () => {
    const { getByTestId } = render(<Component />);
    const input = getByTestId('input-fieldName');
    userEvent.type(input, 'My Text');
    expect(input).toHaveValue('My Text');
  });

  it('should not pass down email field props to a text input', async () => {
    const { getByTestId } = render(<Component />);
    const input = getByTestId('input-fieldName');
    expect(input).toHaveAttribute('type', 'input');
    expect(input).not.toHaveAttribute('inputmode', 'email');
    expect(input).not.toHaveAttribute('autocapitalize', 'off');
    expect(input).not.toHaveAttribute('autocorrect', 'off');
    expect(input).not.toHaveAttribute('autocomplete', 'email');
  });

  it('should have correct attributes for a password filed', async () => {
    const { getByTestId } = render(<PasswordInput />);
    const input = getByTestId('input-passwordField');
    expect(input).toHaveAttribute('type', 'password');
    expect(input).not.toHaveAttribute('inputmode', 'email');
    expect(input).not.toHaveAttribute('autocapitalize', 'off');
    expect(input).not.toHaveAttribute('autocorrect', 'off');
    expect(input).not.toHaveAttribute('autocomplete', 'email');
  });

  it('should pass down required email field props', async () => {
    const { getByTestId } = render(<EmailInput />);
    const input = getByTestId('input-emailField');
    expect(input).toHaveAttribute('type', 'input');
    expect(input).toHaveAttribute('inputmode', 'email');
    expect(input).toHaveAttribute('autocapitalize', 'off');
    expect(input).toHaveAttribute('autocorrect', 'off');
    expect(input).toHaveAttribute('autocomplete', 'email');
  });
});
