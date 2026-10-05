import { Flex, StyleProps } from '@chakra-ui/react';
import { Controller } from 'react-hook-form';

import Textarea from '../../Textarea';
import { FormTextAreaProps } from '../formTypes';

export default function FormTextArea({ control, formField, errors }: Readonly<FormTextAreaProps>) {
  const errorStyles =
    formField.props?.useTooltip && !!errors?.[formField.name]?.message ? tooltipErrorStyles : {};

  return (
    <Flex
      direction="column"
      {...{ ...defaultStyles, ...formField.styles, ...errorStyles }}
      data-fieldname={formField.name}
      data-testid={formField.testid}
    >
      <Controller
        name={formField.name}
        control={control}
        render={({ field }) => {
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          const { onChange, ref: _, ...extraPropsField } = field;
          return (
            <Textarea
              resize="none"
              {...textareaStyle}
              {...formField.props}
              {...extraPropsField}
              value={extraPropsField.value || ''}
              placeholder={formField.label}
              error={errors?.[formField.name]?.message}
              onChange={(o) => {
                formField.onChange?.(o.target.value);
                onChange(o.target.value);
              }}
              maxLength={formField.props?.maxLength}
            />
          );
        }}
      />
    </Flex>
  );
}

const textareaStyle = {
  height: '18.75rem',
  border: '1px solid var(--chakra-colors-lightGrey1)',
};

const tooltipErrorStyles = {
  marginBottom: 'var(--chakra-space-6xl)',
} as StyleProps;

const defaultStyles = {
  marginBottom: 'var(--chakra-space-md)',
};
