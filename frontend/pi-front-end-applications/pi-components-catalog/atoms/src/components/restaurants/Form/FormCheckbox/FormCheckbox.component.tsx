import { Flex, Box, Text, Link, FormLabel } from '@chakra-ui/react';
import { Controller } from 'react-hook-form';

import Checkbox from '../../Checkbox';
import FormError from '../FormError';
import { FormFieldProps } from '../formTypes';

export default function FormCheckbox({ control, formField, errors }: FormFieldProps) {
  return (
    <Flex
      direction="column"
      {...{ ...defaultStyles, ...formField.styles }}
      data-testid={formField.testid}
    >
      <Controller
        name={formField.name}
        control={control}
        render={({ field: { onChange, onBlur, value, ...others } }) => {
          return (
            <Box display="flex" data-testid={formField.testid} data-fieldname={formField.name}>
              <Checkbox
                onBlur={onBlur} // notify when input is touched
                onChange={onChange} // send value to hook form
                isChecked={value}
                {...others}
                {...formField.props}
              ></Checkbox>
              <Flex direction="column">
                <FormLabel
                  fontWeight="normal"
                  cursor={'pointer'}
                  mr="0"
                  ml="10px"
                  mb="0"
                  htmlFor={formField.name}
                >
                  <Text as="span" fontWeight={formField.name === 'wheelchair' ? 'bold' : 'normal'}>
                    {formField?.label}{' '}
                  </Text>
                  {formField.optional && <Text as="span">{`(${formField.optionalText})`}</Text>}
                  {formField.isPrivacStatement && (
                    <Link
                      target="_blank"
                      href={`https://www.premierinn.com/gb/en/terms/privacy-policy.html`} //phase 1 privacy policy link
                      textDecor="underline"
                      fontWeight="400"
                    >
                      {formField.privactStatementLinkText}
                    </Link>
                  )}
                </FormLabel>
                <FormError errors={errors} name={formField.name} />
              </Flex>
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
