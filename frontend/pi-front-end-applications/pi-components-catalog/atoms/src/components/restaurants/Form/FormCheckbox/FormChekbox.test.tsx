import '@testing-library/jest-dom';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render } from '../../../../utils/test-utils';
import { FORM_FIELD_TYPES } from '../formContants';
import type { FieldsType } from '../formTypes';
import FormCheckbox from './FormCheckbox.component';

const Component = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const field: FieldsType = {
    type: FORM_FIELD_TYPES.CHECKBOX,
    name: 'agree',
    label: 'Agree to terms and conditions',
    testid: 'FormDemo-AgreeToTerms',
  };

  const props: any = {
    control,
    formField: field,
    errors,
  };

  return <FormCheckbox {...props} />;
};

describe('Form Checkbox', () => {
  it('should render the component', () => {
    const { getByRole } = render(<Component />);
    const checkbox = getByRole('checkbox');
    expect(checkbox).toBeInTheDocument();
  });
});
