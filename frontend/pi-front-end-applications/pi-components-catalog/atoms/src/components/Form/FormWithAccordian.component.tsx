import {
  AccordionPanelProps,
  AccordionProps,
  Box,
  BoxProps,
  ButtonProps,
  Flex,
  FlexProps,
  TextProps,
} from '@chakra-ui/react';
import { yupResolver } from '@hookform/resolvers/yup';
import { useCallback, useEffect, useRef } from 'react';
import { useForm, useWatch } from 'react-hook-form';

import Accordion from '../Accordion';
import FormField from './FormField';
import { FORM_BUTTON_TYPES } from './formConstants';
import { FormWithAccordianProps } from './formTypes';

export const FormWithAccordian = ({
  id,
  accordians,

  onChange = () => {},
  defaultValues,
  accordionIndex,
  handleAccordionToggle,
  validationSchema,
  getFormState,
  testid,
  autoComplete = 'on',
  fieldsetDisabled = false,
}: Readonly<FormWithAccordianProps>) => {
  const {
    control,
    handleSubmit,
    getValues,
    formState: { errors },
    setError,
    clearErrors,
    setValue,
    watch,
    trigger,
  } = useForm({
    mode: 'onBlur',
    defaultValues,
    resolver: validationSchema ? yupResolver(validationSchema) : undefined,
  });

  const watchValuesForFormWithAccordian = useWatch({
    control,
  });

  const errorsReference = useRef({});
  const accordianFields = accordians.flatMap((accordian) => accordian.elements.fields);
  const customSubmitButtonWithAccordian = accordianFields?.find(
    (field) => field?.type === FORM_BUTTON_TYPES.SUBMIT
  );

  const noSubmitActionWithAccordian = () =>
    console.error('Form does not have a submit button with an action set');

  const errorFnForAccordians = (errors: any) => {
    const firstElementName = Object.keys(errors)[0];
    const errorAccordianIndex = accordians.findIndex((accordian) =>
      accordian.elements.fields.some((field) => field.name === firstElementName)
    );
    handleAccordionToggle(errorAccordianIndex);
    setTimeout(() => {
      const firstErrorElement = document.querySelector(`[data-fieldname="${firstElementName}"]`);
      firstErrorElement?.scrollIntoView?.({ behavior: 'smooth' });
    }, 50);
  };

  useEffect(() => {
    const subscription = watch(() => {
      if (onChange) {
        onChange(getValues());
      }
    });
    return () => subscription.unsubscribe();
  }, [watchValuesForFormWithAccordian]);

  useEffect(() => {
    return () => {
      getFormState?.(getValues(), errorsReference.current);
    };
  }, [getFormState, getValues]);

  useEffect(() => {
    errorsReference.current = errors;
  }, [errors]);

  const handleAccordianTriggerValidation = useCallback(
    (fieldsName: string | string[]) => {
      trigger(fieldsName);
    },
    [trigger]
  );

  const handleSetValueWithAccordian = useCallback(
    (fieldName: string, value: string | number | object) => {
      setValue(fieldName, value);
    },
    [setValue]
  );

  const handleSetErrorWithAccordian = useCallback(
    (fieldName: string, error: Record<string, string>, config?: { shouldFocus: boolean }) => {
      setError(fieldName, error, config);
    },
    [setError]
  );

  const handleClearErrorsWithAccordian = useCallback(
    (fieldName?: string | string[]) => {
      clearErrors(fieldName);
    },
    [clearErrors]
  );

  const accordianSubmitFn = customSubmitButtonWithAccordian?.action ?? noSubmitActionWithAccordian;

  function renderAccordianContent({ fields, formStyles, fieldsContainerStyles }: any) {
    return (
      <Box {...formStyles}>
        <fieldset disabled={fieldsetDisabled} style={styleFieldsetWithAccordian(fieldsetDisabled)}>
          <Flex {...{ ...defaultFieldsContainerStylesWithAccordian, ...fieldsContainerStyles }}>
            {fields.map((formField: any) => {
              return (
                <FormField
                  key={`${formField.name}-${formField.id ?? ''}`}
                  formField={formField}
                  control={control}
                  getValues={getValues}
                  handleSetValue={handleSetValueWithAccordian}
                  errors={errors}
                  handleClearErrors={handleClearErrorsWithAccordian}
                  handleSetError={handleSetErrorWithAccordian}
                  handleTriggerValidation={handleAccordianTriggerValidation}
                  onChangeAction={formField?.onChangeAction}
                  btnProps={{
                    onClick:
                      formField.type === FORM_BUTTON_TYPES.BUTTON
                        ? () => formField.action(trigger)
                        : undefined,
                    varient: 'primary',
                    isDisabled: formField.props?.isDisabled ?? false,
                    label: formField.label,
                    size: 'full',
                    type: formField.type,
                  }}
                />
              );
            })}
          </Flex>
        </fieldset>
      </Box>
    );
  }

  function renderAccordionItems() {
    const accordionItems = accordians.map((accordian) => ({
      title: accordian.title,
      content: renderAccordianContent(accordian.elements),
      onToggleSection: accordian.onToggleSection,
    }));
    return accordionItems;
  }
  return (
    <form
      id={id}
      onSubmit={handleSubmit(accordianSubmitFn, errorFnForAccordians)}
      data-testid={testid}
      autoComplete={autoComplete}
    >
      <Accordion
        allowMultiple={true}
        accordionItems={renderAccordionItems()}
        bgColor={'baseWhite'}
        accordionOverwriteStyles={{
          ...accordionOverwriteStyles,
          container: { ...accordionOverwriteStyles?.container, index: [accordionIndex] },
        }}
      />
    </form>
  );
};

const defaultFieldsContainerStylesWithAccordian = {
  direction: 'column',
  height: 'auto',
  justifyContent: 'flex-start',
  marginBottom: 'var(--chakra-space-lg)',
} as FlexProps;

const defaultFieldsetStyleWithAccordian = {
  border: '0',
  padding: '0.01em 0 0 0',
  margin: '0',
  minWidth: '0',
};

const styleFieldsetWithAccordian = (isDisabled: boolean) => {
  return isDisabled
    ? { ...{ opacity: '0.4', pointerEvents: 'none' }, ...defaultFieldsetStyleWithAccordian }
    : defaultFieldsetStyleWithAccordian;
};

const accordionContainerStyle = {
  border: '1px solid var(--chakra-colors-lightGrey2)',
  borderTop: 0,
} as AccordionProps;

const accordionItemButtonStyle = {
  sx: {
    div: { color: 'darkGrey2' },
  },
  _expanded: {
    borderBottom: '1px solid var(--chakra-colors-lightGrey3)',
  },
  pl: 'md',
  fontSize: '2xl',
  fontWeight: '600',
} as BoxProps & ButtonProps;

const accordionItemTextStyle = {
  fontWeight: 'semibold',
  fontSize: '2xl',
  borderTop: 0,
} as TextProps;

const accordionItemPanelStyle = {
  padding: '0 var(--chakra-space-lg)',
} as AccordionPanelProps;

const accordionItemStyle = {
  borderBottom: 0,
  borderTop: '1px solid var(--chakra-colors-lightGrey2)',
};

const accordionOverwriteStyles = {
  container: accordionContainerStyle,
  button: accordionItemButtonStyle,
  text: accordionItemTextStyle,
  panel: accordionItemPanelStyle,
  item: accordionItemStyle,
};
