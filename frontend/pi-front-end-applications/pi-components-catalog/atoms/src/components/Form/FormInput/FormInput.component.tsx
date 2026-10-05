import { Flex, StyleProps } from '@chakra-ui/react';
import { Controller } from 'react-hook-form';

import { FORM_FIELD_TYPES } from '../formConstants';
import { FormInputProps } from '../formTypes';
import Input from './../../Input';

export default function FormInput({ control, formField, errors }: Readonly<FormInputProps>) {
  const errorStyles =
    formField.props?.useTooltip && !!errors?.[formField.name]?.message ? tooltipErrorStyles : {};

  const emailFieldProps =
    formField.type === FORM_FIELD_TYPES.INPUT_EMAIL
      ? {
          inputMode: 'email' as const,
          autoCapitalize: 'off',
          autoCorrect: 'off',
          autoComplete: 'email',
        }
      : {};

  return (
    <Flex
      direction="column"
      {...{ ...defaultStyles, ...formField.styles, ...errorStyles }}
      data-testid={formField.testid}
      data-fieldname={formField.name}
    >
      <Controller
        name={formField.name}
        control={control}
        render={({ field }) => {
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          const { ref: _, ...extraPropsField } = field;
          return (
            <Input
              {...formField.props}
              {...extraPropsField}
              type={
                formField.type === FORM_FIELD_TYPES.INPUT_EMAIL
                  ? FORM_FIELD_TYPES.INPUT_TEXT
                  : formField.type
              }
              value={extraPropsField.value || ''}
              placeholderText={formField.label}
              label={formField.label}
              error={errors?.[formField.name]?.message}
              emailFieldProps={emailFieldProps}
            />
          );
        }}
      />
    </Flex>
  );
}

const tooltipErrorStyles = {
  marginBottom: 'var(--chakra-space-6xl)',
} as StyleProps;

const defaultStyles = {
  marginBottom: 'var(--chakra-space-md)',
};
