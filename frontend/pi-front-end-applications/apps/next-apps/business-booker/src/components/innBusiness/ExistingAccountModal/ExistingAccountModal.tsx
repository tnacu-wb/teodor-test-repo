'use client';

import { CountryCode, FT_IB_PAY_PIBA_EURO, LOCALES } from '@whitbread-eos/api';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  LinkButton,
  Button,
} from '@whitbread-eos/atoms/ui';
import {
  useFeatureToggle,
  getPathForLocale,
  useTranslation,
  getCountryLanguageByLocale,
} from '@whitbread-eos/utils';

declare global {
  interface Window {
    _satellite: any;
  }
}

interface ExistingAccountModalProps {
  isModalOpen: boolean;
  locale: LOCALES;
  onClose: () => void;
}

const ExistingAccountModal = ({ isModalOpen, locale, onClose }: ExistingAccountModalProps) => {
  const { t } = useTranslation(['notifications']);

  const { language } = getCountryLanguageByLocale(locale);

  const isDeLanguage = language === CountryCode.DE;

  const { [FT_IB_PAY_PIBA_EURO]: isPibaEuroEnabled } = useFeatureToggle();

  const showStartNewApplication = !isDeLanguage || isPibaEuroEnabled;

  const trackLinkAccount = () => {
    window?._satellite?.track('linkAccount');
  };

  const handleStartNewApplication = () => {
    onClose();

    const targetPath = getPathForLocale(locale, 'business-pay/apply');
    window.location.href = targetPath;
  };

  return (
    <Dialog open={isModalOpen} data-testid="ExistingAccount-Dialog" onOpenChange={onClose}>
      <DialogContent
        className={dialogContentStyle}
        data-testid="ExistingAccount-Dialog-Content"
        description={t('notifications.notification.account.exists.title')}
      >
        <DialogHeader data-testid="ExistingAccount-Dialog-Header">
          <DialogTitle data-testid="ExistingAccount-Dialog-Title">
            {t('notifications.notification.account.exists.title')}
          </DialogTitle>
        </DialogHeader>
        <span data-testid="ExistingAccount-Box">
          {t('notifications.notification.account.exists.description')}
        </span>
        <DialogFooter data-testid="ExistingAccount-Dialog-Footer">
          <LinkButton
            data-testid="ExistingAccount-Link-Button"
            variant="default"
            onClick={trackLinkAccount}
            href={`${process?.env?.NEXT_PUBLIC_WORLDLINE_HOST ?? ''}/BBLinkCode.aspx`}
            className="flex-1"
          >
            {t('notifications.notification.account.exists.link.linkAccount')}
          </LinkButton>
          {showStartNewApplication && (
            <Button
              data-testid="ExistingAccount-Submit-Button"
              variant="dialogDefault"
              onClick={handleStartNewApplication}
            >
              {t('notifications.notification.account.exists.link.newApp')}
            </Button>
          )}
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default ExistingAccountModal;

const dialogContentStyle = 'max-w-[700px]';
