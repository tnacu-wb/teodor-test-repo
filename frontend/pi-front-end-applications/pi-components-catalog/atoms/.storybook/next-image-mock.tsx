import type { ImgHTMLAttributes } from 'react';

type ImageProps = ImgHTMLAttributes<HTMLImageElement> & {
  src: string;
  alt: string;
  priority?: boolean;
  quality?: number;
  placeholder?: 'blur' | 'empty';
  blurDataURL?: string;
};

const NextImageMock = ({
  priority: _priority,
  quality: _quality,
  placeholder: _placeholder,
  blurDataURL: _blurDataURL,
  src,
  alt,
  ...rest
}: ImageProps) => {
  // Drop Next.js-only props and render as a native image in Storybook.
  return <img src={src} alt={alt} {...rest} />;
};

export default NextImageMock;
