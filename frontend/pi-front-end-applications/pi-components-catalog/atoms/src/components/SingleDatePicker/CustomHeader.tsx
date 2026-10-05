import { Button, ButtonProps, Select, Flex } from '@chakra-ui/react';
import { getMonth, getYear } from 'date-fns';
import { useTranslation } from 'next-i18next';

import { years } from './common';

interface Props {
  date: Date;
  changeYear: (year: number) => void;
  changeMonth: (month: number) => void;
  decreaseMonth: () => void;
  increaseMonth: () => void;
  prevMonthButtonDisabled: boolean;
  nextMonthButtonDisabled: boolean;
}

interface HeaderSelectProps {
  value: string | number;
  onChange: (value: string) => void;
  options: string[] | number[];
}

function CustomHeader({
  date,
  changeYear,
  changeMonth,
  decreaseMonth,
  increaseMonth,
  prevMonthButtonDisabled,
  nextMonthButtonDisabled,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const months = t('precheckin.calendar.months').split('|');

  return (
    <Flex alignItems="center" justifyContent="center" gap="sm">
      <Button
        {...navBtn}
        onClick={decreaseMonth}
        disabled={prevMonthButtonDisabled}
        className="navigation-btn-left"
      >
        <span className="react-datepicker__navigation-icon--previous react-datepicker__navigation-icon" />
      </Button>

      <HeaderSelect
        value={months[getMonth(date)]}
        onChange={(value: string) => changeMonth(months.indexOf(value))}
        options={months}
      />
      <HeaderSelect
        value={getYear(date)}
        onChange={(value: string) => changeYear(+value)}
        options={years}
      />

      <Button
        {...navBtn}
        onClick={increaseMonth}
        disabled={nextMonthButtonDisabled}
        className="navigation-btn-right"
      >
        <span className="react-datepicker__navigation-icon--next react-datepicker__navigation-icon" />
      </Button>
    </Flex>
  );
}
export default CustomHeader;

const HeaderSelect = ({ value, onChange, options }: HeaderSelectProps) => (
  <Select
    value={value}
    onChange={({ target: { value } }) => onChange(value)}
    size="sm"
    width="120px"
  >
    {options.map((option) => (
      <option key={option} value={option}>
        {option}
      </option>
    ))}
  </Select>
);

const navBtn = {
  type: 'button',
  variant: 'unstyled',
  size: 'xss',
  style: {
    outline: 'none',
    boxShadow: 'none',
  },
} as ButtonProps;
