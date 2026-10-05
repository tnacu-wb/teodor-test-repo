import { Box, Flex, Image, Spinner, Text } from '@chakra-ui/react';
import {
  Area,
  GET_PAYMENT_METHODS_QUERY,
  PaymentMethod,
  PaymentMethods,
  PaymentOption,
} from '@whitbread-eos/api';
import {
  isApplePayConfigured,
  isGooglePayConfigured,
  useCustomLocale,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { AnimatePresence, motion } from 'framer-motion';
import React, {
  Dispatch,
  KeyboardEvent,
  SetStateAction,
  useEffect,
  useMemo,
  useState,
} from 'react';

import {
  defaultTileStyle,
  disabledTileStyle,
  disabledWrapperStyle,
  gridStyle,
  iconContainerStyle,
  labelStyle,
  selectedTileStyle,
  spacerTileStyle,
  tileWrapperStyle,
  titleStyle,
} from './PaymentMethodSelector.styles';

export interface PaymentMethodOption {
  id: string;
  label: string;
  icon: React.ReactNode;
  disabled?: boolean;
}

interface SpacerOption extends PaymentMethodOption {
  isSpacer: true;
}

interface PaymentMethodSelectorProps {
  basketReference: string;
  selectedId: string | null;
  onChange: (id: string) => void;
  onMethodSelect?: (method: PaymentMethod) => void;
  setSelectedPaymentDetail?: Dispatch<SetStateAction<PaymentOption>>;
  'data-testid'?: string;
}

const METHOD_LABELS: Record<string, string> = {
  CARD: 'Credit / Debit',
  PIBA: 'Business Pay',
  PAYPAL: 'PayPal',
  APPLE: 'Apple Pay',
  GOOGLE: 'Google Pay',
};

const resolveImageSrc = (method: PaymentMethod): string => {
  // Wallet methods (APGP type) use subType as the filename key: AP.svg / GP.svg
  const key = method.type;
  return `/images/datatrans/${key}.svg`;
};

const buildOptions = (
  methods: PaymentMethod[] | null | undefined,
  walletAvailability: { APPLE: boolean; GOOGLE: boolean }
): PaymentMethodOption[] => {
  if (!methods) return [];
  return (
    methods
      .filter((m): m is PaymentMethod => m != null)
      // Filter wallet methods by client-side browser/device capability detected via
      // the same APIs Datatrans Payment Button uses internally.
      // APPLE: requires ApplePaySession (Safari/WebKit + card in Wallet).
      // GOOGLE: requires PaymentRequest API + Google Pay support.
      .filter((m) => {
        if (m.name === 'APPLE') return walletAvailability.APPLE;
        if (m.name === 'GOOGLE') return walletAvailability.GOOGLE;
        return true;
      })
      .slice()
      .sort((a, b) => a.order - b.order)
      .map((method) => {
        const imageSrc = resolveImageSrc(method);
        const label = METHOD_LABELS[method.name] ?? method.name;
        const icon = (
          <Image
            src={imageSrc}
            alt={label}
            w="40px"
            h="24px"
            objectFit="contain"
            borderRadius="2px"
          />
        );

        return {
          id: method.type,
          label,
          icon,
          disabled: !method.enabled,
        };
      })
  );
};

// Pad options with spacer entries to fill the last row of 3
const padToRow = (options: PaymentMethodOption[]): (PaymentMethodOption | SpacerOption)[] => {
  const remainder = options.length % 3;
  if (remainder === 0) return options;
  const spacers: SpacerOption[] = Array.from({ length: 3 - remainder }, (_, i) => ({
    id: `__spacer_${i}`,
    label: '',
    icon: null,
    disabled: true,
    isSpacer: true,
  }));
  return [...options, ...spacers];
};

export const PaymentMethodSelector = ({
  basketReference,
  selectedId,
  onChange,
  onMethodSelect,
  setSelectedPaymentDetail,
  'data-testid': testId = 'PaymentMethodSelector',
}: PaymentMethodSelectorProps) => {
  const { language, country } = useCustomLocale();

  // Wallet availability — detected client-side using shared utilities from @whitbread-eos/utils.
  // isApplePayConfigured: checks ApplePaySession (Safari/WebKit + card in Wallet).
  // isGooglePayConfigured: checks PaymentRequest API with Google Pay method identifier.
  // Both default to false (hidden) until the check completes.
  const [walletAvailability, setWalletAvailability] = useState({ APPLE: false, GOOGLE: false });

  useEffect(() => {
    let cancelled = false;

    async function checkWalletAvailability() {
      // Apple Pay — delegate to the shared utility already used across the codebase
      const appleAvailable = isApplePayConfigured();

      // Google Pay — delegate to the shared utility (mirrors isApplePayConfigured pattern)
      const googleAvailable = await isGooglePayConfigured();

      if (!cancelled) {
        setWalletAvailability({ APPLE: appleAvailable, GOOGLE: googleAvailable });
      }
    }

    checkWalletAvailability();
    return () => {
      cancelled = true;
    };
  }, []);

  const { data, isLoading } = useQueryRequest(
    ['getPaymentMethods', language, country, basketReference],
    GET_PAYMENT_METHODS_QUERY,
    { basketReference, language, country, clientChannel: Area.PI.toUpperCase() },
    { gcTime: 0 }
  ) as { data: PaymentMethods | undefined; isLoading: boolean };

  const methods = useMemo<PaymentMethod[]>(
    () => (data?.paymentMethods ?? []).filter((m): m is PaymentMethod => m != null),
    [data]
  );

  // Auto-select the first enabled method and its first enabled option on load,
  // and re-run if the methods list changes and the current selection is no longer valid.
  useEffect(() => {
    if (!methods.length || !onMethodSelect) return;

    const currentlyValid = methods.some((m) => m.type === selectedId && m.enabled);
    if (currentlyValid) return;

    const firstEnabled = methods.find((m) => m.enabled);
    if (!firstEnabled) return;

    onChange(firstEnabled.type);
    onMethodSelect(firstEnabled);

    if (setSelectedPaymentDetail) {
      const firstEnabledOption = (firstEnabled.paymentOptions ?? []).find((o) => o?.enabled);
      if (firstEnabledOption) setSelectedPaymentDetail(firstEnabledOption);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    // Intentionally omits onChange, onMethodSelect, setSelectedPaymentDetail, selectedId:
    // we only want to auto-select when the methods list itself changes, not on every render
    // cycle caused by unstable parent callbacks or selectedId updates triggered by this very effect.
  }, [methods]);

  const options = useMemo<PaymentMethodOption[]>(
    () => buildOptions(methods, walletAvailability),
    [methods, walletAvailability]
  );
  const paddedOptions = useMemo(() => padToRow(options), [options]);

  const handleSelect = (id: string) => {
    onChange(id);
    if (onMethodSelect) {
      const method = methods.find((m) => m.type === id);
      if (method) onMethodSelect(method);
    }
  };

  const handleKeyDown = (event: KeyboardEvent<HTMLDivElement>, id: string, disabled?: boolean) => {
    if (disabled) return;
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      handleSelect(id);
    }
  };

  if (isLoading) {
    return (
      <Flex justify="center" align="center" p="md" data-testid={`${testId}-Loading`}>
        <Spinner />
      </Flex>
    );
  }

  return (
    <Box role="radiogroup" data-testid={testId}>
      {/* Section title — Figma: "Payment details", Proxima Nova Sans Semibold 26px, 120% line-height */}
      <Flex align="center" justify="space-between" mb="md" data-testid={`${testId}-Header`}>
        <Text {...titleStyle} data-testid={`${testId}-Title`}>
          Payment details
        </Text>
        <Image
          src="/images/datatrans/secure.svg"
          alt="Secure payment"
          w="88px"
          h="25px"
          objectFit="contain"
          data-testid={`${testId}-SecureBadge`}
        />
      </Flex>
      {/* Row-wrap layout matching Figma: row gap 27px, col gap 16px */}
      <Flex {...gridStyle} data-testid={`${testId}-Grid`}>
        {paddedOptions.map((option) => {
          const isSpacer = 'isSpacer' in option && (option as SpacerOption).isSpacer === true;
          const isSelected = option.id === selectedId;
          const isDisabled = !!option.disabled;

          if (isSpacer) {
            // Invisible spacer to preserve row alignment
            return (
              <Box
                key={option.id}
                aria-hidden
                {...spacerTileStyle}
                data-testid={`${testId}-Spacer`}
              />
            );
          }

          return (
            // Outer wrapper: column container, no border — label sits BELOW the box
            <Box
              key={option.id}
              role="radio"
              aria-checked={isSelected}
              aria-disabled={isDisabled}
              aria-label={option.label}
              tabIndex={isDisabled ? -1 : 0}
              onClick={() => !isDisabled && handleSelect(option.id)}
              onKeyDown={(e) => handleKeyDown(e, option.id, isDisabled)}
              data-testid={`${testId}-Tile-${option.id}`}
              {...(isDisabled ? disabledWrapperStyle : tileWrapperStyle)}
            >
              {/* The bordered box itself — 56px fixed height */}
              <Box
                {...(isDisabled
                  ? disabledTileStyle
                  : isSelected
                    ? selectedTileStyle
                    : defaultTileStyle)}
                data-testid={`${testId}-Box-${option.id}`}
              >
                {/* Purple triangle badge — scales in when selected, scales out when deselected */}
                <AnimatePresence initial={false}>
                  {isSelected && (
                    <motion.svg
                      key="badge"
                      width={28}
                      height={32}
                      viewBox="0 0 28 32"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                      style={{
                        position: 'absolute',
                        top: 0,
                        right: 0,
                        display: 'block',
                        flexShrink: 0,
                      }}
                      aria-hidden
                      initial={{ scale: 0, opacity: 0, transformOrigin: 'top right' }}
                      animate={{ scale: 1, opacity: 1 }}
                      exit={{ scale: 0, opacity: 0 }}
                      transition={{ duration: 0.18, ease: 'easeOut' }}
                      data-testid={`${testId}-Checkmark-${option.id}`}
                    >
                      <polygon points="0,0 28,0 28,32" fill="#642587" />
                      <polyline
                        points="17,10.5 19,13 23.5,8"
                        stroke="white"
                        strokeWidth={1.5}
                        fill="none"
                      />
                    </motion.svg>
                  )}
                </AnimatePresence>
                <Flex {...iconContainerStyle}>{option.icon}</Flex>
              </Box>
              {/* Label OUTSIDE/BELOW the box, 4px gap from box */}
              <Text {...labelStyle} data-testid={`${testId}-Label-${option.id}`}>
                {option.label}
              </Text>
            </Box>
          );
        })}
      </Flex>
    </Box>
  );
};
