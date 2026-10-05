import { Flex } from '@chakra-ui/react';
import { Controller } from 'react-hook-form';

import { formatDataTestId } from '../../../utils/formatters';
import FormError from '../FormError';
import { FormDropdownProps } from '../formTypes';
import Dropdown from './../../Dropdown';

export default function FormDropdown({
  control,
  formField,
  errors,
  handleSetValue,
  onChangeAction,
}: Readonly<FormDropdownProps>) {
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
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          const { onChange, ref: _, ...extraPropsField } = field;
          const hasError = Boolean(errors?.[formField.name]?.message);
          return (
            <Dropdown
              {...formField.props}
              {...extraPropsField}
              onChange={(o) => {
                formField.onChange?.(o?.id);
                onChange(o?.id);
                onChangeAction?.(o?.id, handleSetValue);
              }}
              options={formField.dropdownOptions}
              label={formField.label}
              matchWidth
              hasError={hasError}
              dataTestId={formatDataTestId(formField.testid, 'InnerDropdown')}
              selectedId={field.value ? field.value : field.name}
              dropdownStyles={{
                ...formField.props?.dropdownStyles,
                menuListStyles: {
                  ...formField.props?.dropdownStyles?.menuListStyles,
                  zIndex: 999,
                },
                menuButtonStyles: {
                  ...formField.props?.dropdownStyles?.menuButtonStyles,
                  ...(hasError && {
                    border: '2px solid var(--chakra-colors-error)',
                    borderColor: 'var(--chakra-colors-error) !important',
                    _focus: {
                      border: '2px solid var(--chakra-colors-error)',
                    },
                    _hover: {
                      border: '2px solid var(--chakra-colors-error)',
                      borderColor: 'var(--chakra-colors-error)',
                    },
                  }),
                },
              }}
            />
          );
        }}
      />
      <FormError errors={errors} name={formField.name} extraStyles={formField.errorStyles} />
    </Flex>
  );
}

const defaultStyles = {
  marginBottom: 'var(--chakra-space-md)',
};
