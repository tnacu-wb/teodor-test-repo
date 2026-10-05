import { PathParams } from '@whitbread-eos/api';
import { SanitizedContent } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations,
  getPathForLocale,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils/server';
import Image from 'next/image';

import { BackButton } from './back-button';

type Props = {
  params?: Promise<PathParams>;
};

export default async function InnBusinessAddCardSuccess({ params }: Props) {
  const resolvedParams = await params;
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const { t } = await getTranslations(language, 'cards');

  return (
    <div className={pageStyle}>
      <div className={containerStyle}>
        <div className={titleWrapper}>
          <Image
            width={32}
            height={32}
            alt={'Success Icon'}
            src={formatIBAssetsUrl(t('cardMgmt.activateCard.confirmation.icon'))}
          />
          <h1 className={h1Style} data-testid={`Add-Card-Success-Title`}>
            {t('cardMgmt.submission.title')}
          </h1>
        </div>
        <div className={contentStyle}>
          <span>
            <SanitizedContent>{t('cardMgmt.submission.message.cardOrdered')}</SanitizedContent>
          </span>
          <span>
            <SanitizedContent>{t('cardMgmt.submission.message.contactUs')}</SanitizedContent>
          </span>
        </div>
        <BackButton
          className={buttonStyle}
          text={t('cardMgmt.submission.backButton')}
          path={getPathForLocale(resolvedParams?.locale, 'manage/cards?tab=innbusiness-pay')}
        />
      </div>
    </div>
  );
}

const titleWrapper = 'flex items-start flex-col mb-4';
const h1Style =
  'flex text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl mt-4';
const pageStyle = 'form-page bg-lightGrey5 border-b border-lightGrey3';
const containerStyle =
  'relative w-[420px] mobile:w-full mx-auto my-12 mobile:m-0 mobile:px-4 mobile:py-6';
const contentStyle = 'flex flex-col gap-4';
const buttonStyle = 'flex w-full mt-12';
