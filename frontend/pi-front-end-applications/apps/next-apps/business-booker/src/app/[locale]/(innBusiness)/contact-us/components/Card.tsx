import { SanitizedContent, Button } from '@whitbread-eos/atoms/ui';
import Image from 'next/image';

type ImageProps = {
  src: string;
  alt: string;
  testId: string;
};

type Props = {
  title: string;
  description: string;
  info: string;
  imageProps?: ImageProps;
  testId: string;
  isEmail?: boolean;
  descriptionCustomStyle?: string;
  link?: string;
  onClick?: (e: React.MouseEvent<HTMLAnchorElement | HTMLButtonElement>) => void;
  isButton?: boolean;
};

export function Card({
  title,
  description,
  info,
  imageProps,
  testId,
  isEmail,
  descriptionCustomStyle,
  link,
  onClick,
  isButton,
}: Props) {
  const renderCardInfo = () => (
    <>
      {imageProps && (
        <Image
          alt={imageProps.alt}
          src={imageProps.src}
          width={24}
          height={24}
          className={imageStyle}
          data-testid={imageProps.testId}
        />
      )}
      <span>{info}</span>
    </>
  );

  const renderLink = () => {
    const handleClick = (e: React.MouseEvent<HTMLAnchorElement | HTMLButtonElement>) => {
      if (link === '#') {
        e.preventDefault();
      }
      if (onClick) {
        onClick(e);
      }
    };

    if (isButton) {
      return (
        <Button
          variant="alternativeDefault"
          data-testid={`${testId}-button`}
          className="w-full"
          onClick={handleClick}
        >
          {renderCardInfo()}
        </Button>
      );
    }

    if (isEmail || link) {
      const href = isEmail ? `mailto:${info}` : link;
      return (
        <a
          href={href}
          className={linkStyle}
          data-testid={`${testId}-link`}
          onClick={link ? handleClick : undefined}
        >
          {renderCardInfo()}
        </a>
      );
    }

    return renderCardInfo();
  };

  return (
    <div data-testid={testId} className={cardStyle}>
      <h3 className={titleStyle}>{title}</h3>
      <div className={`${descriptionStyle} ${descriptionCustomStyle || ''}`}>
        <SanitizedContent>{description}</SanitizedContent>
      </div>
      <div className={infoStyle}>{renderLink()}</div>
    </div>
  );
}

const cardStyle = 'p-6 border rounded-lg shadow-sm bg-white text-gray-900';
const titleStyle = 'font-semibold text-lg mb-4';
const descriptionStyle = 'mb-6 w-[70%] mobile:w-full';
const infoStyle =
  'flex items-center space-x-2 text-lg font-semibold ib-word-break leading-[1.375rem] whitespace-pre-line';
const linkStyle = 'flex items-center space-x-2 text-secondaryColor hover:underline';
const imageStyle = 'self-start mobile:mt-[0.25rem]';
