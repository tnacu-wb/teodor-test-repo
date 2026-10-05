import { Box, Flex, Text } from '@chakra-ui/react';
import { AnimatePresence, motion } from 'framer-motion';
import { useId } from 'react';

import {
  borderUnchecked,
  brandPurple,
  checkboxControlStyle,
  checkboxInnerStyle,
  hiddenInputStyle,
  labelStyle,
} from './DatatransBillingAddress.styles';

export interface DatatransBillingAddressProps {
  /** Formatted address string appended after the label prefix */
  address: string;
  /** Visible label prefix shown before the address (should come from i18n) */
  label: string;
  isChecked: boolean;
  onChange: (checked: boolean) => void;
  'data-testid'?: string;
}

const MotionBox = motion(Box);

export function DatatransBillingAddress({
  address,
  label,
  isChecked,
  onChange,
  'data-testid': testId = 'DatatransBillingAddress',
}: DatatransBillingAddressProps) {
  const id = useId();

  return (
    <Flex
      as="label"
      htmlFor={id}
      align="flex-start"
      gap="sm"
      cursor="pointer"
      my="md"
      flex="1"
      data-testid={testId}
    >
      {/* Real native checkbox — keyboard and screen reader accessible */}
      <input
        style={hiddenInputStyle}
        type="checkbox"
        id={id}
        checked={isChecked}
        onChange={(e: React.ChangeEvent<HTMLInputElement>) => onChange(e.target.checked)}
        data-testid={`${testId}-Input`}
      />

      {/* Custom visual control with animated border colour */}
      <MotionBox
        {...checkboxControlStyle}
        aria-hidden
        flexShrink={0}
        animate={{
          borderColor: isChecked ? brandPurple : borderUnchecked,
        }}
        transition={{ duration: 0.15, ease: 'easeInOut' }}
      >
        {/* Inner square scales in/out */}
        <AnimatePresence initial={false}>
          {isChecked && (
            <MotionBox
              {...checkboxInnerStyle}
              key="inner"
              initial={{ scale: 0, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0, opacity: 0 }}
              transition={{ duration: 0.15, ease: 'easeInOut' }}
            />
          )}
        </AnimatePresence>
      </MotionBox>

      <Text {...labelStyle} data-testid={`${testId}-Label`}>
        {`${label}: ${address}`}
      </Text>
    </Flex>
  );
}
