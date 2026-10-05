import { Flex, FormLabel, Text } from '@chakra-ui/react';
import { Controller } from 'react-hook-form';

import Textarea from '../../../Textarea/Textarea.component';
import { FormFieldProps } from '../formTypes';

export default function FormTextarea({ control, formField }: FormFieldProps) {
  const { label, name, optionalText } = formField;
  return (
    <Flex direction="column" {...formField.styles} data-fieldname={formField.name}>
      {label && (
        <FormLabel {...labelStyle()} htmlFor={name}>
          <Text fontWeight="bold">{label}</Text>
          <Text>({optionalText})</Text>
        </FormLabel>
      )}
      <Controller
        name={formField.name}
        control={control}
        render={({ field }) => {
          return <Textarea {...formField.props} {...field} data-testid={formField.testid} />;
        }}
      />
    </Flex>
  );
}

const labelStyle = () => ({
  display: 'flex',
  w: 'fit-content',
  fontSize: 'md',
  align: 'center',
  zIndex: '1',
  color: '#333333',
});
