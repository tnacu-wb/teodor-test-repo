import '@testing-library/jest-dom';
import { useForm } from 'react-hook-form';

import { render, userEvent } from '../../../utils/test-utils';
import Input from '../../Input';
import { FORM_FIELD_TYPES } from '../formConstants';
import type { FieldsType, FormDynamicFieldCompProps, FormDynamicFieldProps } from '../formTypes';
import FormDynamicField from './FormDynamicField.component';

const DynamicField = ({ formField, field, errors }: FormDynamicFieldCompProps) => {
  const onChangeFn = (val: string) => {
    onChange(val);
  };
  const onBlurFn = () => {
    onBlur();
  };

  const { name, value, onChange, onBlur } = field;
  return (
    <Input
      name={name}
      value={value || ''}
      onChange={onChangeFn}
      onBlur={onBlurFn}
      type={formField.type}
      defaultValue="value"
      placeholderText={formField.label}
      label={formField.label}
      error={errors?.[formField.name]?.message}
    />
  );
};

const Component = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const field: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'name',
    label: 'Test Dynamic Field',
    testid: 'FormDemo-DynamiField',
    Component: DynamicField,
  };

  const props: FormDynamicFieldProps = {
    getValues(name: string | undefined): any {
      return name || 'value';
    },
    control,
    formField: field,
    errors,
  };

  return <FormDynamicField {...props} />;
};

describe('Form Dynamic Field', () => {
  it('should render the component', () => {
    const { getByLabelText } = render(<Component />);
    const input = getByLabelText('Test Dynamic Field');
    expect(input).toBeInTheDocument();
  });

  it('should change the dynamic field value', async () => {
    const { getByLabelText } = render(<Component />);
    const input = getByLabelText('Test Dynamic Field');

    userEvent.type(input, 'My Text');
    expect(input).toHaveValue('My Text');
  });
});
