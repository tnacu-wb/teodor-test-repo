import '@testing-library/jest-dom';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render, screen, userEvent } from '../../../utils/test-utils';
import { FORM_FIELD_TYPES } from '../formConstants';
import { FormRelatedFieldsProps } from '../formTypes';
import FormRelatedFields from './FormRelatedFields.component';

const Component = () => {
  const {
    control,
    register,
    handleSubmit,
    getValues,
    formState: { errors },
  } = useForm();

  const relatedFields = {
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
  };

  const props: FormRelatedFieldsProps = {
    fieldName: 'details',
    relatedFields,
    control,
    errors,
    getValues,
  };

  return (
    <>
      <form onSubmit={handleSubmit(jest.fn)}>
        <label aria-labelledby="details" htmlFor="details">
          Details
        </label>
        <input id="details" defaultValue="" {...register('details')} />
        <FormRelatedFields {...props} />
      </form>
    </>
  );
};

describe('Form Related Fields', () => {
  it('should show related fields only when given field has same value', async () => {
    const { getByLabelText } = render(<Component />);
    const detailsInput = getByLabelText('Details');
    const relatedFieldName = 'Company Address Details';

    expect(screen.queryByText(relatedFieldName)).not.toBeInTheDocument();
    userEvent.type(detailsInput, 'companyAddress');
    await expect(screen.queryByText(relatedFieldName)).toBeInTheDocument();
  });
});
