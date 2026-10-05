import '@testing-library/jest-dom';
import { useForm } from 'react-hook-form';

import { render, userEvent } from '../../../utils/test-utils';
import { FORM_FIELD_TYPES } from '../formConstants';
import { FieldsType, FormFieldProps } from '../formTypes';
import FormRadioGroup from './FormRadioGroup.component';

const Component = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const fields: FieldsType = {
    type: FORM_FIELD_TYPES.RADIO_GROUP,
    name: 'addressType',
    label: 'Address Type',
    options: [
      {
        value: 'personalAddress',
        label: 'Personal Address',
      },
      {
        value: 'companyAddress',
        label: 'Company Address',
      },
    ],
  };

  const props: FormFieldProps = {
    control,
    formField: fields,
    errors,
    getValues,
  };

  return <FormRadioGroup {...props} />;
};

describe('Form Radio Group', () => {
  it('should render the component', () => {
    const { getByRole } = render(<Component />);
    const radioGroup = getByRole('radiogroup');

    expect(radioGroup).toBeInTheDocument();
  });

  it('should update selection on click', async () => {
    const { getByLabelText } = render(<Component />);
    const radioOptionPersonalAddress = getByLabelText('Personal Address');
    const radioOptionCompanyAddress = getByLabelText('Company Address');

    await userEvent.click(radioOptionPersonalAddress);
    expect(radioOptionPersonalAddress).toBeChecked();
    expect(radioOptionCompanyAddress).not.toBeChecked();
  });

  it('should keep only one selection on clicking the other one', async () => {
    const { getByLabelText } = render(<Component />);
    const radioOptionPersonalAddress = getByLabelText('Personal Address');
    const radioOptionCompanyAddress = getByLabelText('Company Address');

    await userEvent.click(radioOptionCompanyAddress);
    expect(radioOptionPersonalAddress).not.toBeChecked();
    expect(radioOptionCompanyAddress).toBeChecked();
  });
});
