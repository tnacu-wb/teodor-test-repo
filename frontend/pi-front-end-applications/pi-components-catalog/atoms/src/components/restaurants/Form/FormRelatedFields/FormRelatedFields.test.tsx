import '@testing-library/jest-dom';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render } from '../../../../utils/test-utils';
import { FORM_FIELD_TYPES } from '../formContants';
import { FormRelatedFieldsProps } from '../formTypes';
import FormRelatedFields from './FormRelatedFields.component';

const Component = () => {
  const {
    control,
    handleSubmit,
    getValues,
    formState: { errors },
  } = useForm();

  const relatedFields = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'firstname',
      label: 'First Name',
      charLimit: 99,
    },
  ];

  const props: FormRelatedFieldsProps = {
    fieldName: 'Your Details',
    relatedFields,
    control,
    handleSetValue: jest.fn(),
    errors,
    getValues,
  };

  return (
    <>
      <form onSubmit={handleSubmit(jest.fn)}>
        <label aria-labelledby="details" htmlFor="details">
          Your Details
        </label>
        <FormRelatedFields {...props} />
      </form>
    </>
  );
};

describe('Form Related Fields', () => {
  it('should show related fields', () => {
    const { queryByText } = render(<Component />);
    const relatedFieldName = 'First Name';
    expect(queryByText(relatedFieldName)).toBeInTheDocument();
  });
});
