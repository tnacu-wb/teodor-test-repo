import { Box, Flex, Text, Heading } from '@chakra-ui/react';

import FormInput from '../FormInput';
import { FormFieldProps, FieldsType } from '../formTypes';

export default function EnquiryForm({
  formField,
  control,
  errors,
  enquiryHeading,
  enquirySubheading,
}: FormFieldProps) {
  return (
    <Flex mb="2xl" p="md" flexDirection={'column'} background={'#D9D9D9'} data-testid="EnquiryForm">
      <Heading as="h1" fontSize="md" fontWeight="bold" lineHeight="3" pb={2}>
        {enquiryHeading}
      </Heading>
      <Text mb="12px">{enquirySubheading}</Text>
      {formField?.relatedFields?.map((field: FieldsType) => {
        return (
          <Box width="100%" key={field.label}>
            <FormInput control={control} formField={field} errors={errors} />
          </Box>
        );
      })}
    </Flex>
  );
}
