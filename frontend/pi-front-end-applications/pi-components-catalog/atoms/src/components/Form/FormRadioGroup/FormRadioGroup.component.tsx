import { Flex, RadioGroup, Stack, Text } from '@chakra-ui/react';
import { useSemanticTypography } from '@whitbread-eos/utils';
import { Controller } from 'react-hook-form';

import RadioButton, { PaymentRadioButton } from '../../Radio';
import FormError from '../FormError';
import { FORM_VALIDATIONS } from '../formConstants';
import { FormRadioGroupProps } from '../formTypes';

export default function FormRadioGroup({
  control,
  formField,
  errors,
  handleSetValue,
  onChangeAction,
}: Readonly<FormRadioGroupProps>) {
  const getTypographyProps = useSemanticTypography();
  const horizontalRadioButtons = formField?.props?.horizontalRadioButtons;

  const WrapperComponent = horizontalRadioButtons ? PaymentRadioButton : RadioButton;

  return (
    <Flex
      direction="column"
      {...{ ...defaultStyles, ...formField.styles }}
      data-testid={formField.testid}
      data-fieldname={formField.name}
    >
      {formField?.props?.errorLocation === FORM_VALIDATIONS.FORM_FIELD_ERROR_LOCATION.ON_TOP && (
        <FormError
          errors={errors}
          name={formField.name}
          extraStyles={{
            containerStyle: { marginBottom: 'md' },
            textStyle: { marginTop: 0, marginLeft: 0 },
          }}
        />
      )}
      <Controller
        name={formField.name}
        control={control}
        render={({ field }) => {
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          const { onChange, ref: _, ...extraPropsField } = field;
          return (
            <RadioGroup
              onChange={(value) => {
                formField.onChange?.(value);
                onChange(value);
                onChangeAction?.(value, handleSetValue);
              }}
              {...extraPropsField}
            >
              <Stack
                {...(formField?.props?.horizontalRadioButtons && {
                  ...horizontalRadioContainerStyles,
                })}
                spacing="0"
                direction="column"
                style={{
                  borderBottomLeftRadius: 0,
                  borderBottomRightRadius: 0,
                }}
              >
                {formField.options?.map((option, listIndex) => {
                  const isLastInList = listIndex === Number(formField.options?.length) - 1;
                  const isChecked = field.value === option.value;
                  const optionLabelLegacyTypography = {
                    fontWeight: isChecked || horizontalRadioButtons ? 'semibold' : 'normal',
                  };
                  const optionLabelSemanticTypography = {
                    textStyle:
                      isChecked || horizontalRadioButtons ? 'body-m-emphasis' : 'body-m-regular',
                  };

                  return (
                    <WrapperComponent
                      key={`${formField.name}-${listIndex}`}
                      name={option.value}
                      listIndex={isLastInList ? 'last' : 0}
                      value={option.value}
                      isChecked={isChecked}
                      data-testid={option.testid}
                    >
                      {option.Component ? (
                        <option.Component isChecked={isChecked} />
                      ) : (
                        <Text
                          {...getTypographyProps(
                            optionLabelLegacyTypography,
                            optionLabelSemanticTypography
                          )}
                        >
                          {option.label}
                        </Text>
                      )}
                    </WrapperComponent>
                  );
                })}
              </Stack>
            </RadioGroup>
          );
        }}
      />
      {!formField?.props?.errorLocation.length && (
        <FormError errors={errors} name={formField.name} extraStyles={formField.errorStyles} />
      )}
    </Flex>
  );
}

const defaultStyles = {
  marginBottom: 'var(--chakra-space-md)',
};

const horizontalRadioContainerStyles = {
  flexDirection: {
    mobile: 'column',
    lg: 'row',
  },
};
