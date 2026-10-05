import { HStack, Link, Text } from '@chakra-ui/react';
import { usePromoTranslation } from '@whitbread-eos/utils';
import { Clock, Info } from 'lucide-react';

import { ResendTextProps, TimerStatusProps } from '../List/types';
import { styles } from './styles';

export const PASSWORD_DURATION = 10 * 60;

export const formatTime = (seconds: number) => {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;

  return `${mins}:${secs.toString().padStart(2, '0')}`;
};

export const ResendText = ({ onResend }: ResendTextProps) => {
  return (
    <Text sx={styles.resendTextStyles}>
      {"Didn't receive your code?"}
      <Link sx={styles.resendStyles} as="button" onClick={onResend}>
        resend
      </Link>
    </Text>
  );
};

export const TimerStatus = ({ timeLeft }: TimerStatusProps) => {
  const { oneTimePasswordExpiredMessage, expiresTimerText } = usePromoTranslation();
  const isExpired = timeLeft <= 0;

  if (isExpired) {
    return (
      <HStack sx={styles.expiredStyles}>
        <Info size={18} />
        <Text>{oneTimePasswordExpiredMessage}</Text>
      </HStack>
    );
  }

  return (
    <HStack sx={styles.timerStyles}>
      <Text>{expiresTimerText}</Text>
      <Clock size={20} />
      <Text sx={styles.timeStyles}>{formatTime(timeLeft)}</Text>
    </HStack>
  );
};

export const downloadFileFromUrl = (url: string, fileName?: string) => {
  if (!url) return;

  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.rel = 'noopener noreferrer';
  anchor.download = fileName || url.split('/').pop() || 'download';

  document.body.appendChild(anchor);
  anchor.click();
  document.body.removeChild(anchor);
};
