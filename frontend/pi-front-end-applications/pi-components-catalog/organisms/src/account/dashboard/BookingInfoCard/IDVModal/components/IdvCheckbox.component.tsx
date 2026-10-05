import { Box, Checkbox, CheckboxProps, Flex, Text, TextProps } from '@chakra-ui/react';
import { formatDataTestId } from '@whitbread-eos/utils';
import { ChangeEvent, useState } from 'react';

type IdvCheckboxType = {
  text: string;
  value: string;
  dataTestId: string;
  partOfSearch?: boolean;
};

const IdvCheckbox = ({ text, value, dataTestId, partOfSearch }: IdvCheckboxType) => {
  const [isChecked, setIsChecked] = useState(!!partOfSearch);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    if (!partOfSearch) {
      setIsChecked(e.target.checked);
    }
  };

  return (
    <Flex opacity={partOfSearch ? 0.3 : 1}>
      <Box flex="1" data-testid={formatDataTestId(dataTestId, 'Label')}>
        <Text {...textStyle}>{text}</Text>
        <Text {...valueStyle}>{value}</Text>
      </Box>
      <Checkbox
        {...checkboxStyle}
        isChecked={isChecked}
        onChange={handleChange}
        data-testid={formatDataTestId(dataTestId, 'Checkbox')}
      ></Checkbox>
    </Flex>
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
  fontSize: 'md',
  fontWeight: '400',
  lineHeight: '1.25rem',
  fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
} as TextProps;

const checkboxStyle = {
  color: 'lightGrey1',
  alignItems: 'start',
  size: 'lg',
} as CheckboxProps;

export default IdvCheckbox;
