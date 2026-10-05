import { GridProps, StyleProps } from '@chakra-ui/react';

export const gridStyles = {
  w: { base: '100%', md: '50%' },
  flex: '0 0 auto',
  alignItems: 'center',
} as GridProps;

export const inputStyle = {
  height: 'var(--chakra-space-4xl)',
  _placeholder: { opacity: 0.7, color: 'var(--chakra-darkGrey1)' },
  borderColor: 'lightGrey1',
  borderRadius: 'var(--chakra-radii-md)',
} as StyleProps;

export const inputIconStyles = {
  top: 'var(--chakra-space-sm)',
};
export const dataErrorText = {
  alignItems: 'center',
  color: 'var(--chakra-colors-error)',
  fontSize: 'var(--chakra-fontSizes-xs)',
  marginLeft: '15px',
};

export const todayLabel = 'Today';
export const tomorrowLabel = 'Tomorrow';

export const additionalDetailsFields = [
  {
    type: 'input',
    name: 'email',
    label: 'email',
    testId: 'Email',
    styles: { w: '100%' },
  },
  {
    type: 'datePicker',
    name: 'dateOfBirth',
    label: 'dateofbirth',
    testId: 'DateOfBirth',
    styles: { w: '100%' },
  },
  {
    type: 'autoComplete',
    name: 'nationality',
    label: 'nationality',
    testId: 'Nationality',
    styles: { w: '100%' },
  },
  {
    type: 'input',
    name: 'passport',
    label: 'passport',
    testId: 'Passport',
    styles: { w: '100%' },
  },
];

export const additionalDetailsFieldsBooker = additionalDetailsFields.filter(
  ({ name }) => name !== 'email'
);

export const headingStyles = {
  fontFamily: 'header',
  fontSize: 'xl',
  fontWeight: 'semibold',
  lineHeight: '3',
  color: 'darkGrey1',
};

export const modalButtonsStyles = {
  w: {
    base: 'full',
    xs: '21.43rem',
    sm: '16.18rem',
    md: '15.93rem',
    lg: '16.5rem',
    xl: '17.87rem',
  },
  h: '3.5rem',
  borderRadius: '.25rem',
  p: 'md',
};

export interface Nationality {
  value: string;
  label: string;
  image: string;
}

export const fieldProps = ({ name, onBlur, value, onChange }: any) => ({
  name,
  onBlur,
  value,
  onChange,
});

interface FieldProp {
  value: string;
}
interface Field {
  onChange: (value: any) => void;
}
type HandleSetValueField = (fieldToReset: string, value: string) => void;

export const handleNationalityChange = (
  fieldProp: FieldProp,
  field: Field,
  fieldToReset: string,
  handleSetValue: HandleSetValueField
): void => {
  field.onChange(fieldProp);
  handleSetValue?.(fieldToReset, '');
};
