import { Skeleton } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl } from '@whitbread-eos/utils/server';
import Image from 'next/image';

type Props = {
  baseDataTestId: string;
  article: NewsArticle;
  index: number;
};

export type NewsArticle = {
  title: string;
  description: string;
  image: string;
  badge?: string;
  link?: string;
  linkTarget?: string;
  optionalLink?: string;
  optionalLinkTarget?: string;
  optionalLinkLabel?: string;
};

export function Article({ article, baseDataTestId, index }: Props) {
  const imgSrc = formatIBAssetsUrl(article.image);

  return (
    <article className={containerStyle} data-testid={`${baseDataTestId}-Article-${index}`}>
      <a
        href={article.link}
        target={article?.linkTarget ?? '_blank'}
        data-testid={`${baseDataTestId}-ArticleContainer-${index}`}
      >
        <div className="relative bg-lightGrey3 width-full h-[180px] overflow-hidden mb-2 rounded-lg">
          <Image
            alt={article.title}
            src={imgSrc}
            layout="fill"
            objectFit="cover"
            className={'w-full'}
            priority={true}
            data-testid={`${baseDataTestId}-ArticleImage-${index}`}
          />
          {article.badge && (
            <span
              className="absolute top-4 left-4 bg-tooltipInfo text-[0.81rem] leading-[1.2rem] font-semibold px-2 py-1 rounded"
              data-testid={`${baseDataTestId}-ArticleBadge-${index}`}
            >
              {article.badge}
            </span>
          )}
        </div>
      </a>
      <a
        href={article.link}
        target={article?.linkTarget ?? '_blank'}
        data-testid={`${baseDataTestId}-ArticleTitle-${index}`}
      >
        <h2 className={h2Style}>{article.title}</h2>
      </a>
      {article.description && (
        <a
          href={article.link}
          target={article?.linkTarget ?? '_blank'}
          data-testid={`${baseDataTestId}-ArticleDescription-${index}`}
        >
          <p className="text-sm">{article.description}</p>
        </a>
      )}
      {article.optionalLink && article.optionalLinkLabel && (
        <a
          href={article.optionalLink}
          target={article?.optionalLinkTarget ?? '_blank'}
          className="text-secondaryColor underline text-sm"
          data-testid={`${baseDataTestId}-ArticleOptionalLink-${index}`}
        >
          {article.optionalLinkLabel}
        </a>
      )}
    </article>
  );
}

export function ArticleSkeleton() {
  return (
    <article className={containerStyle} data-testid={'Article-Skeleton'}>
      <Skeleton className={'h-[180px] w-full mb-2 rounded-lg'} />
      <Skeleton className={'h-[24px] w-full md:w-1/2'} />
      <Skeleton className={'h-[60px] w-full'} />
      <Skeleton className={'h-[20px] w-1/2'} />
    </article>
  );
}

const containerStyle = 'flex-1 flex flex-col gap-2';
const h2Style = 'text-lg leading-6 font-bold text-secondaryColor';
