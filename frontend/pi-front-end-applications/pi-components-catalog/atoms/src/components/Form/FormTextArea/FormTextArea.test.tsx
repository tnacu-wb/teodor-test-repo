import '@testing-library/jest-dom';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render, userEvent } from '../../../utils/test-utils';
import { FORM_FIELD_TYPES } from '../formConstants';
import { FieldsType, FormFieldProps } from '../formTypes';
import FormTextArea from './FormTextArea.component';

const Component = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.TEXTAREA,
    name: 'ieldName',
    label: 'textareaLabel',
  };

  const props: FormFieldProps = {
    control,
    formField: fieldType,
    errors,
    getValues(name: string | undefined): any {
      return name || 'value';
    },
  };

  return <FormTextArea {...props} />;
};

describe('Form TextArea', () => {
  it('should render the component', () => {
    const { getByRole } = render(<Component />);
    const textarea = getByRole('textbox');
    expect(textarea).toBeInTheDocument();
  });

  it('should change the text input value', async () => {
    const { getByRole } = render(<Component />);
    const textarea = getByRole('textbox');
    userEvent.type(textarea, 'My Text');
    expect(textarea).toHaveValue('My Text');
  });
});
