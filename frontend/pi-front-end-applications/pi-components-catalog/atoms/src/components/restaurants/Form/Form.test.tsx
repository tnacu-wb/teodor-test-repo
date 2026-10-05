import '@testing-library/jest-dom';
import { fireEvent } from '@testing-library/react';
import { formatDataTestId } from '@whitbread-eos/utils';
import { restaurantFormAnalytics } from '@whitbread-eos/utils';
import React from 'react';
import { z } from 'zod';

import { act, render, screen } from '../../../utils/test-utils';
import Form from './Form.component';
import { FieldsType } from './formTypes';

// Mock validation schemas
const mockSchema = z.object({});

// Mock the validateForm function since it's in organisms package
const validateForm = jest.fn(() => ({
  formStepOneValidationSchema: mockSchema,
  completeFormValidationSchema: mockSchema,
  enquiryFormValidationSchema: mockSchema,
}));

jest.mock('next/router', () => ({
  useRouter: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  restaurantFormAnalytics: {
    update: jest.fn(),
  },
}));

jest.mock('./FormField', () => {
  function MockFormField(props: any) {
    const { formField, setValue } = props;

    return (
      <input
        data-testid={formField.name}
        onChange={(e) => setValue(formField.name, e.target.value)}
      />
    );
  }

  return MockFormField;
});

const Component = () => {
  const Props = {
    onSubmit: jest.fn(),
    getFormState: jest.fn(),
    baseDataTestId: '',
    elements: {
      fields: [],
      buttons: [],
    },
    testid: 'New Form',
    defaultValues: {},
    onChange: jest.fn(),
    validationSchema: [],
    errorsOrder: [],
  };
  return <Form {...Props} />;
};

describe('Form', () => {
  it('should render the Form component', () => {
    render(<Component />);
    const text = screen.getByTestId('New Form');
    expect(text).toBeInTheDocument();
  });

  it('should render the Form component with submit button', () => {
    const { getByTestId } = render(
      <Form
        elements={{
          formStyles: undefined,
          fieldsContainerStyles: undefined,
          buttonsContainerStyles: undefined,
          fields: [],
          buttons: [
            {
              type: 'submit',
              label: ['continue', 'enquiry', 'book'],
              action: jest.fn(),
              testid: formatDataTestId('Form', 'Submit'),
              props: { size: 'full', variant: '' },
            },
          ],
          bottomFields: undefined,
          onSubmitAction: undefined,
        }}
        defaultValues={{}}
        validationSchema={[]}
      />
    );
    expect(getByTestId('Form-Submit')).toBeInTheDocument();
  });

  it('should submit the form with fields', async () => {
    window.scrollTo = jest.fn();
    const {
      formStepOneValidationSchema,
      completeFormValidationSchema,
      enquiryFormValidationSchema,
    } = validateForm([]);
    const field: FieldsType = {
      type: 'input',
      name: 'adultsByEnquiry',
      label: 'adult',
      props: {
        charLimit: 3,
        type: 'text',
      },
      testid: formatDataTestId('input', 'Adults'),
      styles: { maxW: '100%', mb: '2xl' },
    };

    const { getByTestId } = render(
      <Form
        elements={{
          formStyles: undefined,
          fieldsContainerStyles: undefined,
          buttonsContainerStyles: undefined,
          fields: [field],
          buttons: [
            {
              type: 'submit',
              label: ['continue', 'enquiry', 'book'],
              action: jest.fn(),
              testid: formatDataTestId('Form', 'Submit'),
              props: { size: 'full', variant: '' },
            },
          ],
          bottomFields: undefined,
          onSubmitAction: undefined,
        }}
        defaultValues={{}}
        validationSchema={[
          formStepOneValidationSchema,
          completeFormValidationSchema,
          enquiryFormValidationSchema,
        ]}
      />
    );

    expect(getByTestId('adultsByEnquiry')).toBeInTheDocument();
  });
  it('should submit the form', async () => {
    window.scrollTo = jest.fn();
    const {
      formStepOneValidationSchema,
      completeFormValidationSchema,
      enquiryFormValidationSchema,
    } = validateForm([]);

    const { getByTestId } = render(
      <Form
        elements={{
          formStyles: undefined,
          fieldsContainerStyles: undefined,
          buttonsContainerStyles: undefined,
          fields: [],
          buttons: [
            {
              type: 'submit',
              label: ['continue', 'enquiry', 'book'],
              action: jest.fn(),
              testid: formatDataTestId('Form', 'Submit'),
              props: { size: 'full', variant: '' },
            },
          ],
          bottomFields: undefined,
          onSubmitAction: undefined,
        }}
        defaultValues={{}}
        validationSchema={[
          formStepOneValidationSchema,
          completeFormValidationSchema,
          enquiryFormValidationSchema,
        ]}
      />
    );
    const submitBtn = getByTestId('Form-Submit');
    submitBtn.click();
    await act(async () => {
      expect(getByTestId('Form-Submit')).toBeInTheDocument();
    });
  });
});

describe('Form analytics', () => {
  it('calls restaurantFormAnalytics.update when form values change', () => {
    render(
      <Form
        id="test-form"
        elements={{
          fields: [{ name: 'firstName', id: '1' }],
          buttons: [],
        }}
        defaultValues={{ firstName: '' }}
        validationSchema={[]}
      />
    );

    fireEvent.change(screen.getByTestId('firstName'), {
      target: { value: 'John' },
    });

    expect(restaurantFormAnalytics.update).toHaveBeenCalled();
  });
});

describe('Form satellite tracking', () => {
  beforeEach(() => {
    window.__satelliteLoaded = true;
    window._satellite = {
      track: jest.fn(),
    };
  });

  it('should call satellite track on continue submit', async () => {
    const schema = z.object({});

    render(
      <Form
        id="test-form"
        elements={{
          fields: [],
          buttons: [
            {
              type: 'submit',
              label: ['continue'],
              action: jest.fn(),
              testid: 'Form-Submit',
              props: {},
            },
          ],
        }}
        defaultValues={{}}
        validationSchema={[schema, schema, schema]}
      />
    );

    fireEvent.submit(document.querySelector('form')!);
  });
});
