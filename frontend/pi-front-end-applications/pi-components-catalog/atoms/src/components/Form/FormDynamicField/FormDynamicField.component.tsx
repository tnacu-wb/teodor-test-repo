import { Box, Flex, StyleProps } from '@chakra-ui/react';
import { Controller } from 'react-hook-form';

import { FormDynamicFieldProps } from '../formTypes';

export default function FormDynamicField({
  control,
  formField,
  errors,
  handleSetValue,
  handleResetField,
  handleSetError,
  handleClearErrors,
  handleTriggerValidation,
  getValues,
  reset,
}: Readonly<FormDynamicFieldProps>) {
  const errorStyles =
    formField?.props?.useTooltip && !!errors?.[formField.name]?.message ? tooltipErrorStyles : {};

  return (
    <Flex
      direction="column"
      {...{ ...defaultStyles, ...formField.styles, ...errorStyles }}
      data-fieldname={formField.name}
    >
      <Controller
        name={formField.name}
        control={control}
        render={({ field }) => {
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          const { ref: _, ...extraPropsField } = field;
          return (
            <Box data-testid={formField.testid}>
              {!!formField.Component && (
                <formField.Component
                  formField={formField}
                  field={extraPropsField}
                  control={control}
                  errors={errors}
                  handleSetValue={handleSetValue}
                  handleResetField={handleResetField}
                  handleSetError={handleSetError}
                  handleClearErrors={handleClearErrors}
                  handleTriggerValidation={handleTriggerValidation}
                  getValues={getValues}
                  reset={reset}
                />
              )}
            </Box>
          );
        }}
      />
    </Flex>
  );
}

const defaultStyles = {
  marginBottom: 'var(--chakra-space-md)',
};

const tooltipErrorStyles = {
  marginBottom: 'var(--chakra-space-6xl)',
} as StyleProps;
