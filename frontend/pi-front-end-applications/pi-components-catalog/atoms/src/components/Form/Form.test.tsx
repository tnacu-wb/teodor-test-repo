import { StyleProps } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import Button from '../Button';
import Form from './Form.component';
import { FORM_BUTTON_TYPES, FORM_FIELD_TYPES } from './formConstants';
import { ButtonsType, FieldsType, FormProps } from './formTypes';

const Component = () => {
  const fields: FieldsType[] = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'fieldName',
      label: 'Field Name',
    },
    {
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
    },
  ];

  const buttonStyle: StyleProps = { color: 'blue' };

  const buttons: ButtonsType[] = [
    {
      type: FORM_BUTTON_TYPES.SUBMIT,
      label: 'Submit',
      action: jest.fn(),
      styles: buttonStyle,
      props: {
        variant: 'primary',
        size: 'sm',
      },
    },
  ];

  const props: FormProps = {
    elements: {
      fields,
      buttons,
    },
    defaultValues: {},
  };

  return <Form {...props} />;
};

const MockButton = () => (
  <Button id="clearSearch" name="clearSearch" size="md" variant="primary">
    Clear Search
  </Button>
);

const ComponentWithBottomFields = () => {
  const fields: FieldsType[] = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'fieldName',
      label: 'Field Name',
    },
    {
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
    },
  ];

  const props: FormProps = {
    elements: {
      fields,
      bottomFields: [
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          label: 'Clear Search',
          name: 'clearSearch',
          testid: 'ClearSearch',
          Component: MockButton,
        },
      ],
    },
    defaultValues: {},
  };

  return <Form {...props} />;
};

describe('Form', () => {
  it('should render the component', () => {
    const { getByRole } = render(<Component />);
    const radioGroup = getByRole('radiogroup');

    expect(radioGroup).toBeInTheDocument();
  });

  it('should render a bottom field if given', () => {
    const { getByRole } = render(<ComponentWithBottomFields />);
    expect(getByRole('button', { name: 'Clear Search' })).toBeInTheDocument();
  });
});
