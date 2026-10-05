import '@testing-library/jest-dom';
import { FormDynamicFieldCompProps } from '@whitbread-eos/atoms';
import { useForm } from 'react-hook-form';

import { userEvent, render, fireEvent, waitFor } from '../../../../utils/test-utils';
import Input from '../../Input';
import { FORM_FIELD_TYPES } from '../formContants';
import type { FieldsType, FormFieldProps } from '../formTypes';
import FormField from './FormField.component';

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMediaQuery: jest.fn(() => [true]), // Mock implementation for useMediaQuery hook
}));

const ComponentWithInputField = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.INPUT_TEXT,
    name: 'fieldName',
    label: 'Field Name',
  };

  const props: FormFieldProps = {
    control,
    formField,
    setIsEnquiry: jest.fn(),
    setValue: jest.fn(),
    handleSetValue: jest.fn(),
    errors,
    getValues,
  };

  return <FormField {...props} />;
};

const ComponentWithTextareaField = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.TEXT_AREA,
    name: 'fieldName',
    label: 'Field Name',
    testid: 'textarea-fieldName',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues,
  };

  return <FormField {...props} />;
};

const ComponentWithMenuDropdown = () => {
  const {
    control,
    formState: { errors },
    getValues,
    setValue,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.MENUDROPDOWN,
    name: 'menuId',
    label: 'Menu',
    dropdownOptions: [
      { label: 'Menu 1', id: '1' },
      { label: 'Menu 2', id: '2' },
    ],
    testid: 'formMenuDropdown',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
    setValue: setValue,
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
    name: 'child',
    label: 'Children',
    dropdownOptions: [
      { label: '1 Child', id: '1' },
      { label: '2 Children', id: '2' },
      { label: '3 Children', id: '3' },
      { label: '4 Children', id: '4' },
      { label: '5 Children', id: '5 ' },
    ],
    testid: 'formDropdown',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
    isEnquiry: false,
  };

  return <FormField {...props} />;
};

const ComponentWithIsChildrenDropdownOpen = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DROPDOWN,
    name: 'children',
    label: 'Children',
    dropdownOptions: [
      { label: '1 Child', id: '1' },
      { label: '2 Children', id: '2' },
      { label: '3 Children', id: '3' },
      { label: '4 Children', id: '4' },
      { label: '5 Children', id: '5 ' },
    ],
    testid: 'childrenToggle',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues,
    isEnquiry: true,
  };

  return <FormField {...props} />;
};

const ComponentWithEnquiryFormFields = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.ENQUIRY_FORM_FIELDS,
    name: 'formEnquiry',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues,
    isEnquiry: true,
  };

  return <FormField {...props} />;
};

const ComponentWithSessionTabs = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.SESSION_TABS,
    name: 'sessionTabs',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues,
    isEnquiry: true,
  };

  return <FormField {...props} />;
};

const ComponentWithSingleDatePicker = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.SINGLE_DATE_PICKER,
    name: 'selectedDate',
    label: 'Choice Date?',
    testid: 'TableBooking-SelectedDate',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    setValue: jest.fn(),
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
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    setValue: jest.fn(),
    getValues: getValues,
  };

  return <FormField {...props} />;
};

const ComponentWithUserDetailsRelatedFields = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    name: 'userDetails',
    type: FORM_FIELD_TYPES.USER_DETAILS,
    label: 'Your Details',
    relatedFields: [
      {
        type: FORM_FIELD_TYPES.INPUT_TEXT,
        name: 'firstname',
        label: 'First Name',
      },
    ],
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
  };

  return <FormField formStepOneCompleted={true} {...props} />;
};

const ComponentWithSwicher = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.SWITCH,
    name: 'additionalRequirement',
    label: 'Additional requirements (Optional)',
    relatedFields: [
      {
        type: FORM_FIELD_TYPES.INPUT_TEXT,
        name: 'firstname',
        label: 'First Name',
      },
    ],
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
  };

  return <FormField formStepOneCompleted={false} {...props} />;
};

