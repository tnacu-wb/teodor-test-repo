import { getTranslations, formatIBAssetsUrl, cn } from '@whitbread-eos/utils/server';
import { ChevronRight } from 'lucide-react';
import Image from 'next/image';
import Link from 'next/link';

interface Props {
  language: string;
  dataTestId: string;
  title?: string;
  description?: string;
  link?: string;
  bannerImage?: string;
}

export async function BannerPromo({
  language,
  dataTestId,
  title,
  description,
  link,
  bannerImage,
}: Props) {
  const { t } = await getTranslations(language, ['homepage']);
  const showDetailsContainer = title || description;
  const linkTarget = t('homepage.home.promoBanner.linkTarget');

  const renderImage = () => (
    <>
      <Image
        className={imgStyle}
        src={formatIBAssetsUrl(bannerImage)}
        alt={'Banner Promo Image'}
        width={1920}
        height={1080}
        data-testid={`${dataTestId}-BannerPromo-Image`}
        quality={100}
        priority={true}
      />
    </>
  );

  return (
    <div
      data-testid={`${dataTestId}-BannerPromo`}
      className={cn(containerStyle, bannerImage && imageContainerStyle)}
    >
      {!bannerImage ? (
        <>
          {showDetailsContainer && (
            <div className="mobile:pr-[1rem]">
              {title && (
                <span data-testid={`${dataTestId}-BannerPromo-Title`} className={titleStyle}>
                  {title}
                </span>
              )}
              {description && (
                <span
                  data-testid={`${dataTestId}-BannerPromo-Description`}
                  className={descriptionStyle}
                >
                  {description}
                </span>
              )}
            </div>
          )}
          {link && (
            <span data-testid={`${dataTestId}-BannerPromo-Link`} className={linkStyle}>
              <Link href={link} target={linkTarget}>
                <ChevronRight className="w-[1.5rem] h-[1.5rem]" />
              </Link>
            </span>
          )}
        </>
      ) : link ? (
        <Link href={link} target={linkTarget}>
          {renderImage()}
        </Link>
      ) : (
        renderImage()
      )}
    </div>
  );
}

const containerStyle =
  'relative flex items-center w-full min-h-[3.56rem] mt-px mb-[3rem] bg-secondaryColor py-[1rem] px-[1.5rem] rounded-lg text-baseWhite mobile:h-auto';
const imageContainerStyle = 'p-0 min-h-[4rem]';
const titleStyle = 'font-bold text-[1.25rem] leading-[1.5rem]  mr-[.5rem]';
const descriptionStyle = 'text-base';
const linkStyle = 'ml-auto';
const imgStyle = 'rounded-lg w-full object-cover h-[4rem] mobile:object-[43%]';
