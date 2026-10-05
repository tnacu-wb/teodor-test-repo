import { Link, Stack, StyleProps } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';
import React from 'react';
import { action } from 'storybook/actions';
import * as yup from 'yup';

import Input from '../Input';
import Form, {
  FORM_BUTTON_TYPES,
  FORM_FIELD_TYPES,
  FORM_VALIDATIONS,
  FormDynamicFieldCompProps,
  FormProps,
} from './';

const onSubmit = (data?: object) => {
  action('Submit Clicked / Form valid')(data);
};

const getFormState: FormProps['getFormState'] = (data, errors) => {
  action('Unmount / Get Form State and errors')(data, errors);
};

const fieldsContainerStyles = {
  marginBottom: '2rem',
} as StyleProps;

const buttonsContainerStyles = {
  marginBottom: '2rem',
} as StyleProps;

const defaultButtons = [
  {
    type: FORM_BUTTON_TYPES.SUBMIT,
    label: 'Submit',
    action: onSubmit,
    styles: {
      marginBottom: '1.25rem',
    },
    props: {
      variant: 'primary' as const,
      size: 'full' as const,
    },
    testid: 'FormDemo-SubmitButton',
  },
  {
    type: FORM_BUTTON_TYPES.BUTTON,
    label: 'Cancel',
    action: () => {
      action('Cancel clicked')();
    },
    props: {
      variant: 'tertiary' as const,
      size: 'full' as const,
    },
    testid: 'FormDemo-CancelButton',
  },
];

const defaultElements = {
  fieldsContainerStyles,
  buttonsContainerStyles,
  buttons: defaultButtons,
  bottomFields: [],
};

const meta: Meta<typeof Form> = {
  title: 'Form',
  component: Form,
  argTypes: {
    id: {
      description: 'HTML id attribute for the form element',
      control: 'text',
    },
    autoComplete: {
      description: 'HTML autocomplete attribute for the form',
      control: 'text',
    },
    testid: {
      description: 'Data test ID for testing purposes',
      control: 'text',
    },
    fieldsetDisabled: {
      description: 'Disables all fields in the form when true',
      control: 'boolean',
    },
    resetForm: {
      description: 'Increment to trigger a form reset',
      control: 'number',
    },
    elements: {
      description: 'Configuration object containing fields, buttons, and container styles',
      control: 'object',
    },
    defaultValues: {
      description: 'Initial values for form fields keyed by field name',
      control: 'object',
    },
    validationSchema: {
      description: 'Yup validation schema applied on submit',
      control: false,
    },
    getFormState: {
      description: 'Callback receiving current form data and errors on unmount',
      control: false,
    },
    defaultErrors: {
      description: 'Pre-populated field errors to display on initial render',
      control: 'object',
    },
    errorsOrder: {
      description: 'Array of field names defining the display order of errors',
      control: 'object',
    },
    onChange: {
      description: 'Callback fired when any form field value changes',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component:
          'A dynamic form component supporting multiple field types (text, email, password, radio, dropdown, checkbox, dynamic fields), validation via Yup schemas, and configurable button actions.',
      },
    },
  },
  decorators: [
    (Story) => (
      <Stack w="31rem">
        <Story />
      </Stack>
    ),
  ],
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    elements: {
      ...defaultElements,
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'nonMandatoryField',
          label: 'Not Mandatory Field',
          testid: 'FormDemo-NotMandatoryField',
        },
      ],
    },
    defaultValues: {
      nonMandatoryField: '',
    },
    getFormState,
  } as FormProps,
};

const isRequiredAndIsEmailValidationSchema = yup.object().shape({
  email: yup
    .string()
    .required('Email is Required')
    .matches(FORM_VALIDATIONS.EMAIL.MATCHES, 'This needs to be a valid email'),
  password: yup.string().required('Password is Required'),
});

// --- Variants ---

export const WithEmailAndPasswordValidation: Story = {
  args: {
    elements: {
      ...defaultElements,
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'email',
          label: 'Email',
          props: {
            useTooltip: true,
          },
          testid: 'FormDemo-Email',
        },
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          name: 'password',
          label: 'Password',
          props: {
            useTooltip: true,
          },
          testid: 'FormDemo-Password',
        },
      ],
    },
    defaultValues: {
      email: '',
      password: '',
    },
    validationSchema: isRequiredAndIsEmailValidationSchema,
    getFormState,
  } as FormProps,
};

const radioGroupValidationSchema = yup.object().shape({
  details: yup.string().required('Details are Required'),
});

