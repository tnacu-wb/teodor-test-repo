'use client';

import { WizardPage } from '@whitbread-eos/layout';
import {
  useTranslation,
  getPathForLocale,
  getCountryLanguageByLocale,
  formatAnalyticsFunnelStep,
  analytics,
} from '@whitbread-eos/utils';
import { getDetailsFromToken } from '@whitbread-eos/utils/server';
import Link from 'next/link';
import React, { useEffect } from 'react';

import CardLink, { CardLinkProps } from '~components/innBusiness/CardLink/card-link';

interface Props {
  locale: string;
  token?: string;
}

export default function Confirmation({ locale, token }: Props) {
  const baseDataTestId = 'Confirmation';

  const { t } = useTranslation(['auth']);
  const { isBusinessPayManager } = getDetailsFromToken(token || '');
  const { language } = getCountryLanguageByLocale(locale);

  useEffect(() => {
    window?._satellite?.track('signUpCompleteEmailVerified');
  }, []);

  useEffect(() => {
    const funnelStep = formatAnalyticsFunnelStep('PIB', 'Confirm your Account - Welcome', language);
    analytics.update({ funnel_step: funnelStep });
  }, [language]);

  let cards: CardLinkProps[] = [
    {
      title: t('auth.signup.confirmation.option1.title'),
      subtitle: t('auth.signup.confirmation.option1.description'),
      alt: `${t('auth.signup.confirmation.option1.title')} icon`,
      icon: t('auth.signup.confirmation.option1.icon'),
      href: getPathForLocale(locale, t('auth.signup.confirmation.option1.link')),
      baseDataTestId: 'ApplyInnBusinessPayCard',
      buttonText: t('auth.signup.confirmation.option1.button'),
      isExternalHref: true,
    },
    {
      title: t('auth.signup.confirmation.option2.title'),
      subtitle: t('auth.signup.confirmation.option2.description'),
      alt: `${t('auth.signup.confirmation.option2.title')} icon`,
      icon: t('auth.signup.confirmation.option2.icon'),
      href: getPathForLocale(locale, t('auth.signup.confirmation.option2.link')),
      baseDataTestId: 'TakeATourCard',
      buttonText: t('auth.signup.confirmation.option2.button'),
    },
    {
      title: t('auth.signup.confirmation.option3.title'),
      subtitle: t('auth.signup.confirmation.option3.description'),
      alt: `${t('auth.signup.confirmation.option3.title')} icon`,
      icon: t('auth.signup.confirmation.option3.icon'),
      href: getPathForLocale(locale, t('auth.signup.confirmation.option3.link')),
      baseDataTestId: 'AddACardCard',
      buttonText: t('auth.signup.confirmation.option3.button'),
    },
    {
      title: t('auth.signup.confirmation.option4.title'),
      subtitle: t('auth.signup.confirmation.option4.description'),
      alt: `${t('auth.signup.confirmation.option4.title')} icon`,
      icon: t('auth.signup.confirmation.option4.icon'),
      href: getPathForLocale(locale, t('auth.signup.confirmation.option4.link')),
      baseDataTestId: 'AddAnEmployeeCard',
      buttonText: t('auth.signup.confirmation.option4.button'),
    },
  ];

  cards = isBusinessPayManager
    ? cards.filter((card) => card.baseDataTestId !== 'AddACardCard')
    : cards;
  return (
    <WizardPage>
      <div
        data-testid={`${baseDataTestId}-wrapper`}
        className="my-16 mx-[4.125rem] mobile:my-6 mobile:mx-4 flex flex-col gap-12"
      >
        <h1 className="text-[2.5rem] font-bold text-secondaryColor">
          {t('auth.signup.confirmation.title')}
        </h1>
        <div className="grid grid-cols-4 mobile:grid-cols-1 gap-4 place-items-center">
          {cards.map((card) => (
            <CardLink
              key={card.baseDataTestId}
              title={card.title}
              subtitle={card.subtitle}
              alt={card.alt}
              icon={card.icon}
              baseDataTestId={card.baseDataTestId}
              href={card.href}
              variant="button-link"
              buttonText={card.buttonText}
            />
          ))}
        </div>
        <div className="flex justify-center">
          <Link
            data-testid={`${baseDataTestId}-skip-link`}
            href={getPathForLocale(locale, 'homepage')}
            prefetch
            className="text-base font-medium p-0 h-[1.5rem] underline text-secondaryColor underline-offset-2 cursor-pointer self-center"
          >
            {t('auth.signup.confirmation.skip.link')}
          </Link>
        </div>
      </div>
    </WizardPage>
  );
}
