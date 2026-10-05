import { Flex, FlexProps } from '@chakra-ui/react';
import { zodResolver } from '@hookform/resolvers/zod';
import { RestaurantsAnalyticsData, AnalyticsData } from '@whitbread-eos/api';
import { restaurantFormAnalytics, fetchFormattedDateValues } from '@whitbread-eos/utils';
import { useCallback, useEffect, useRef, useState } from 'react';
import { useForm, useWatch } from 'react-hook-form';
import * as z from 'zod';

import FormButtons from './FormButton';
import FormField from './FormField';
import { FORM_BUTTON_TYPES } from './formContants';
import { FormProps, SubmitBook } from './formTypes';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

export default function Form({
  id,
  elements: { fields, buttons },
  onChange,
  errorsOrder,
  defaultValues,
  validationSchema,
  getFormState,
  childrenToggle,
  optionalText,
  specialRequestLabel,
  enquiryHeading,
  enquirySubheading,
  testid,
  isEnquiry,
  setIsEnquiry,
  setIsMenuOptionAvailable,
}: Readonly<FormProps>) {
  const [formStepOneCompleted, setFormStepOneCompleted] = useState<boolean>(false);
  const [formStepOneValidationSchema, completeFormValidationSchema, enquiryFormValidationSchema] =
    validationSchema as z.AnyZodObject[];
  const updatedValidationScheema = formStepOneCompleted
    ? completeFormValidationSchema.merge(formStepOneValidationSchema)
    : formStepOneValidationSchema;
  const finalValidationSchema = isEnquiry
    ? updatedValidationScheema.merge(enquiryFormValidationSchema)
    : updatedValidationScheema;
  const {
    control,
    handleSubmit,
    getValues,
    formState: { errors },
    setValue,
    watch,
    clearErrors,
  } = useForm({
    mode: 'onBlur',
    reValidateMode: 'onBlur',
    defaultValues,
    resolver: finalValidationSchema ? zodResolver(finalValidationSchema) : undefined,
  });
  const watchValues = useWatch({
    control,
  });

  const errorsRef = useRef({});
  const submitButton = buttons?.find((button) => button.type === FORM_BUTTON_TYPES.SUBMIT);

  const errorFn = async (errors: any) => {
    let firstElementName;
    if (errorsOrder) {
      firstElementName = errorsOrder.find((el) => !!errors[el]);
    }
    if (!firstElementName) {
      firstElementName = Object.keys(errors)[0];
    }
    const firstErrorElement = document.querySelector(`[data-fieldname="${firstElementName}"]`);
    const headerOffset = 70;
    if (firstErrorElement) {
      const elementPosition = firstErrorElement.getBoundingClientRect().top;
      const offsetPosition = elementPosition + window.scrollY - headerOffset;
      window.scrollTo({ top: offsetPosition, behavior: 'smooth' });
    }
  };

  useEffect(() => {
    // analytics.update({ bookingFormState: 'booking' });
  }, []);

  useEffect(() => {
    const subscription = watch(() => {
      restaurantFormAnalytics?.update(getValues() as unknown as RestaurantsAnalyticsData);
      if (onChange) onChange(getValues());
    });
    return () => subscription.unsubscribe();
  }, [watchValues]);

  useEffect(() => {
    // analytics.update({
    //   largeGroupsEnquiry: isEnquiry,
    // });
  }, [isEnquiry]);

  useEffect(() => {
    return () => {
      getFormState?.(getValues(), errorsRef.current);
    };
  }, [getFormState, getValues]);

  useEffect(() => {
    errorsRef.current = errors;
  }, [errors]);

  const handleSetValue = useCallback(
    (fieldName: string, value: string | number | object) => {
      setValue(fieldName, value);
    },
    [setValue]
  );

  const submitContinue = () => {
    window.__satelliteLoaded && window._satellite.track('continueRestaurantDetails');
    if (errors) setFormStepOneCompleted(true);
  };

  const submitBook = (data: SubmitBook) => {
    submitButton?.action({
      ...data,
      isEnquiry,
      date: fetchFormattedDateValues(getValues('date') as string),
    });
    // analytics.update({
    //   isUserDataClickedOnSubmission: true,
    // });
  };

  return (
    <form
      id={id}
      onSubmit={handleSubmit(formStepOneCompleted ? submitBook : submitContinue, errorFn)}
      data-testid={testid}
    >
      <Flex direction="column" justify="center" align="center">
        <Flex width="100%" {...defaultFieldsContainerStyles}>
          {fields.map((formField) => (
            <FormField
              childrenToggle={childrenToggle}
              optionalText={optionalText}
              enquiryHeading={enquiryHeading}
              enquirySubheading={enquirySubheading}
              specialRequestLabel={specialRequestLabel}
              isEnquiry={isEnquiry}
              setIsEnquiry={setIsEnquiry}
              formStepOneCompleted={formStepOneCompleted}
              key={`${formField.name}-${formField.id || ''}`}
              control={control}
              formField={formField}
              errors={errors}
              clearErrors={clearErrors}
              getValues={getValues}
              setValue={setValue}
              handleSetValue={handleSetValue}
              setIsMenuOptionAvailable={setIsMenuOptionAvailable}
            />
          ))}
        </Flex>
        {buttons && (
          <FormButtons
            formStepOneCompleted={formStepOneCompleted}
            isEnquiry={isEnquiry}
            buttons={buttons}
          />
        )}
      </Flex>
    </form>
  );
}

const defaultFieldsContainerStyles = {
  direction: 'column',
  height: 'auto',
  justifyContent: 'flex-start',
  marginBottom: 'var(--chakra-space-lg)',
} as FlexProps;