export const WithRadioGroup: Story = {
  args: {
    elements: {
      ...defaultElements,
      fields: [
        {
          type: FORM_FIELD_TYPES.RADIO_GROUP,
          name: 'details',
          label: 'Details',
          options: [
            {
              value: 'personalAddress',
              label: 'Personal Address',
              testid: 'FormDemo-PersonalAddress',
            },
            {
              value: 'companyAddress',
              label: 'Company Address',
              testid: 'FormDemo-CompanyAddress',
            },
          ],
          testid: 'FormDemo-Details',
        },
      ],
    },
    defaultValues: {
      details: '',
      personalAddress: '',
      companyAddress: '',
    },
    validationSchema: radioGroupValidationSchema,
    getFormState,
  } as FormProps,
};

const radioGroupAlternateContentValidationSchema = yup.object().shape({
  details: yup.string().required('Details are Required'),
  address: yup.string().when('details', {
    is: 'personalAddress',
    then: yup.string().required('Personal address is required'),
  }),
  compAddress: yup.string().when('details', {
    is: 'companyAddress',
    then: yup.string().required('Company address is required'),
  }),
});

export const WithRadioGroupAndRelatedFields: Story = {
  args: {
    elements: {
      ...defaultElements,
      fields: [
        {
          type: FORM_FIELD_TYPES.RADIO_GROUP,
          name: 'details',
          label: 'Details',
          options: [
            {
              value: 'personalAddress',
              label: 'Personal Address',
              testid: 'FormDemo-PersonalAddress',
            },
            {
              value: 'companyAddress',
              label: 'Company Address',
              testid: 'FormDemo-CompanyAddress',
            },
          ],
          testid: 'FormDemo-Details',
          relatedFields: {
            personalAddress: [
              {
                type: FORM_FIELD_TYPES.INPUT_TEXT,
                name: 'address',
                label: 'Address',
                testid: 'FormDemo-Address',
              },
            ],
            companyAddress: [
              {
                type: FORM_FIELD_TYPES.INPUT_TEXT,
                name: 'compAddress',
                label: 'Company Address',
                testid: 'FormDemo-CompAddress',
              },
            ],
          },
        },
      ],
    },
    defaultValues: {
      details: '',
      address: '',
      compAddress: '',
    },
    validationSchema: radioGroupAlternateContentValidationSchema,
    getFormState,
  } as FormProps,
};

const dropdownValidationSchema = yup.object().shape({
  country: yup.string().required('An option is required'),
});

export const WithDropdown: Story = {
  args: {
    elements: {
      ...defaultElements,
      fields: [
        {
          type: FORM_FIELD_TYPES.DROPDOWN,
          name: 'country',
          label: '',
          dropdownOptions: [
            {
              id: 'uk',
              label: 'United Kingdom',
            },
            {
              id: 'germany',
              label: 'Germany',
            },
            {
              id: 'france',
              label: 'France',
            },
          ],
          testid: 'FormDemo-Countries',
          props: {
            placeholder: 'Country',
          },
        },
      ],
    },
    defaultValues: {
      country: '',
    },
    validationSchema: dropdownValidationSchema,
    getFormState,
  } as FormProps,
};

const matchFieldValidationSchema = yup.object().shape({
  newPassword: yup.string().required('Password is required'),
  confirmPassword: yup
    .string()
    .required('Confirm password is required')
    .oneOf([yup.ref('newPassword')], 'Your passwords do not match.'),
});

export const WithPasswordConfirmation: Story = {
  args: {
    elements: {
      ...defaultElements,
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          name: 'newPassword',
          label: 'New Password',
          testid: 'FormDemo-NewPassword',
        },
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          name: 'confirmPassword',
          label: 'Confirm Password',
          testid: 'FormDemo-ConfirmPassword',
        },
      ],
    },
    defaultValues: {
      newPassword: '',
      confirmPassword: '',
    },
    validationSchema: matchFieldValidationSchema,
    getFormState,
  } as FormProps,
};

const nonFieldContentValidationSchema = yup.object().shape({
  email: yup
    .string()
    .required('Email is Required')
    .matches(FORM_VALIDATIONS.EMAIL.MATCHES, 'This needs to be a valid email'),
  password: yup.string().required('Password is Required'),
});

