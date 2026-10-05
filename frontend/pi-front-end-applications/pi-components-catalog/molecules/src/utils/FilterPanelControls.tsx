import { Text, TextProps } from '@chakra-ui/react';
import { Dismiss, Icon } from '@whitbread-eos/atoms';

import { onActivationKeyDown } from './filterPanelKeyboard';

interface FilterPanelCloseButtonProps {
  testId: string;
  label: string;
  onClose: () => void;
}

export function FilterPanelCloseButton({
  testId,
  label,
  onClose,
}: Readonly<FilterPanelCloseButtonProps>) {
  return (
    <Icon
      svg={<Dismiss data-testid={testId} />}
      onClick={onClose}
      onKeyDown={onActivationKeyDown(onClose)}
      role="button"
      tabIndex={0}
      aria-label={label}
      style={{ cursor: 'pointer' }}
    />
  );
}

interface FilterPanelClearButtonProps extends TextProps {
  testId: string;
  label?: string;
  onClear: () => void;
}

export function FilterPanelClearButton({
  testId,
  label,
  onClear,
  ...textProps
}: Readonly<FilterPanelClearButtonProps>) {
  return (
    <Text
      {...textProps}
      onClick={onClear}
      onKeyDown={onActivationKeyDown(onClear)}
      role="button"
      tabIndex={0}
      aria-label={label}
      data-testid={testId}
    >
      {label}
    </Text>
  );
}
