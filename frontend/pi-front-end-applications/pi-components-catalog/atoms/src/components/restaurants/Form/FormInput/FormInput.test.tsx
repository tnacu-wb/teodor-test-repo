import '@testing-library/jest-dom';
import React from 'react';
import { useForm } from 'react-hook-form';

import { userEvent, render } from '../../../../utils/test-utils';
import { FORM_FIELD_TYPES } from '../formContants';
import { FieldsType, FormFieldProps } from '../formTypes';
import FormInput from './FormInput.component';

const Component = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.INPUT_TEXT,
    name: 'fieldName',
    charLimit: 3,
    label: 'Field Name',
  };

  errors['fieldName'] = {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    message: 'Field Name is required',
  };
  const props: FormFieldProps = {
    control,
    formField: fieldType,
    errors,
    getValues,
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
    await userEvent.type(input, 'Tex');
    expect(input).toHaveValue('Tex');
  });
});
