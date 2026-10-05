import { Box, Flex, Text } from '@chakra-ui/react';
import { PaymentOption, paymentOptions } from '@whitbread-eos/api';
import { AnimatePresence, motion } from 'framer-motion';
import React, { ReactNode } from 'react';

import {
  descriptionStyle,
  optionLabelStyle,
  titleStyle,
  toggleBarStyle,
} from './PaymentOptionToggle.styles';

interface OptionConfig {
  type: string;
  label: string;
  description?: ReactNode;
}

export interface PaymentOptionToggleProps {
  /** Raw paymentOptions array from the selected PaymentMethod */
  options: PaymentOption[];
  selectedType: string;
  onChange: (type: string) => void;
  optionConfig?: OptionConfig[];
  title?: string;
  'data-testid'?: string;
}

const DEFAULT_OPTION_CONFIG: OptionConfig[] = [
  {
    type: paymentOptions.PAY_ON_ARRIVAL,
    label: 'Pay on arrival',
    description: (
      <>
        <strong>No payment will be taken.</strong> We need your card details to reserve your
        booking. Free cancellation up to 1pm on day of arrival
      </>
    ),
  },
  {
    type: paymentOptions.PAY_NOW,
    label: 'Pay now',
    description: (
      <>
        <strong>Pay now.</strong> Free cancellation up to 1pm on day of arrival
      </>
    ),
  },
];

export const PaymentOptionToggle = ({
  options,
  selectedType,
  onChange,
  optionConfig = DEFAULT_OPTION_CONFIG,
  title = 'When would you like to pay?',
  'data-testid': testId = 'PaymentOptionToggle',
}: PaymentOptionToggleProps) => {
  const payNow = options.find((o) => o.type === paymentOptions.PAY_NOW);
  const payOnArrival = options.find((o) => o.type === paymentOptions.PAY_ON_ARRIVAL);
  const bothEnabled = payNow?.enabled && payOnArrival?.enabled;

  if (!bothEnabled) return null;

  const visibleOptions = optionConfig.filter((cfg) =>
    options.some((o) => o.type === cfg.type && o.enabled)
  );

  const resolvedSelectedType =
    selectedType === 'default' ? paymentOptions.PAY_ON_ARRIVAL : selectedType;

  const selectedIndex = visibleOptions.findIndex((o) => o.type === resolvedSelectedType);
  const selectedOptionConfig = visibleOptions[selectedIndex];
  const pillPercent = selectedIndex * (100 / visibleOptions.length);

  return (
    <Box flex={1} my="md" data-testid={testId}>
      <Text {...titleStyle} data-testid={`${testId}-Title`}>
        {title}
      </Text>

      {/* Toggle bar — position:relative so the animated pill can sit inside it */}
      <Flex
        {...toggleBarStyle}
        position="relative"
        role="radiogroup"
        aria-label={title}
        data-testid={`${testId}-Toggle`}
      >
        {/* Animated sliding pill — moves between option slots */}
        <motion.div
          style={{
            position: 'absolute',
            top: 2,
            bottom: 2,
            borderRadius: 4,
            backgroundColor: '#642587',
            width: `calc(${100 / visibleOptions.length}% - 2px)`,
            zIndex: 0,
          }}
          animate={{ left: selectedIndex === 0 ? '2px' : `calc(${pillPercent}%)` }}
          transition={{ type: 'spring', stiffness: 400, damping: 35 }}
          aria-hidden
        />

        {/* Option labels — sit above the pill */}
        {visibleOptions.map((option) => {
          const isSelected = option.type === resolvedSelectedType;

          return (
            <Box
              key={option.type}
              {...optionLabelStyle}
              color={isSelected ? 'baseWhite' : '#642587'}
              aria-checked={isSelected}
              onClick={() => onChange(option.type)}
              data-testid={`${testId}-Option-${option.type}`}
              sx={{ transition: 'color 0.2s ease' }}
              type="button"
            >
              {option.label}
            </Box>
          );
        })}
      </Flex>

      {/* Description — fades + slides up when the selection changes */}
      <AnimatePresence mode="wait" initial={false}>
        {selectedOptionConfig?.description && (
          <motion.div
            key={resolvedSelectedType}
            initial={{ opacity: 0, y: 6 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -6 }}
            transition={{ duration: 0.2, ease: 'easeInOut' }}
          >
            <Text {...descriptionStyle} data-testid={`${testId}-Description`}>
              {selectedOptionConfig.description}
            </Text>
          </motion.div>
        )}
      </AnimatePresence>
    </Box>
  );
};
