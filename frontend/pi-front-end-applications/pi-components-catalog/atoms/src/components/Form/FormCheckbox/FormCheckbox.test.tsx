import '@testing-library/jest-dom';
import { useForm } from 'react-hook-form';

import { render, userEvent } from '../../../utils/test-utils';
import { FORM_FIELD_TYPES } from '../formConstants';
import type { FieldsType, FormCheckboxProps } from '../formTypes';
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

  const props: FormCheckboxProps = {
    control,
    formField: field,
    errors,
  };

  return <FormCheckbox {...props} />;
};

describe('Form Checkbox', () => {
  it('should render the component', () => {
    const { getByRole } = render(<Component />);
    const checkbox = getByRole('checkbox', {
      name: /agree/i,
    });

    expect(checkbox).toBeInTheDocument();
  });

  it('should update selection on click', async () => {
    const { getByRole } = render(<Component />);
    const checkbox = getByRole('checkbox', {
      name: /agree/i,
    });

    expect(checkbox).not.toBeChecked();
    await userEvent.click(checkbox);
    expect(checkbox).toBeChecked();
  });

  it('should allow the checkbox to be unchecked', async () => {
    const { getByRole } = render(<Component />);
    const checkbox = getByRole('checkbox', {
      name: /agree/i,
    });

    expect(checkbox).not.toBeChecked();
    await userEvent.click(checkbox);
    expect(checkbox).toBeChecked();
    await userEvent.click(checkbox);
    expect(checkbox).not.toBeChecked();
  });
});