const ComponentWithHiddenField = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.CHECKBOX,
    name: 'agree',
    hidden: true,
    label: 'Agree to terms and conditions',
    testid: 'FormDemo-AgreeToTerms',
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
  };

  return <FormField {...props} />;
};

const ComponentWithUndefinedField = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: undefined,
    name: 'agree',
    label: 'Agree to terms and conditions',
    testid: 'FormDemo-AgreeToTerms',
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

const ComponentWithDynamicFieldWithoutGetValues = () => {
  const {
    control,
    formState: { errors },
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
    // getValues is intentionally omitted or set to undefined
    getValues: undefined,
  };

  return <FormField {...props} />;
};

const ComponentWithDynamicFieldWithoutRelatedFields = () => {
  const {
    control,
    formState: { errors },
    getValues,
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'name',
    label: 'Test Dynamic Field',
    testid: 'FormDemo-DynamicField',
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
  beforeAll(() => {
    window.analyticsData = window.analyticsData || {};
    window.analyticsData.restaurants = {};
  });
  it('should render the component', () => {
    const { getByTestId } = render(<ComponentWithInputField />);
    const text = getByTestId('input-fieldName');

    expect(text).toBeInTheDocument();
  });

  it('should change the text input value, if an Input is given', async () => {
    const { getByRole } = render(<ComponentWithInputField />);
    const input = getByRole('textbox');

    await userEvent.type(input, 'My Text');
    expect(input).toHaveValue('My Text');
  });

  it('should change the textarea value, if an Input is given', async () => {
    const { getByRole } = render(<ComponentWithTextareaField />);
    const input = getByRole('textbox');

    await userEvent.type(input, 'My Text');
    expect(input).toHaveValue('My Text');
  });

  it('should change the checkbox value, if a Checkbox is given', async () => {
    const { getByRole } = render(<ComponentWithCheckbox />);
    const checkbox = getByRole('checkbox');
    expect(checkbox).not.toBeChecked();
    await userEvent.click(checkbox);
    expect(checkbox).toBeChecked();
    await userEvent.click(checkbox);
    expect(checkbox).not.toBeChecked();
  });

  it('renders related fields for "USER_DETIALS" when formStepOneCompleted is true', async () => {
    window.scrollTo = jest.fn();
    const { queryByText } = render(<ComponentWithUserDetailsRelatedFields />);
    const relatedFieldName = 'First Name';
    expect(queryByText(relatedFieldName)).toBeInTheDocument();
  });

  it('should render FormDropdown component when isChildrenDropDownOpen is true', async () => {
    const { getByTestId } = render(<ComponentWithDropdown />);
    const formDropdown = getByTestId('formDropdown');
    expect(formDropdown).toBeInTheDocument();
  });

  it('should render FormDropdown component when isChildrenDropDownOpen is false', async () => {
    const { getByTestId } = render(<ComponentWithIsChildrenDropdownOpen />);
    const formDropdown = getByTestId('childrenToggle');
    expect(formDropdown).toBeInTheDocument();
  });

  it('should render FormDropdown component with enquiry form fields', async () => {
    const { container } = render(<ComponentWithEnquiryFormFields />);
    expect(container).toBeInTheDocument();
  });

  it('should render FormDropdown component with session tabs', async () => {
    const { container } = render(<ComponentWithSessionTabs />);
    expect(container).toBeInTheDocument();
  });

  it('should render the SingleDatePicker component component', () => {
    const { getByTestId } = render(<ComponentWithSingleDatePicker />);
    const text = getByTestId('TableBooking-SelectedDate');

    expect(text).toBeInTheDocument();
  });

  it('should render Swicher component', async () => {
    const { getByRole, queryByText } = render(<ComponentWithSwicher />);
    const AdditonalRequirementToggleButton = getByRole('checkbox');
    expect(AdditonalRequirementToggleButton).toBeInTheDocument();
    await userEvent.click(AdditonalRequirementToggleButton);
    expect(AdditonalRequirementToggleButton).toBeInTheDocument();
    const relatedFieldName = 'First Name';
    expect(queryByText(relatedFieldName)).toBeInTheDocument();
  });

  it('should not render the component for hidden value', async () => {
    const { container } = render(<ComponentWithHiddenField />);
    expect(container).toBeNull;
  });
  it('should not render the component for hidden value', async () => {
    const { container } = render(<ComponentWithUndefinedField />);
    expect(container).toBeNull;
  });

  it('should open children dropdown on click', async () => {
    const { getByTestId } = render(<ComponentWithIsChildrenDropdownOpen />);
    const toggleButton = getByTestId('childrenToggle');

    expect(toggleButton).toBeInTheDocument();

    fireEvent.click(toggleButton);
    await waitFor(() => {
      const dropdownContent = getByTestId('DropdownComp-li-0');
      expect(dropdownContent).toBeInTheDocument();
    });
  });

  it('should render FormMenuDropdown component', async () => {
    const { container } = render(<ComponentWithMenuDropdown />);
    expect(container).toBeInTheDocument();
  });

  it('should change the dynamic field value, if a Dynamic Field is given', async () => {
    const { getByLabelText } = render(<ComponentWithDynamicField />);
    const input = getByLabelText('Test Dynamic Field');

    userEvent.type(input, 'My Text');
    expect(input).toHaveValue('My Text');
  });

  it('should return null when getValues is undefined for DYNAMIC_FIELD type', async () => {
    const { container } = render(<ComponentWithDynamicFieldWithoutGetValues />);
    expect(container.firstChild).toBeNull();
  });
});

