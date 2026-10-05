import { FORM_FIELD_TYPES } from '..';

import Button from '../../Button';
import FormCheckbox from '../FormCheckbox';
import FormDropdown from '../FormDropdown/FormDropdown.component';
import FormDynamicField from '../FormDynamicField';
import FormInput from '../FormInput';
import FormRadioGroup from '../FormRadioGroup';
import FormRelatedFields from '../FormRelatedFields';
import FormTextArea from '../FormTextArea';
import { FormFieldProps } from '../formTypes';

export default function FormField({
  control,
  formField,
  errors,
  getValues,
  handleSetValue,
  handleResetField,
  handleSetError,
  handleClearErrors,
  handleTriggerValidation,
  reset,
  btnProps,
  onChangeAction,
}: Readonly<FormFieldProps>) {
  const { type } = formField;

  if (formField.hidden === true) {
    return null;
  }
  switch (type) {
    case FORM_FIELD_TYPES.BUTTON:
    case FORM_FIELD_TYPES.SUBMIT:
      return (
        <Button data-testid={formField.testid} {...btnProps}>
          {btnProps?.label}
        </Button>
      );
    case FORM_FIELD_TYPES.INPUT_TEXT:
    case FORM_FIELD_TYPES.INPUT_EMAIL:
    case FORM_FIELD_TYPES.INPUT_PASSWORD:
      return <FormInput control={control} formField={formField} errors={errors} />;
    case FORM_FIELD_TYPES.RADIO_GROUP: {
      return (
        <>
          <FormRadioGroup
            control={control}
            formField={formField}
            errors={errors}
            handleSetValue={handleSetValue}
            onChangeAction={onChangeAction}
          />
          {!!formField.relatedFields && (
            <FormRelatedFields
              fieldName={formField.name}
              relatedFields={formField.relatedFields}
              control={control}
              errors={errors}
              handleSetValue={handleSetValue}
              handleResetField={handleResetField}
              getValues={getValues}
              reset={reset}
            />
          )}
        </>
      );
    }
    case FORM_FIELD_TYPES.CHECKBOX: {
      return (
        <>
          <FormCheckbox control={control} formField={formField} errors={errors} />
          {!!formField.relatedFields && (
            <FormRelatedFields
              fieldName={formField.name}
              relatedFields={formField.relatedFields}
              control={control}
              errors={errors}
              handleSetValue={handleSetValue}
              handleResetField={handleResetField}
              getValues={getValues}
              reset={reset}
            />
          )}
        </>
      );
    }
    case FORM_FIELD_TYPES.NON_FIELD_CONTENT:
      return <>{formField.content}</>;
    case FORM_FIELD_TYPES.DROPDOWN:
      return (
        <FormDropdown
          formField={formField}
          control={control}
          errors={errors}
          handleSetValue={handleSetValue}
          onChangeAction={onChangeAction}
        />
      );
    case FORM_FIELD_TYPES.DYNAMIC_FIELD:
      return (
        <>
          <FormDynamicField
            control={control}
            formField={formField}
            errors={errors}
            handleSetValue={handleSetValue}
            handleResetField={handleResetField}
            handleSetError={handleSetError}
            handleClearErrors={handleClearErrors}
            handleTriggerValidation={handleTriggerValidation}
            getValues={getValues}
            reset={reset}
          />
          {!!formField.relatedFields && (
            <FormRelatedFields
              fieldName={formField.name}
              relatedFields={formField.relatedFields}
              control={control}
              errors={errors}
              handleSetValue={handleSetValue}
              handleResetField={handleResetField}
              handleSetError={handleSetError}
              handleClearErrors={handleClearErrors}
              getValues={getValues}
              reset={reset}
            />
          )}
        </>
      );
    case FORM_FIELD_TYPES.TEXTAREA:
      return <FormTextArea control={control} formField={formField} errors={errors} />;
    default:
      return null;
  }
}
