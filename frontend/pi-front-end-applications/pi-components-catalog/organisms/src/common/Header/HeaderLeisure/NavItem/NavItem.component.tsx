import { Text, TextProps } from '@chakra-ui/react';

interface Props {
  title: string;
  onClick?: () => void;
}

export default function NavItem(props: Readonly<Props>) {
  const { title, ...rest } = props;
  return (
    <Text {...rest} {...titleStyles} textAlign="center">
      {title}
    </Text>
  );
}

const titleStyles = {
  w: { lg: '11.5rem', xl: '12.375rem' },
  cursor: 'pointer',
  fontSize: 'lg',
  lineHeight: '3',
  color: 'darkGrey1',
  whiteSpace: 'nowrap',
  _focusVisible: {
    outline: 'none',
  },
  _hover: {
    color: 'primary',
  },
  mb: 0,
} as TextProps;
