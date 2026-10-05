import { PathParams } from '@whitbread-eos/api';
import { SanitizedContent, Button } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations,
  getPathForLocale,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils/server';
import Image from 'next/image';
import Link from 'next/link';

type Props = {
  params?: Promise<PathParams>;
};

export default async function InnBusinessAddCardFail({ params }: Props) {
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
            alt={'Fail Icon'}
            src={formatIBAssetsUrl(t('cardMgmt.addCard.failure.notifications.icon'))}
          />
          <h1 className={h1Style} data-testid={`Add-Card-Success-Title`}>
            {t('cardMgmt.addCard.failure.heading')}
          </h1>
        </div>
        <div className={contentStyle}>
          <span>
            <SanitizedContent>{t('cardMgmt.addCard.failure.text')}</SanitizedContent>
          </span>
        </div>
        <Link
          href={getPathForLocale(resolvedParams?.locale, 'manage/cards?tab=innbusiness-pay')}
          className="mobile:w-full"
        >
          <Button data-testid="Retry-Button" variant="dialogDefault" className={buttonStyle}>
            {t('cardMgmt.addCard.failure.try.again.button')}
          </Button>
        </Link>
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
