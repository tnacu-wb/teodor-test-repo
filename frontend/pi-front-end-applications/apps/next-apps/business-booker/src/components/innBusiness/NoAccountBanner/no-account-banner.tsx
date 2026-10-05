'use client';

import { LOCALES, Scheme } from '@whitbread-eos/api';
import { Button, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl, getPathForLocale } from '@whitbread-eos/utils';
import { appPreCheck, getDetailsFromToken } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

import { ExistingAccountModal } from '../ExistingAccountModal';

type Props = {
  token: string;
  locale: LOCALES;
  scheme: Scheme;
};

export function NoAccountBanner({ locale, token, scheme }: Props) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { t } = useTranslation('cards');
  const baseDataTestId = 'NoAccountBanner';
  const router = useRouter();
  const [appCheckResult, setAppCheckResult] = useState<any>(null);
  const { isTravelManager, isBusinessPayManager } = getDetailsFromToken(token);

  const handleButtonClick = async (applyButton: boolean) => {
    let response = appCheckResult;

    if (!response) {
      response = await appPreCheck(token, scheme);
      setAppCheckResult(response);
    }

    if (response?.isTetheredUser === false) {
      setIsModalOpen(true);
    } else if (applyButton) {
      router.push(getPathForLocale(locale?.toLowerCase() as LOCALES, 'business-pay/apply'));
    } else {
      window?._satellite?.track('linkAccount');
      router.push(`${process?.env?.NEXT_PUBLIC_WORLDLINE_HOST ?? ''}/BBLinkCode.aspx`);
    }
  };

  const handleOnCloseModal = () => {
    setIsModalOpen(false);
  };

  return (
    <>
      <ExistingAccountModal
        isModalOpen={isModalOpen}
        locale={locale}
        onClose={handleOnCloseModal}
      />
      <div
        className={getApplyBannerStyle(isTravelManager)}
        data-testid={`${baseDataTestId}-Container`}
      >
        <div className={getApplyBannerContentStyle(isTravelManager)}>
          <div className={applyNowTextStyle} data-testid={`${baseDataTestId}-Texts`}>
            <span className={applyNowTitleStyle} data-testid={`${baseDataTestId}-Title`}>
              {t('cardMgmt.applyBanner.title')}
            </span>
            <span className={applySubtitleStyle} data-testid={`${baseDataTestId}-SubTitle`}>
              <SanitizedContent>{t('cardMgmt.applyBanner.subtitle')}</SanitizedContent>
            </span>
          </div>
          <div className={applyNowButtonsStyle} data-testid={`${baseDataTestId}-Buttons`}>
            {(isTravelManager || isBusinessPayManager) && (
              <Button
                variant="default"
                className={buttonStyle}
                data-testid={`${baseDataTestId}-ApplyNowButton`}
                onClick={() => handleButtonClick(true)}
              >
                {t('cardMgmt.applyBanner.applyNowButton')}
              </Button>
            )}
            <Button
              variant="alternativeDefault"
              data-testid={`${baseDataTestId}-LinkAccountButton`}
              className="w-full"
              onClick={() => handleButtonClick(false)}
            >
              {t('cardMgmt.applyBanner.linkAccountButton')}
            </Button>
          </div>
        </div>

        <div className={applyBannerImageContainerStyle}>
          <Image
            className={applyBannerImageStyle}
            src={formatIBAssetsUrl(t('cardMgmt.applyBanner.image'))}
            alt={'Inn Business Apply Card'}
            width={365}
            height={306}
            unoptimized
            data-testid={`${baseDataTestId}-Image`}
          />
        </div>
      </div>
    </>
  );
}

const getApplyBannerStyle = (isTravelManager?: boolean) => {
  const baseStyle = 'flex bg-lightGrey5 px-6 rounded-lg mobile:flex-col-reverse mb-12';
  const paddingStyle = isTravelManager ? 'py-8' : 'py-0 mobile:py-8';
  return `${baseStyle} ${paddingStyle}`;
};

const getApplyBannerContentStyle = (isTravelManager?: boolean) => {
  const baseStyle = 'flex flex-col w-[40%] mobile:w-full mobile:mt-8';
  const gapStyle = isTravelManager ? 'gap-10 mobile:gap-6' : 'gap-8 mobile:gap-4';
  const justifyStyle = isTravelManager ? '' : 'justify-center';
  return `${baseStyle} ${gapStyle} ${justifyStyle}`;
};

const applyNowTextStyle = 'flex flex-col gap-4';
const applyNowButtonsStyle = 'flex flex-col gap-4';
const applyNowTitleStyle = 'text-[1.813rem] leading-8 text-secondaryColor font-black mb-4';
const applySubtitleStyle = 'text-lg leading-6 text-darkGrey1';
const applyBannerImageContainerStyle = 'flex justify-center items-center grow';
const applyBannerImageStyle = 'mobile:w-[13rem]';
const buttonStyle = 'w-full';
