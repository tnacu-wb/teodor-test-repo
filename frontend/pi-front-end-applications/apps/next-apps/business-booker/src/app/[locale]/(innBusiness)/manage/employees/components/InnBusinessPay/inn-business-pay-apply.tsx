import { LOCALES, Scheme } from '@whitbread-eos/api';

import { BenefitsBoxes } from '~components/innBusiness/BenefitsBoxes';
import { NoAccountBanner } from '~components/innBusiness/NoAccountBanner';

type Props = {
  locale?: LOCALES;
  token: string;
};

export async function InnBusinessPayApply({ locale, token }: Props) {
  const scheme = (locale === LOCALES.EN ? 'GB' : 'DE') as Scheme;

  return (
    <div className={applyPageStyle} data-testid="Inn-Business-Pay-Apply-Container">
      <NoAccountBanner locale={locale ?? LOCALES.EN} scheme={scheme} token={token} />
      <BenefitsBoxes locale={locale ?? LOCALES.EN} token={token} />
    </div>
  );
}

const applyPageStyle = 'flex flex-col gap-12';
