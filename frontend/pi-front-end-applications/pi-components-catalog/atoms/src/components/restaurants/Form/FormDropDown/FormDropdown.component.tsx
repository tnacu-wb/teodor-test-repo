import { Flex } from '@chakra-ui/react';
import { Controller } from 'react-hook-form';

import Dropdown from '../../Dropdown';
import FormError from '../FormError';
import { FormFieldProps } from '../formTypes';

const FormDropdown = ({
  getValues,
  setValue,
  setIsEnquiry,
  isEnquiry,
  control,
  formField,
  errors,
}: FormFieldProps) => {
  return (
    <Flex
      width="100%"
      direction="column"
      {...formField.styles}
      data-testid={formField.testid}
      data-fieldname={formField.name}
    >
      <Controller
        name={formField.name}
        control={control}
        render={({ field }) => {
          const { onChange, ...restOfField } = field;
          return (
            <Dropdown
              variant="beefeater"
              {...restOfField}
              {...formField}
              {...formField.props}
              onChange={(o) => {
                onChange(o?.id);
                if (
                  (formField.name === 'adults' || formField.name === 'children') &&
                  setIsEnquiry &&
                  Number(getValues?.('adults') ? getValues?.('adults') : 0) +
                    Number(getValues?.('children') ? getValues?.('children') : 0) >
                    16
                ) {
                  setIsEnquiry(true);
                } else if (isEnquiry && setIsEnquiry && setValue) {
                  setIsEnquiry(false);
                  setValue('adultsByEnquiry', '0');
                  setValue('childrenByEnquiry', '0');
                }
              }}
              options={formField.dropdownOptions?.map((option) => ({
                ...option,
                label: typeof option.label === 'string' ? option.label : String(option.label),
              }))}
              label={formField.label}
              optional={formField.optional}
              matchWidth
              hasError={Boolean(errors?.[formField.name]?.message)}
              selectedId={field.value}
              dropdownStyles={{
                menuListStyles: {
                  zIndex: 999,
                  maxHeight: '250px',
                },
                menuButtonStyles: {
                  h: 'var(--chakra-space-3xl)',
                  bg: '#F4F4F4',
                },
              }}
            />
          );
        }}
      />
      <FormError errors={errors} name={formField.name} />
    </Flex>
  );
};

export default FormDropdown;
