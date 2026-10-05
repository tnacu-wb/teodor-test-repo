import { Radio, Box, BoxProps } from '@chakra-ui/react';
import { forwardRef } from 'react';

import { formatDataTestId } from '../../utils/formatters';

interface Props extends BoxProps {
  header: React.ReactNode;
  isChecked: boolean;
  value: string;
  onClick: (ev: React.MouseEvent<HTMLDivElement>) => void;
  children: React.ReactNode;
  isClassChecked?: boolean;
}

export const RadioCard = forwardRef<HTMLDivElement, Readonly<Props>>(
  ({ header, isChecked, value, onClick, children, isClassChecked }, ref) => {
    const baseDataTestId = 'RadioCard';
    return (
      <Box
        {...radioCardBoxStyle}
        {...(isChecked
          ? {
              borderColor: 'darkPurple',
              boxShadow: '0px 5.23px 6.28px 0px #00000005',
              cursor: 'default',
            }
          : { borderColor: 'lightGrey3', boxShadow: undefined, cursor: 'pointer' })}
        data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}
        ref={ref}
        onClick={onClick}
      >
        <Box
          {...{
            ...headerBoxStyle,
            background: isChecked ? 'darkPurple' : isClassChecked ? 'lightPurple2' : 'lightGrey5',
          }}
        >
          <Radio value={value} isChecked={isChecked} size="lg" variant="softBundle" />
          {header}
        </Box>
        <Box {...contentBoxStyle}>{children}</Box>
      </Box>
    );
  }
);

RadioCard.displayName = 'RadioCard';

const radioCardBoxStyle = {
  border: '1px solid',
  borderRadius: 4,
  position: 'sticky',
  minW: { mobile: '13.188rem', xs: '16.25rem', sm: '13.35rem', md: 'auto' },
  flex: { md: '1' },
  display: 'flex',
  flexDirection: 'column',
  overflow: 'hidden',
  cursor: 'pointer',
} as BoxProps;

const headerBoxStyle = {
  padding: 'md',
  display: 'flex',
  alignItems: 'center',
} as BoxProps;

const contentBoxStyle = {
  background: 'baseWhite',
  gap: 'md',
  borderBottomRadius: 4,
  display: 'flex',
  flexDirection: 'column',
  flex: 1,
} as BoxProps;