// Add this component for testing DYNAMIC_FIELD with relatedFields
const ComponentWithDynamicFieldAndRelatedFields = () => {
  const {
    control,
    formState: { errors },
    getValues,
    setValue,
  } = useForm({
    defaultValues: {
      name: '',
      address: '',
    },
  });

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'name',
    label: 'Test Dynamic Field',
    testid: 'FormDemo-DynamicField',
    Component: DynamicField,
    relatedFields: [
      {
        type: FORM_FIELD_TYPES.INPUT_TEXT,
        name: 'address',
        label: 'Address',
        testid: 'address-field',
      },
    ],
  };

  const props: FormFieldProps = {
    control,
    formField,
    errors,
    getValues: getValues,
    handleSetValue: setValue,
    handleResetField: jest.fn(),
    handleSetError: jest.fn(),
    handleClearErrors: jest.fn(),
    handleTriggerValidation: jest.fn(),
    reset: jest.fn(),
  };

  return <FormField {...props} />;
};

describe('Form Field - DYNAMIC_FIELD with relatedFields', () => {
  it('should render FormRelatedFields when both relatedFields and getValues exist', async () => {
    const { getByTestId, getByLabelText } = render(<ComponentWithDynamicFieldAndRelatedFields />);

    const dynamicField = getByLabelText('Test Dynamic Field');
    expect(dynamicField).toBeInTheDocument();

    const relatedField = getByTestId('address-field');
    expect(relatedField).toBeInTheDocument();
  });

  it('should not render FormRelatedFields when relatedFields is missing', async () => {
    const { queryByTestId, getByLabelText } = render(
      <ComponentWithDynamicFieldWithoutRelatedFields />
    );

    const dynamicField = getByLabelText('Test Dynamic Field');
    expect(dynamicField).toBeInTheDocument();
    const relatedField = queryByTestId('address-field');
    expect(relatedField).not.toBeInTheDocument();
  });

  it('should not render FormRelatedFields when getValues is undefined', async () => {
    const { container, queryByTestId } = render(<ComponentWithDynamicFieldWithoutGetValues />);

    expect(container.firstChild).toBeNull();

    const relatedField = queryByTestId('address-field');
    expect(relatedField).not.toBeInTheDocument();
  });
});
