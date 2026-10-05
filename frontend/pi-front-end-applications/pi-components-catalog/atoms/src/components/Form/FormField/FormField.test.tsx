import '@testing-library/jest-dom';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render, userEvent, waitFor } from '../../../utils/test-utils';
import Input from '../../Input';
import { FORM_FIELD_TYPES, FORM_BUTTON_TYPES } from '../formConstants';
import type { FieldsType, FormDynamicFieldCompProps, FormFieldProps } from '../formTypes';
import FormField from './FormField.component';

const ComponentWithInputField = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.INPUT_TEXT,
    name: 'fieldName',
    label: 'Field Name',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues(name: string | undefined): any {
      return name || 'value';
    },
  };

  return <FormField {...props} />;
};

const ComponentWithTextareaField = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.TEXTAREA,
    name: 'fieldName',
    label: 'Field Name',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues(name: string | undefined): any {
      return name || 'value';
    },
  };

  return <FormField {...props} />;
};

const ComponentWithRadioGroup = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.RADIO_GROUP,
    name: 'details',
    label: 'Details',
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
    formField,
    errors,
    getValues: getValues,
  };

  return <FormField {...props} />;
};

const ComponentWithRelatedFields = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
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
    relatedFields: {
      personalAddress: [
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'address',
          label: 'Address',
        },
      ],
      companyAddress: [
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'companyDetails',
          label: 'Company Address Details',
        },
      ],
    },
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
  };

  return <FormField {...props} />;
};

const ComponentWithDropdown = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DROPDOWN,
    name: 'country',
    label: 'Country',
    dropdownOptions: [
      {
        id: 1,
        label: 'United Kingdom',
      },
      {
        id: 2,
        label: 'Germany',
      },
      {
        id: 3,
        label: 'France',
      },
    ],
    testid: 'formDropdown',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
  };

  return <FormField {...props} />;
};

const ComponentWithCheckbox = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.CHECKBOX,
    name: 'agree',
    label: 'Agree to terms and conditions',
    testid: 'FormDemo-AgreeToTerms',
    relatedFields: {
      personalAddress: [
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'address',
          label: 'Address',
        },
      ],
      companyAddress: [
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'companyDetails',
          label: 'Company Address Details',
        },
      ],
    },
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
  };

  return <FormField {...props} />;
};

const ComponentWithNonFieldContent = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
    name: 'heading',
    label: 'Heading',
    content: <h2 role="heading">My Heading</h2>,
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
  };

  return <FormField {...props} />;
};

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
      placeholderText={formField.label}
      label={formField.label}
      error={errors?.[formField.name]?.message}
    />
  );
};

const ComponentWithSubmitButton = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    name: 'buttonName',
    type: FORM_BUTTON_TYPES.SUBMIT,
    label: 'Submit',
    action: jest.fn(),
    styles: {
      color: 'blue',
    },
    props: {
      variant: 'primary',
      size: 'sm',
    },
    testid: 'test-submitbutton',
  };

  const btnProps = {
    label: 'Submit',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
    btnProps: btnProps,
  };

  return (
    <FormField {...props} {...btnProps}>
      {btnProps?.label}
    </FormField>
  );
};

const ComponentWithButton = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    name: 'buttonName',
    type: FORM_BUTTON_TYPES.BUTTON,
    label: 'Continue',
    action: jest.fn(),
    styles: {
      color: 'blue',
    },
    props: {
      variant: 'primary',
      size: 'sm',
    },
    testid: 'test-button',
  };

  const btnProps = {
    label: 'Continue',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
    btnProps: btnProps,
  };

  return (
    <FormField {...props} {...btnProps}>
      {btnProps?.label}
    </FormField>
  );
};

const ComponentWithDynamicField = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'name',
    label: 'Test Dynamic Field',
    testid: 'FormDemo-DynamiField',
    Component: DynamicField,
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
  };

  return <FormField {...props} />;
};

describe('Form Field', () => {
  it('should render the component', () => {
    const { getByTestId } = render(<ComponentWithInputField />);
    const text = getByTestId('input-fieldName');

    expect(text).toBeInTheDocument();
  });

  it('should change the text input value, if an Input is given', async () => {
    const { getByTestId } = render(<ComponentWithInputField />);
    const input = getByTestId('input-fieldName');

    userEvent.type(input, 'My Text');
    await waitFor(() => {
      expect(input).toHaveValue('My Text');
    });
  });

  it('should render textarea component', () => {
    const { getByPlaceholderText } = render(<ComponentWithTextareaField />);
    const textarea = getByPlaceholderText('Field Name');
    expect(textarea).toBeInTheDocument();
  });

  it('should render submit button component', () => {
    const { getByText } = render(<ComponentWithSubmitButton />);
    const button = getByText('Submit');
    expect(button).toBeInTheDocument();
  });

  it('should render plain button component', () => {
    const { getByText } = render(<ComponentWithButton />);
    const button = getByText('Continue');
    expect(button).toBeInTheDocument();
  });

  it('should change the radio group value, if an Radio Group is given', async () => {
    const { getByLabelText } = render(<ComponentWithRadioGroup />);
    const radioOptionPersonalAddress = getByLabelText('Personal Address');
    const radioOptionCompanyAddress = getByLabelText('Company Address');

    await userEvent.click(radioOptionPersonalAddress);
    expect(radioOptionPersonalAddress).toBeChecked();
    expect(radioOptionCompanyAddress).not.toBeChecked();
  });

  it('should render the related fields, if the right option is selected', async () => {
    const { getByLabelText } = render(<ComponentWithRelatedFields />);
    const radioOptionCompanyAddress = getByLabelText('Company Address');

    await userEvent.click(radioOptionCompanyAddress);
    expect(radioOptionCompanyAddress).toBeChecked();

    await waitFor(() => {
      expect(getByLabelText('Company Address Details')).toBeInTheDocument();
    });
  });

  it('should render non field content, if given', () => {
    const { getByRole } = render(<ComponentWithNonFieldContent />);
    const heading = getByRole('heading');

    expect(heading).toBeInTheDocument();
    expect(heading.innerHTML).toBe('My Heading');
  });

  it('should change the dropdown value, if a Dropdown is given', async () => {
    const { getByText, container } = render(<ComponentWithDropdown />);
    const dropdownToggle = getByText('Country', { selector: 'p' });
    const selector = container.querySelector('[aria-haspopup="menu"]');

    await userEvent.click(dropdownToggle);

    const dropdownOptionUK = getByText('United Kingdom');
    await dropdownOptionUK.click();

    expect(selector).toHaveTextContent('United Kingdom');
  });

  it('should change the checkbox value, if a Checkbox is given', async () => {
    const { getByRole } = render(<ComponentWithCheckbox />);
    const checkbox = getByRole('checkbox', {
      name: /agree/i,
    });

    expect(checkbox).not.toBeChecked();
    await userEvent.click(checkbox);
    expect(checkbox).toBeChecked();
    await userEvent.click(checkbox);
    expect(checkbox).not.toBeChecked();
  });

  it('should change the dynamic field value, if a Dynamic Field is given', async () => {
    const { getByLabelText } = render(<ComponentWithDynamicField />);
    const input = getByLabelText('Test Dynamic Field');

    userEvent.type(input, 'My Text');
    expect(input).toHaveValue('My Text');
  });
});
