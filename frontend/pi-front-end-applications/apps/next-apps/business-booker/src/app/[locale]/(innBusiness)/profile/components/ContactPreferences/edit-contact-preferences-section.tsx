'use client';

import { Button, ButtonVariantDescriptor, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';

type Props = {
  locale: string;
};

export function EditContactPreferencesSection({ locale }: Props) {
  const { t } = useTranslation('profile');
  const baseDataTestId = 'EditContactPreferencesSection';

  return (
    <div data-testid={baseDataTestId} className={containerStyle}>
      <h4 data-testid={`${baseDataTestId}-Title`} className={titleStyle}>
        {t('marketingpreferences.title')}
      </h4>
      <SanitizedContent data-testid={`${baseDataTestId}-Description`} className={descriptionStyle}>
        {t('marketingpreferences.description')}
      </SanitizedContent>
      <a
        href={`/${locale}/profile/contact-preferences`}
        className={linkStyle}
        data-testid={`${baseDataTestId}-EditLink`}
      >
        <Button
          variant="default"
          size="default"
          className={buttonStyle}
          data-testid={`${baseDataTestId}-EditButton`}
        >
          {t('marketingpreferences.button.edit')}
        </Button>
      </a>
    </div>
  );
}

const containerStyle = 'w-[531px] flex flex-col gap-2 mobile:w-full';
const titleStyle = 'font-semibold text-[1.438rem]';
const descriptionStyle = 'text-darkGrey1 text-base font-normal leading-6 text-left';
const linkStyle = 'w-[309px] mt-2 mobile:w-full';
const buttonStyle = `mobile:min-w-full min-w-[19.313rem] h-[3.5rem] ${ButtonVariantDescriptor.truncateWithEllipsisButton}`;
