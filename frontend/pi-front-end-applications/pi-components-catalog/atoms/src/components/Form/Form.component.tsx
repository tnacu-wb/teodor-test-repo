import { Box, Flex, FlexProps } from '@chakra-ui/react';
import { yupResolver } from '@hookform/resolvers/yup';
import { useCallback, useEffect, useRef } from 'react';
import { useForm, useWatch } from 'react-hook-form';

import FormButtons from './FormButtons';
import FormField from './FormField';
import { FORM_BUTTON_TYPES } from './formConstants';
import { FormProps } from './formTypes';

export default function Form({
  id,
  elements: {
    formStyles,
    fieldsContainerStyles,
    buttonsContainerStyles,
    fields,
    buttons,
    bottomFields,
    onSubmitAction,
  },

  onChange = () => {},
  errorsOrder,
  defaultValues,
  validationSchema,
  getFormState,
  defaultErrors,
  resetForm,
  testid,
  autoComplete = 'on',
  fieldsetDisabled = false,
}: Readonly<FormProps>) {
  const {
    control,
    handleSubmit,
    getValues,
    formState: { errors },
    setError,
    clearErrors,
    setValue,
    reset,
    resetField,
    watch,
    trigger,
  } = useForm({
    mode: 'onBlur',
    defaultValues,
    resolver: validationSchema ? yupResolver(validationSchema) : undefined,
  });

  const watchValues = useWatch({
    control,
  });

  const errorsRef = useRef({});
  const submitButton = buttons?.find((button) => button?.type === FORM_BUTTON_TYPES.SUBMIT);
  const customSubmitButton = fields?.find(
    (field) => field?.props?.type === FORM_BUTTON_TYPES.SUBMIT
  );

  const noSubmitAction = () =>
    console.error('Form does not have a submit button with an action set');

  const errorFn = (errors: any) => {
    let firstElementName;
    if (errorsOrder) {
      firstElementName = errorsOrder.find((el) => !!errors[el]);
    }
    if (!firstElementName) {
      firstElementName = Object.keys(errors)[0];
    }
    const firstErrorElement = document.querySelector(`[data-fieldname="${firstElementName}"]`);
    firstErrorElement?.scrollIntoView?.({ behavior: 'smooth' });
  };

  useEffect(() => {
    const subscription = watch(() => {
      if (onChange) {
        onChange(getValues());
      }
    });
    return () => subscription.unsubscribe();
  }, [watchValues]);

  useEffect(() => {
    if (defaultErrors) {
      for (const fieldName in defaultErrors) {
        setError(fieldName, defaultErrors[fieldName]);
      }
    }
  }, [defaultErrors]);

  useEffect(() => {
    return () => {
      getFormState?.(getValues(), errorsRef.current);
    };
  }, [getFormState, getValues]);

  useEffect(() => {
    errorsRef.current = errors;
  }, [errors]);

  useEffect(() => {
    if (resetForm) {
      const resetValues: { [key: string]: string } = {};
      for (const key in defaultValues) {
        resetValues[key] = '';
      }
      reset(resetValues);
    }
  }, [resetForm]);

  const handleTriggerValidation = useCallback(
    (fieldsName: string | string[]) => {
      trigger(fieldsName);
    },
    [trigger]
  );

  const handleSetValue = useCallback(
    (fieldName: string, value: string | number | object) => {
      setValue(fieldName, value);
    },
    [setValue]
  );

  const handleResetField = useCallback(
    (fieldName: string, options?: Record<string, boolean>) => {
      resetField(fieldName, options);
    },
    [resetField]
  );

  const handleSetError = useCallback(
    (fieldName: string, error: Record<string, string>, config?: { shouldFocus: boolean }) => {
      setError(fieldName, error, config);
    },
    [setError]
  );

  const handleClearErrors = useCallback(
    (fieldName?: string | string[]) => {
      clearErrors(fieldName);
    },
    [clearErrors]
  );

  const submitFn =
    submitButton?.action ?? customSubmitButton?.action ?? onSubmitAction ?? noSubmitAction;

  return (
    <form
      id={id}
      onSubmit={handleSubmit(submitFn, errorFn)}
      data-testid={testid}
      autoComplete={autoComplete}
    >
      <Box {...formStyles}>
        <fieldset disabled={fieldsetDisabled} style={styleFieldset(fieldsetDisabled)}>
          <Flex {...{ ...defaultFieldsContainerStyles, ...fieldsContainerStyles }}>
            {fields.map((formField) => {
              return (
                <FormField
                  key={`${formField.name}-${formField.id ?? ''}`}
                  control={control}
                  formField={formField}
                  errors={errors}
                  getValues={getValues}
                  handleSetValue={handleSetValue}
                  handleResetField={handleResetField}
                  handleSetError={handleSetError}
                  handleClearErrors={handleClearErrors}
                  handleTriggerValidation={handleTriggerValidation}
                  reset={reset}
                />
              );
            })}
          </Flex>
        </fieldset>
        {buttons && (
          <FormButtons buttonsContainerStyles={buttonsContainerStyles} buttons={buttons} />
        )}
        {bottomFields && (
          <Flex {...{ ...defaultFieldsContainerStyles, ...fieldsContainerStyles }}>
            {bottomFields.map((formField) => {
              return (
                <FormField
                  key={`${formField.name}-${formField.id ?? ''}`}
                  control={control}
                  formField={formField}
                  errors={errors}
                  getValues={getValues}
                  handleSetValue={handleSetValue}
                  handleResetField={handleResetField}
                  reset={reset}
                />
              );
            })}
          </Flex>
        )}
      </Box>
    </form>
  );
}

const defaultFieldsContainerStyles = {
  direction: 'column',
  height: 'auto',
  justifyContent: 'flex-start',
  marginBottom: 'var(--chakra-space-lg)',
} as FlexProps;

const defaultFieldsetStyle = {
  border: '0',
  padding: '0.01em 0 0 0',
  margin: '0',
  minWidth: '0',
};

const styleFieldset = (isDisabled: boolean) => {
  return isDisabled
    ? { ...{ opacity: '0.4', pointerEvents: 'none' }, ...defaultFieldsetStyle }
    : defaultFieldsetStyle;
};
