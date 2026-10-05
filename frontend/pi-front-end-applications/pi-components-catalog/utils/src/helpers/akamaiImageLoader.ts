import getConfig from 'next/config';

type loaderProps = { src: string; width: number };

export function akamaiImageLoader({ src, width }: loaderProps): string {
  return `${src}?imwidth=${width}`;
}

export function isIVMEnabled(): boolean {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const { NEXT_IMAGE_UNOPTIMIZED } = publicRuntimeConfig;
  return NEXT_IMAGE_UNOPTIMIZED === 'true';
}
