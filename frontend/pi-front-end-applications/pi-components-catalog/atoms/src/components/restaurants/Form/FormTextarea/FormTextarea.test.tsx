import '@testing-library/jest-dom';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render } from '../../../../utils/test-utils';
import { FORM_FIELD_TYPES } from '../formContants';
import { FieldsType, FormFieldProps } from '../formTypes';
import FormTextarea from './FormTextarea.component';

const Component = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const fieldType: FieldsType = {
    type: FORM_FIELD_TYPES.TEXT_AREA,
    name: 'fieldName',
    testid: 'textarea',
    charLimit: 999,
    label: 'Field Name',
  };

  const props: FormFieldProps = {
    control,
    formField: fieldType,
    errors,
    getValues,
  };

  return <FormTextarea {...props} />;
};

describe('Form Teextarea', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<Component />);
    const text = getByTestId('textarea');
    expect(text).toBeInTheDocument();
  });
});
