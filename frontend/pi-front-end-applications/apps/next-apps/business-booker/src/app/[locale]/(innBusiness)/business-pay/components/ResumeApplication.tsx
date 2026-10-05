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

export function ResumeApplication({
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
  const baseDataTestId = 'ResumeApplication';
  const router = useRouter();
  const { t } = useTranslation('payApplication');

  const handleBackToHome = async () => {
    const homepagePath = getPathForLocale(locale || LOCALES.EN, 'homepage');

    await revalidateCacheOnLink(homepagePath);
    router.push(homepagePath);
  };

  const handleResumeApplication = async () => {
    const url = getPathForLocale(
      locale || LOCALES.EN,
      `business-pay/apply?applicationId=${applicationId}&applicationGuid=${applicationGuid}`
    );

    await revalidateCacheOnLink(url);
    router.push(url);
  };

  const titleSection = (
    <div
      data-testid={`${baseDataTestId}-title`}
      className={`${titleStyle} flex items-center gap-2`}
    >
      <button
        onClick={handleBackToHome}
        className="flex items-center text-secondaryColor hover:text-secondaryColorHover"
        data-testid={`${baseDataTestId}-back-arrow`}
      >
        <Image
          alt={'Back arrow'}
          src={formatIBAssetsUrl(icons['icon.arrow.left.purple'])}
          width={26}
          height={26}
          className="mr-2"
        />
      </button>
      <span>{t('payapp.resume.title')}</span>
    </div>
  );

  const bottomButton = (
    <Button
      data-testid={`${baseDataTestId}-resume-application`}
      variant="default"
      size="default"
      className={buttonStyle}
      onClick={handleResumeApplication}
    >
      {t('payapp.resume.subheading')}
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
      showResumeLink={false}
      baseDataTestId={baseDataTestId}
      showShareAppWithColleague={showShareAppWithColleague}
    />
  );
}

const titleStyle = 'text-3xl font-black text-secondaryColor mb-6';
const buttonStyle = 'flex w-full';
