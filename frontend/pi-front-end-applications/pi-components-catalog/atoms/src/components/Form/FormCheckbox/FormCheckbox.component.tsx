import { Box, Flex, Text } from '@chakra-ui/react';
import { Controller } from 'react-hook-form';

import Checkbox from '../../Checkbox';
import FormError from '../FormError';
import { FormCheckboxProps } from '../formTypes';

export default function FormCheckbox({ control, formField, errors }: Readonly<FormCheckboxProps>) {
  return (
    <Flex
      direction="column"
      {...{ ...defaultStyles, ...formField.styles }}
      data-testid={formField.testid}
    >
      <Controller
        name={formField.name}
        control={control}
        render={({ field }) => {
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          const { onChange, ref: _, ...extraPropsField } = field;

          return (
            <Box data-testid={formField.testid} data-fieldname={formField.name}>
              <Checkbox
                {...formField.props}
                {...extraPropsField}
                onChange={(o) => {
                  formField.onChange?.(o.target.checked);
                  onChange(o.target.checked);
                }}
              >
                <Text as="div">{formField.label}</Text>
              </Checkbox>
              <FormError errors={errors} name={formField.name} />
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