export const WithNonFieldContent: Story = {
  args: {
    elements: {
      ...defaultElements,
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'email',
          label: 'Email',
          props: {
            useTooltip: true,
          },
          testid: 'FormDemo-Email',
        },
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          name: 'password',
          label: 'Password',
          props: {
            useTooltip: true,
          },
          testid: 'FormDemo-Password',
        },
        {
          type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
          name: 'forgottenPassLink',
          label: '',
          content: (
            <Link onClick={() => action('Link clicked')()}>
              Forgotten password - Non Field Content Link
            </Link>
          ),
        },
      ],
    },
    defaultValues: {
      email: '',
      password: '',
    },
    validationSchema: nonFieldContentValidationSchema,
    getFormState,
  } as FormProps,
};

const defaultErrorsValidationSchema = yup.object().shape({
  email: yup
    .string()
    .required('Email is Required')
    .matches(FORM_VALIDATIONS.EMAIL.MATCHES, 'This needs to be a valid email'),
  password: yup.string().required('Password is Required'),
});

const presetErrors = {
  email: {
    message: 'This needs to be a valid email',
    type: 'email',
  },
  password: {
    message: 'Password is Required',
    type: 'required',
  },
};

// --- States ---

export const WithDefaultErrors: Story = {
  args: {
    elements: {
      ...defaultElements,
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          name: 'email',
          label: 'Email',
          props: {
            useTooltip: true,
          },
          testid: 'FormDemo-Email',
        },
        {
          type: FORM_FIELD_TYPES.INPUT_PASSWORD,
          name: 'password',
          label: 'Password',
          props: {
            useTooltip: true,
          },
          testid: 'FormDemo-Password',
        },
      ],
    },
    defaultValues: {
      email: 'aa',
      password: '',
    },
    validationSchema: defaultErrorsValidationSchema,
    getFormState,
    defaultErrors: presetErrors,
  } as FormProps,
};

const formCheckboxSchema = yup.object().shape({
  agree: yup.string().matches(/true/, 'You have to agree to terms and conditions'),
});

export const WithCheckbox: Story = {
  args: {
    elements: {
      ...defaultElements,
      fields: [
        {
          type: FORM_FIELD_TYPES.CHECKBOX,
          name: 'agree',
          label: 'Agree to terms and conditions',
          testid: 'FormDemo-AgreeToTerms',
        },
      ],
    },
    defaultValues: {
      agree: 'false',
    },
    validationSchema: formCheckboxSchema,
  } as FormProps,
};

const formDynamicFieldSchema = yup.object().shape({
  name: yup.string().required('Name is Required'),
});

const DynamicField = ({ formField, field, errors }: FormDynamicFieldCompProps) => {
  const { name, value, onChange, onBlur } = field;
  const onChangeFn = (val: string) => {
    onChange(val);
  };
  const onBlurFn = () => {
    onBlur();
  };
  return (
    <Input
      name={name}
      value={value}
      onChange={onChangeFn}
      onBlur={onBlurFn}
      type={formField.type}
      placeholderText={formField.label}
      label={formField.label}
      error={errors?.[formField.name]?.message}
    />
  );
};

// --- Configuration ---

export const WithDynamicField: Story = {
  args: {
    elements: {
      ...defaultElements,
      fields: [
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'name',
          label: 'Test Dynamic Field',
          testid: 'FormDemo-DynamicField',
          Component: DynamicField,
        },
      ],
    },
    defaultValues: {
      name: 'aa',
    },
    validationSchema: formDynamicFieldSchema,
  } as FormProps,
};

const LinkToggle = ({ field, handleSetValue }: FormDynamicFieldCompProps) => {
  const { name } = field;
  return (
    <Link
      color="btnSecondaryEnabled"
      textDecoration="underline"
      mb="md"
      onClick={() => {
        if (!handleSetValue) {
          return;
        }
        handleSetValue(name, 'manualAddress');
      }}
    >
      Enter manual address
    </Link>
  );
};

export const WithDynamicFieldAndRelatedFields: Story = {
  args: {
    elements: {
      ...defaultElements,
      fields: [
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          name: 'linkToggle',
          label: 'Test Dynamic Field',
          testid: 'FormDemo-DynamicFieldRelated-Toggle',
          Component: LinkToggle,
          relatedFields: {
            manualAddress: [
              {
                type: FORM_FIELD_TYPES.INPUT_TEXT,
                name: 'address',
                label: 'Address',
                testid: 'FormDemo-DynamicFieldRelated-Address',
              },
            ],
          },
        },
      ],
    },
    defaultValues: {
      linkToggle: '',
    },
  } as FormProps,
};
