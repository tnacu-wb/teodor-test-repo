import type { BoxProps, DividerProps, FlexProps, TextProps } from '@chakra-ui/react';
import { Box, Center, Divider, Flex, Text, useMediaQuery, VStack } from '@chakra-ui/react';
import { FT_PI_BOOKING_STEPS_WITH_TITLE } from '@whitbread-eos/api';
import { Icon, Tick, Tick24 } from '@whitbread-eos/atoms';
import { useFeatureToggle } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

interface Step {
  title?: string;
  id: number;
}

export interface Props {
  steps: Array<{ id: number; title?: string }>;
  activeStep: number;
}

export default function BookingFlowSteps({ steps, activeStep }: Readonly<Props>) {
  const [isMobileView] = useMediaQuery('(max-width: 767px)');
  const { t } = useTranslation();
  const { [FT_PI_BOOKING_STEPS_WITH_TITLE]: isBookingflowStepTtilesEnabled = false } =
    useFeatureToggle();

  return (
    <Flex justifyContent="flex-end" sx={{ '@media print': { display: 'none' } }}>
      <VStack alignItems="center" gap="xs">
        <Flex {...progressContainerStyle} data-testid="progress-indicator-wrapper">
          {steps.map((step: Step, index) => (
            <Flex key={step.id} {...stepWrapperStyles} data-testid="step-container">
              {step.id > 1 && (
                <Divider
                  {...dividerStyles(
                    step.title,
                    isBookingflowStepTtilesEnabled,
                    step.id <= activeStep
                  )}
                  data-testid={`divider-${index}`}
                />
              )}
              <Flex {...containerStepStyle}>
                <Box
                  key={step.id}
                  {...assignBoxStyles(step.id, activeStep, isBookingflowStepTtilesEnabled)}
                >
                  <Center
                    data-testid={`disc-${index}`}
                    {...stepIconDiscStyle(isBookingflowStepTtilesEnabled)}
                  >
                    {step.id < activeStep ? (
                      <Icon
                        svg={
                          <>
                            <Box display={{ mobile: 'none', sm: 'block' }}>
                              <Tick24
                                color="var(--chakra-colors-baseWhite)"
                                data-testid="progress-indicator-icon"
                              />
                            </Box>
                            <Box display={{ mobile: 'block', sm: 'none' }}>
                              <Tick
                                color="var(--chakra-colors-baseWhite)"
                                data-testid="progress-indicator-icon"
                              />
                            </Box>
                          </>
                        }
                      />
                    ) : (
                      <Text {...assignStepStyles(step.id, activeStep)}>{step.id}</Text>
                    )}
                  </Center>
                </Box>
                {step.title && isBookingflowStepTtilesEnabled && (
                  <Text
                    data-testid="progress-indicator-title"
                    {...assignTitleStyles(step.id, activeStep)}
                  >
                    {step.title}
                  </Text>
                )}
              </Flex>
            </Flex>
          ))}
        </Flex>
        {/* Display active step and its title - mobile only */}
        {isMobileView &&
          isBookingflowStepTtilesEnabled &&
          renderActiveStepSummary(steps, activeStep, t)}
      </VStack>
    </Flex>
  );
}

function renderActiveStepSummary(steps: Step[], activeStep: number, t: (key: string) => string) {
  const active = steps.find((s) => s.id === activeStep);

  if (!active) {
    return null;
  }

  return (
    <Flex alignItems="center" data-testid="active-step-summary">
      <Text fontSize="sm" lineHeight="1" color="darkGrey1">
        {t('bookingflow.step.now.label')}
      </Text>
      {active.title && (
        <Text pl="2px" fontSize="sm" lineHeight="1" color="darkGrey1" fontWeight="semibold">
          {active.title}
        </Text>
      )}
    </Flex>
  );
}

function getStepSizeStyles(isBookingflowStepTtilesEnabled: boolean) {
  return isBookingflowStepTtilesEnabled
    ? {
        h: { mobile: 'var(--chakra-space-lg)', sm: 'var(--chakra-space-lg)' },
        w: { mobile: 'var(--chakra-space-lg)', sm: 'var(--chakra-space-lg)' },
      }
    : {
        h: { mobile: 'var(--chakra-space-lg)', sm: 'var(--chakra-space-xl)' },
        w: { mobile: 'var(--chakra-space-lg)', sm: 'var(--chakra-space-xl)' },
      };
}

function assignBoxStyles(
  currentIndex: number,
  activeStep: number,
  isBookingflowStepTtilesEnabled: boolean
) {
  const baseStyles: BoxProps = {
    borderRadius: 'full',
    bgColor:
      (activeStep === currentIndex && 'darkGrey1') ||
      (activeStep > currentIndex && 'primary') ||
      'lightGrey4',
  };

  return {
    ...baseStyles,
    ...getStepSizeStyles(isBookingflowStepTtilesEnabled),
  };
}

const stepIconDiscStyle = getStepSizeStyles;

function assignStepStyles(currentIndex: number, activeStep: number) {
  return {
    color: activeStep === currentIndex ? 'baseWhite' : 'darkGrey2',
    fontSize: { mobile: 'sm', sm: 'md' },
    fontWeight: 'medium',
  } as TextProps;
}

function assignTitleStyles(steId: number, activeStep: number) {
  return {
    display: { mobile: 'none', md: 'block' },
    pt: 'var(--chakra-space-sm)',
    fontSize: 'sm',
    lineHeight: '1',
    whiteSpace: 'nowrap',
    fontWeight: steId <= activeStep ? 'semibold' : 'normal',
    color: steId <= activeStep ? 'darkGrey1' : 'darkGrey2',
  } as TextProps;
}

const dividerStyles = (
  title: string | undefined,
  isBookingflowStepTtilesEnabled: boolean,
  isTicked: boolean
) => {
  return {
    orientation: 'horizontal',
    bgColor: isTicked ? 'var(--chakra-colors-primary)' : 'lightGrey4', // green if ticked, grey otherwise
    h: isTicked ? '2px' : '1px',
    mx: { mobile: 'var(--chakra-space-sm)', lg: 'var(--chakra-space-md)' },
    w: {
      mobile: '6px',
      sm: 'var(--chakra-space-3xl)',
    },
    mb: { md: title && isBookingflowStepTtilesEnabled ? 'lg' : '' },
  } as DividerProps;
};

const progressContainerStyle = {
  justifyContent: 'flex-end',
  alignItems: 'center',
  flexDir: 'row',
} as FlexProps;

const containerStepStyle = {
  flexDir: 'column',
  justifyContent: 'center',
  alignItems: 'center',
} as FlexProps;

const stepWrapperStyles = {
  flexDirection: 'row',
  justifyContent: 'flex-end',
  alignItems: 'center',
} as FlexProps;
