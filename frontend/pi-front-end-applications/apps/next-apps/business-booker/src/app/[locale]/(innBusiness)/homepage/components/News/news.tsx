import { LOCALES } from '@whitbread-eos/api';
import { getCountryLanguageByLocale, getTranslations } from '@whitbread-eos/utils/server';

import { NewsArticle, Article, ArticleSkeleton } from './article';

type Props = {
  locale: LOCALES;
};

export type News = NewsArticle[];
type Translations = {
  homepage: {
    [key: string]: string;
  };
};

function getNewsArticlesFromTranslations(translations: Translations): NewsArticle[] {
  const newsArticles: NewsArticle[] = [];

  for (let i = 1; i <= 3; i++) {
    newsArticles.push({
      title: translations.homepage[`home.innbusinessPay.whatsNew.promo.item${i}.title`],
      description: translations.homepage[`home.innbusinessPay.whatsNew.promo.item${i}.description`],
      image: translations.homepage[`home.innbusinessPay.whatsNew.promo.item${i}.image`],
      link: translations.homepage[`home.innbusinessPay.whatsNew.promo.item${i}.link`],
      linkTarget: translations.homepage[`home.innbusinessPay.whatsNew.promo.item${i}.linkTarget`],
      badge: translations.homepage[`home.innbusinessPay.whatsNew.promo.item${i}.badge`],
      optionalLink:
        translations.homepage?.[`home.innbusinessPay.whatsNew.promo.item${i}.optionalLink`] ?? '',
      optionalLinkTarget:
        translations.homepage?.[`home.innbusinessPay.whatsNew.promo.item${i}.optionalLinkTarget`] ??
        '',
      optionalLinkLabel:
        translations.homepage?.[`home.innbusinessPay.whatsNew.promo.item${i}.optionalLinkLabel`] ??
        '',
    });
  }

  return newsArticles;
}

export async function News({ locale }: Props) {
  const baseDataTestId = `WhatsNew`;
  const { language } = getCountryLanguageByLocale(locale);
  const { t, translations } = await getTranslations(language, ['homepage']);
  const news = getNewsArticlesFromTranslations(translations);

  return (
    <section className={containerStyle} data-testid={`${baseDataTestId}-Container`}>
      <h2 className={h2Style} data-testid={`${baseDataTestId}-Title`}>
        {t('homepage.home.innbusinessPay.whatsNew.heading')}
      </h2>
      <div className={newsStyle} data-testid={`${baseDataTestId}-List`}>
        {news.map((article, index) => (
          <Article
            key={`home-news-${index}`}
            article={article}
            baseDataTestId={baseDataTestId}
            index={index}
          />
        ))}
      </div>
    </section>
  );
}

type SkeletonProps = {
  t: (key: string) => string;
};

export function NewsSkeleton({ t }: SkeletonProps) {
  return (
    <section className={containerStyle} data-testid={'WhatsNew-Skeleton'}>
      <h2 className={h2Style}>{t('homepage.home.innbusinessPay.whatsNew.heading')}</h2>
      <div className={newsStyle} data-testid={`WhatsNew-Skeleton-List`}>
        {[1, 2, 3].map((index) => (
          <ArticleSkeleton key={`home-news-skeleton-${index}`} />
        ))}
      </div>
    </section>
  );
}

const containerStyle = 'mt-12 border-t-[1px] border-lightGrey3 pt-12';
const h2Style = 'text-[1.44rem] leading-[2rem] font-bold mb-4';
const newsStyle = 'flex flex-col md:flex-row gap-6';
