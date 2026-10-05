import { LOCALES } from '@whitbread-eos/api';
import { useTranslation, getPathForLocale, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Image from 'next/image';
import Link from 'next/link';

type Props = {
  locale: LOCALES;
  showContact?: boolean;
  showFaqs?: boolean;
};

export const FormFooter = ({ locale, showContact = true, showFaqs = true }: Props) => {
  const { t } = useTranslation(['auth']);
  return (
    <div className="flex flex-col">
      <div className="form-box-separator" />
      <p className="mt-6 text-darkGrey2">{t('auth.payApp.security.needHelp')}</p>
      <div className="flex flex-row gap-2 mt-4">
        {showContact && (
          <Link
            className={buttonStyle}
            href={getPathForLocale(locale.toLowerCase() as LOCALES, `contact`)}
          >
            <Image
              width={20}
              height={20}
              alt={''}
              src={formatIBAssetsUrl(t('auth.payApp.contact.icon'))}
            />
            <span className={buttonTextStyle}>{t('auth.payApp.security.contact')}</span>
          </Link>
        )}
        {showFaqs && (
          <Link
            className={buttonStyle}
            href={getPathForLocale(locale.toLowerCase() as LOCALES, `faqs`)}
          >
            <Image
              width={20}
              height={20}
              alt={''}
              src={formatIBAssetsUrl(t('auth.payApp.faq.icon'))}
            />
            <span className={buttonTextStyle}>{t('auth.payApp.security.faqs')}</span>
          </Link>
        )}
      </div>
    </div>
  );
};

const buttonStyle =
  'flex flex-col items-center justify-center rounded-lg bg-white p-4 min-w-[80px] h-[80px] border-[1px] border-lightGrey3';
const buttonTextStyle = 'text-sm font-medium mt-1 text-darkGrey1';
