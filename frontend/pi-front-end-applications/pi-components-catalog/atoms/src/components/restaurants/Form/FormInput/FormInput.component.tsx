import { Flex } from '@chakra-ui/react';
import { Controller } from 'react-hook-form';

// import { analytics } from '~utils/analytics';
import Input from '../../Input';
import { FormFieldProps } from '../formTypes';

function FormInput({ control, formField, errors }: FormFieldProps) {
  return (
    <Flex
      direction="column"
      {...{ ...defaultStyles, ...formField.styles }}
      data-testid={formField.testid}
      data-fieldname={formField.name}
    >
      <Controller
        name={formField.name}
        control={control}
        render={({ field }) => {
          const { onChange, ...restOfField } = field;
          return (
            <Input
              {...formField.props}
              {...restOfField}
              onChange={(value: string | number | object) => {
                onChange(value);
                if (formField.name === 'telephoneNumber') {
                  // if (String(value).length) analytics.update({ isUserDataTel: true });
                  // else analytics.remove(['isUserDataTel']);
                }
              }}
              className={formField.className}
              optionalText={formField.optionalText}
              label={formField.label}
              optional={formField.optional}
              error={errors?.[formField.name]?.message}
            />
          );
        }}
      />
    </Flex>
  );
}

const defaultStyles = {
  marginBottom: 'var(--chakra-space-lg)',
};

export default FormInput;
