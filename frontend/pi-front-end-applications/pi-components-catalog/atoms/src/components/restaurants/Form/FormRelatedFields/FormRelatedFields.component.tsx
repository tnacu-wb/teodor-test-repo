import React, { useEffect } from 'react';

import FormField from '../FormField';
import { FormRelatedFieldsProps } from '../formTypes';

function FormRelatedFields({
  fieldName,
  relatedFields,
  control,
  errors,
  getValues,
  handleSetValue,
  handleResetField,
}: FormRelatedFieldsProps) {
  useEffect(() => {
    if (fieldName !== 'tableBookingForm') {
      const firstErrorElement = document.querySelector(
        `[data-fieldname="${relatedFields[0].name}"]`
      );

      const headerOffset = 70;
      if (firstErrorElement) {
        const elementPosition = firstErrorElement.getBoundingClientRect().top;
        const offsetPosition = elementPosition + window.scrollY - headerOffset;
        window.scrollTo({ top: offsetPosition, behavior: 'smooth' });
      }
    }
  }, []);
  return (
    <>
      {relatedFields?.map?.((field) => (
        <FormField
          key={`${field.name}-${field.id}`}
          control={control}
          formField={field}
          errors={errors}
          handleSetValue={handleSetValue}
          handleResetField={handleResetField}
          getValues={getValues}
        />
      ))}
    </>
  );
}

export default FormRelatedFields;
