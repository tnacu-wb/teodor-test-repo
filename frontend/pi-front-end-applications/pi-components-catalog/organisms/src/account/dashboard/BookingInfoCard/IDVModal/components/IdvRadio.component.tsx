import { Box, Radio, RadioGroup, RadioGroupProps, Stack, Text, TextProps } from '@chakra-ui/react';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

export type IdvRadioOptionType = {
  text: string;
  value: boolean;
};

type IdvRadioType = {
  text: string;
  value: boolean;
  options: IdvRadioOptionType[];
  onChange: (value: boolean) => void;
  dataTestId: string;
};

const TRUE = 'TRUE';
const FALSE = 'FALSE';
const mapBooleanToString = (value: boolean) => (value ? TRUE : FALSE);

const IdvRadio = ({ text, value, options, onChange, dataTestId }: IdvRadioType) => {
  const [selectedOption, setSelectedOption] = useState(mapBooleanToString(value));

  const handleChange = (newValue: string) => {
    setSelectedOption(newValue);
    onChange(newValue === TRUE);
  };

  useEffect(() => {
    // logic for only one DPA button on “Yes” at a time
    if (mapBooleanToString(value) !== selectedOption) {
      setSelectedOption(FALSE);
    }
  }, [value, selectedOption]);

  return (
    <Box flex="1">
      <Text {...textStyle} data-testid={formatDataTestId(dataTestId, 'Label')}>
        {text}
      </Text>
      <RadioGroup
        {...radioGroupStyle}
        onChange={handleChange}
        value={selectedOption}
        data-testid={formatDataTestId(dataTestId, 'RadioGroup')}
      >
        <Stack spacing={5} direction="row">
          {options.map((option) => (
            <Radio
              data-testid={formatDataTestId(
                dataTestId,
                `RadioOption-${mapBooleanToString(option.value)}`
              )}
              key={option.text}
              value={mapBooleanToString(option.value)}
              variant="primary"
            >
              <Text {...valueStyle}>{option.text}</Text>
            </Radio>
          ))}
        </Stack>
      </RadioGroup>
    </Box>
  );
};

const textStyle = {
  color: 'darkGrey1',
  fontSize: 'md',
  fontWeight: '400',
  lineHeight: '1.5rem',
  fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
} as TextProps;

const valueStyle = {
  color: 'darkGrey2',
  fontSize: 'sm',
  fontWeight: '400',
  lineHeight: '1.25rem',
  fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
} as TextProps;

const radioGroupStyle = {
  color: 'lightGrey1',
} as RadioGroupProps;

export default IdvRadio;
