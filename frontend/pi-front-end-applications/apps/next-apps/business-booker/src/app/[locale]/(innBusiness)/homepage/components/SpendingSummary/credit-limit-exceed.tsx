import { LOCALES } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils/server';
import Image from 'next/image';

type Props = {
  locale: LOCALES;
  baseDataTestId?: string;
};

export async function CreditLimitExceed({ locale, baseDataTestId = '' }: Props) {
  const { language } = getCountryLanguageByLocale(locale);
  const { t } = await getTranslations(language, ['homepage', 'icons']);
  const alertIcon = t('icons.icon.notification.alert');

  return (
    <div
      className={'bg-tooltipError w-full p-6 rounded-md mt-4 relative'}
      data-testid={`${baseDataTestId}-CreditLimitExceed`}
    >
      <div className={'flex pl-8 relative'} data-testid={`${baseDataTestId}-CreditLimitExceed`}>
        {alertIcon && (
          <Image
            alt={''}
            src={formatIBAssetsUrl(alertIcon)}
            width={16}
            height={16}
            className="absolute top-[3px] left-0"
            data-testid={`${baseDataTestId}-CreditLimitExceedIcon`}
          />
        )}
        <div
          className={'flex flex-col text-left'}
          data-testid={`${baseDataTestId}-CreditLimitExceedContent`}
        >
          <h3 className={titleStyle} data-testid={`${baseDataTestId}-CreditLimitExceedTitle`}>
            {t('homepage.home.upcoming.spending.creditExceeded')}
          </h3>
          <p className={pStyle} data-testid={`${baseDataTestId}-CreditLimitExceedDescription`}>
            {t('homepage.home.upcoming.spending.creditExceededDescription')}
          </p>
        </div>
      </div>
    </div>
  );
}

const titleStyle = 'font-semibold text-[1rem] leading-[1.5rem]';
const pStyle = 'text-sm font-normal';
