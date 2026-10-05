import { useWatch } from 'react-hook-form';

import FormField from '../FormField';
import { FormRelatedFieldsProps } from '../formTypes';

export default function FormRelatedFields({
  fieldName,
  relatedFields,
  control,
  errors,
  getValues,
  handleSetValue,
  handleResetField,
  handleSetError,
  handleClearErrors,
  reset,
}: Readonly<FormRelatedFieldsProps>) {
  const selectedValue = useWatch({ name: fieldName, control });
  if (!!relatedFields && !!selectedValue) {
    return (
      <>
        {relatedFields[selectedValue]?.map?.((field) => {
          return (
            <FormField
              key={`${field.name}-${field.id ?? ''}`}
              control={control}
              formField={field}
              errors={errors}
              handleSetValue={handleSetValue}
              handleResetField={handleResetField}
              handleSetError={handleSetError}
              handleClearErrors={handleClearErrors}
              getValues={getValues}
              reset={reset}
            />
          );
        })}
      </>
    );
  }
  return null;
}
