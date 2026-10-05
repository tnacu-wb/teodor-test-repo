# Motivation

Simplify Form handling and Form validations with a Form component that renders and validates fields based on a configuration object.

Form uses react-hook-form and yupResolver for field validations using yup.schema

Check storybook examples for use cases!

# Exports

Form
FORM_FIELD_TYPES
FORM_BUTTON_TYPES

# Configuration object format

```
{
  elements: {
    formStyles?: StyleProps;
    fieldsContainerStyles?: StyleProps;
    buttonsContainerStyles?: StyleProps;
    fields: FieldsType[];
    buttons: ButtonsType[];
  };
  errorsOrder?: string[];
  defaultValues: {
    [key: string]: string | number;
  };
  validationSchema?: yup.AnyObjectSchema;
  getFormState?: (data?: object, errors?: FieldErrors<{ [key: string]: string | number }>) => void;
  defaultErrors?: {
    [key: string]: FieldError;
  };
}
```

## elements

### formStyles (Optional)

Applied on the form container which is a Box

### fieldsContainerStyles (Optional)

Applied on the fields container which is a Flex

### buttonsContainerStyles (Optional)

Applied on the buttons container which is a Flex

### fields format

```
interface FieldsType {
  type: FormFieldsValuesType;
  id?: string; 
  name: string;
  label: string;
  relatedFields?: {
    [key: string]: FieldsType[];
  };
  props?: {
    [key: string]: any;
  };
  styles?: StyleProps;
  options?: FormRadioGroupOptionType[]; 
  dropdownOptions?:FormDropdownOptionType[];
  content?: ReactNode;
  testid?: String;
  Component?: FunctionComponent<any>;
  hidden?: boolean;
  onChange?: (value: string) => void;
}
```

#### type

type is one of the FORM_FIELD_TYPES:

```
export const FORM_FIELD_TYPES = {
  INPUT_TEXT: 'input',
  INPUT_PASSWORD: 'password',
  DROPDOWN: 'dropdown',
  RADIO_GROUP: 'radioGroup',
  NON_FIELD_CONTENT: 'nonFieldContent',
  CHECKBOX: 'checkbox',
  DYNAMIC_FIELD: 'dynamicField',
  TEXTAREA: 'textArea',
} as const;
```
#### id

id is used to generate React key by concatenating the name with the id. 

This is most useful when we have alternate content with the same field names.

#### name

Name of the field - will be the key in the submit data object

#### label 

Label of the field 

- used as a placeholder for input
- as a Text before Radio list for RADIO_GROUP

#### relatedFields (optional)

Currently handled only for RADIO_GROUP type. Used to render alternate fields for each radio selection.

Each key in the relatedFields should match one of the RADIO_GROUP options value.

Example:
```
fields: [
  {
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
          name: 'compAddress',
          label: 'Company Address',
        },
      ],
    },
  },
]
```

#### props (optional)

Currently props are only used for FORM_FIELD_TYPES.INPUT_TEXT and FORM_FIELD_TYPES.INPUT_PASSWORD.

props will be passed as a spread in the props of inner Input component.

#### styles (optional)

Merged with the container (Flex) props

#### options (mandatory for RADIO_GROUP type)

An array of objects containing value and label for each Radio button in the RadioGroup

```
export type FormRadioGroupOptionType = {
  value: string;
  label: string;
  testid?: string
};
```

#### dropdownOptions (mandatory for DROPDOWN type)
An array of objects containing id, label and optionally an icon for each Dropdown Option in the Dropdown

```
export type FormDropdownOptionType = {
  id: number | string;
  icon?: ReactNode;
  label: string | number;
};
```

#### content

Used only for NON_FIELD_CONTENT type to render content that is not a field.

NON_FIELD_CONTENT type offers the ability to render other content (start, in between or at the end of the list of fields handled by the form). (Check stories for an example)

#### testid

This will be the data-testid property.

- Input: set on the Flex container
- inner Input will get the default data-testid={`input-{formField.name}`}
- RadioGroup: set on the Flex container
- RadioGroup option set on inner Radio component

#### Component

Used for DYNAMIC_FIELD type. 

The component will receive the following props:
- formField - the actual config object field
- errors - the full errors object of the form
- field ({name, value, onChange, onBlur})

#### hidden

Boolean value to hide a field. This is useful to show different fields based on external condition.

(e.g. UK should have addressLine4 field displayed, while DE needs to have cityName (location) displayed).

#### onChange

Can be passed into config if we need to react to the change of this field.

(one use case would be to set analytics values when a field changes value)

### buttons format

```
export interface ButtonsType {
  type: FormButtonsValuesType;
  label: string;
  action: (data?: object) => void;
  styles?: StyleProps;
  props: {
    variant: ButtonProps['variant'];
    size: ButtonProps['size'];
  };
  testid?: String;
}
```

#### type

type is one of the FORM_BUTTON_TYPES

```
export const FORM_BUTTON_TYPES = {
  SUBMIT: 'submit',
  RESET: 'reset', // reset is not handled yet
  BUTTON: 'button',
} as const;
```

#### label

Label of the button

#### action

A function that will be called when the button is clicked.

If the button is of type SUBMIT, on click the form will run all validations and if all fields are valid it will pass the form **data** object as a parameter to action function.

#### styles (Optional)

Will be passed as props to the button container (Box)

#### props

Will be passed into the Button component as props.

## errorsOrder

List of field names that are mandatory; the order of the fields in this array is used to scroll to error fields on submit. If this is not provided, scroll will happen to the first field that is found (based on the order they were added to the form).

## defaultValues

The **name of each field needs to be a key in defaultValues**, either with an empty string or an actual value (string | number).

## validationSchema

yup validation schema including any of the field names in the configuration object.

## getFormState

Optional callback that when passed through props will be called on Form unmount with the state and errors of the unmounted Form.

## defaultErrors

Can be passed to form (check examples) to display errors.

# TO DO
- Checkbox type
- Dynamic/smart input type
- Handle reset button type
- ...