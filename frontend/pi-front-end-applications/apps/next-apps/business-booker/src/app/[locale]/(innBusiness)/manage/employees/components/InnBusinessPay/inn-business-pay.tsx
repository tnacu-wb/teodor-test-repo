import { LOCALES } from '@whitbread-eos/api';
import { Button, InfoTooltip, SanitizedContent } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations,
  formatIBAssetsUrl,
  getPathForLocale,
} from '@whitbread-eos/utils/server';
import Image from 'next/image';
import Link from 'next/link';

import { AccountHolder } from '~components/innBusiness/AccountHolder/account-holder';

type Props = {
  locale?: LOCALES;
};

export async function InnBusinessPay({ locale }: Props) {
  const baseDataTestId = 'InnBusinessPayTab';
  const { language } = getCountryLanguageByLocale(locale);
  const { t, translations } = await getTranslations(language, ['users', 'icons']);
  const icons = translations?.['icons'] ?? {};
  const manageEmployeeLabel = t('users.userMgmt.manageEmployees.manageEmployeesCta.label');

  return (
    <div data-testid={`${baseDataTestId}-container`}>
      <div data-testid={`${baseDataTestId}-first-section`}>
        <div data-testid={`${baseDataTestId}-first-title-section`} className={firstTitleStyle}>
          <div className={titleStyle} data-testid={`${baseDataTestId}-first-title`}>
            <span className={titleStyle} data-testid={`${baseDataTestId}-title-text`}>
              {t('users.userMgmt.manageEmployees.innBusinessPay.title')}
            </span>
            <InfoTooltip
              className={tooltipStyle}
              content={
                <SanitizedContent>
                  {t('users.userMgmt.manageEmployees.innBusinessPay.tooltip')}
                </SanitizedContent>
              }
              testId={`${baseDataTestId}-InfoTooltip`}
              hoverVariant={true}
            >
              <Image
                alt={'Employee management info'}
                src={formatIBAssetsUrl(icons['icon.notification.info'])}
                width={26}
                height={26}
                className={infoTooltip}
                priority={true}
                data-testid={`${baseDataTestId}-title-icon`}
              />
            </InfoTooltip>
          </div>
          <div data-testid={`${baseDataTestId}-first-description`}>
            <p className={paragraphStyle}>
              {t('users.userMgmt.manageEmployees.innBusinessPay.description')}
            </p>
          </div>
        </div>
        <AccountHolder
          locale={locale}
          isUserManagement={true}
          accountHolderIcon={icons['icon.manageEmployees-icon']}
          manageEmployeeLabel={manageEmployeeLabel}
        />
      </div>
      <div data-testid={`${baseDataTestId}-second-section`} className={containerStyle}>
        <h2 className={titleStyle} data-testid={`${baseDataTestId}-second-title`}>
          {t('users.userMgmt.manageEmployees.innBusinessPay.cardHolders.title')}
        </h2>
        <div data-testid={`${baseDataTestId}-widget`} className={widgetStyle}>
          <Image
            data-testid={`${baseDataTestId}-new-innbusiness-pay-card-icon`}
            className={'mb-6'}
            src={formatIBAssetsUrl(icons['icon.newIbPayCard-icon'])}
            alt={'New Innbusiness card icon'}
            width={40}
            height={24}
          />
          <span data-testid={`${baseDataTestId}-widget-title`} className={nameStyle}>
            {t('users.userMgmt.manageEmployees.innBusinessPay.cardHolders.title')}
          </span>
          <div data-testid={`${baseDataTestId}-widget-description`}>
            {t('users.userMgmt.manageEmployees.innBusinessPay.card.description')}
          </div>
          <Link href={getPathForLocale(locale, `manage/cards?tab=innbusiness-pay`)}>
            <Button
              variant="default"
              data-testid={`${baseDataTestId}-create-innbusiness-pay-card`}
              className={buttonStyle}
            >
              {t('users.userMgmt.manageEmployees.innBusinessPay.createIbPayCardCta.label')}
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
}

const titleStyle = 'flex items-center text-xl font-bold leading-6';
const firstTitleStyle = 'mb-8';
const paragraphStyle = 'font-normal text-base';
const containerStyle = 'mt-12 border-t-[1px] border-lightGrey3 pt-12';
const widgetStyle = 'w-1/2 mobile:w-full mt-6 p-6 rounded-lg bg-white border border-lightGrey3';
const nameStyle = 'text-2xl text-secondaryColor font-bold flex pb-4';
const buttonStyle = 'mt-12 w-full mobile:w-full mobile:mt-4';
const infoTooltip = 'ml-2 cursor-pointer';
const tooltipStyle = 'w-80 rounded shadow-variantTooltip';
