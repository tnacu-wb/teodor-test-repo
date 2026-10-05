import { useToast } from '@whitbread-eos/atoms/ui';
import { analytics, useTranslation } from '@whitbread-eos/utils';

export const useWorldlineErrorHandler = () => {
  const { t } = useTranslation('payApplication');
  const { toast } = useToast();

  const handleWorldlineError = (fetchResponse: any) => {
    try {
      const debugMessage = JSON.parse(fetchResponse.errors[0].message).debugMessage;
      const errorRegex = /message=([^}]+)}/;
      const match = debugMessage?.match(errorRegex);

      if (!match) {
        throw new Error('could not parse error from Worldline');
      }

      toast({
        content: <div className={wlErrorToastStyle}>{match[1]}</div>,
        variant: 'error',
      });

      analytics.update({
        validation: match[1],
      });
    } catch (error) {
      toast({
        content: t('notification.message.error'),
        variant: 'error',
      });

      analytics.update({
        validation: t('notification.message.error'),
      });
    }
  };

  return { handleWorldlineError };
};

const wlErrorToastStyle = 'max-w-[500px] mobile:max-w-full';
