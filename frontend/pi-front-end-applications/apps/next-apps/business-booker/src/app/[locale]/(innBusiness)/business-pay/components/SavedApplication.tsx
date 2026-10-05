'use client';

import { LOCALES, ApplicationParticipant, Language } from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation, getPathForLocale } from '@whitbread-eos/utils';
import Image from 'next/image';
import { useRouter } from 'next/navigation';

import { revalidateCacheOnLink } from '../../../(innBusiness)/manage/cards/components/revalidate-link';
import { BaseApplicationComponent } from './BaseApplicationComponent';

type Props = {
  companyName: string;
  applicationReference: number;
  startedBy: string;
  companyId: string;
  icons: Record<string, string>;
  participants: ApplicationParticipant[];
  locale?: LOCALES;
  language?: Language;
  applicationGuid: string;
  applicationId: string;
  token: string;
  showShareAppWithColleague?: boolean;
};

export function SavedApplication({
  companyName,
  applicationReference,
  startedBy,
  companyId,
  icons,
  locale,
  participants,
  applicationGuid,
  applicationId,
  token,
  language,
  showShareAppWithColleague = false,
}: Readonly<Props>) {
  const baseDataTestId = 'SavedApplication';
  const router = useRouter();
  const { t } = useTranslation('payApplication');

  const handleBackToHome = async () => {
    const homepagePath = getPathForLocale(locale || LOCALES.EN, 'homepage');

    await revalidateCacheOnLink(homepagePath);
    router.push(homepagePath);
  };

  const titleSection = (
    <div data-testid={`${baseDataTestId}-title`} className={`${titleStyle} flex flex-col gap-2`}>
      <Image
        alt={'Application saved icon'}
        src={formatIBAssetsUrl(icons['icon.checkmark-white-purple'])}
        width={26}
        height={26}
        className={buttonIconStyle}
        data-testid={`${baseDataTestId}-app-saved-icon`}
      />
      <span>{t('payapp.applicationSave.label')}</span>
    </div>
  );

  const bottomButton = (
    <Button
      data-testid={`${baseDataTestId}-back-to-home`}
      variant="default"
      size="default"
      className={buttonStyle}
      onClick={handleBackToHome}
    >
      {t('payapp.backToHome')}
    </Button>
  );

  return (
    <BaseApplicationComponent
      companyName={companyName}
      applicationReference={applicationReference}
      startedBy={startedBy}
      companyId={companyId}
      icons={icons}
      locale={locale}
      participants={participants}
      applicationGuid={applicationGuid}
      applicationId={applicationId}
      token={token}
      language={language}
      titleSection={titleSection}
      bottomButton={bottomButton}
      showResumeLink={true}
      baseDataTestId={baseDataTestId}
      showShareAppWithColleague={showShareAppWithColleague}
    />
  );
}

const titleStyle = 'text-3xl font-black text-secondaryColor mb-6';
const buttonStyle = 'flex w-full';
const buttonIconStyle = 'mr-2';
