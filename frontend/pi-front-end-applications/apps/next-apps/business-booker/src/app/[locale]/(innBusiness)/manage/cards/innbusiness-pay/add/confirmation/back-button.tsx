'use client';

import { Button } from '@whitbread-eos/atoms/ui';
import { useRouter } from 'next/navigation';

import { revalidateCacheOnLink } from '../../../components/revalidate-link';

interface Props {
  text: string;
  className: string;
  path: string;
}

export const BackButton = ({ text, path, className }: Readonly<Props>) => {
  const router = useRouter();

  const handleClick = async () => {
    await revalidateCacheOnLink(path);
    router.push(path);
  };

  return (
    <Button
      onClick={handleClick}
      data-testid="Back-Button"
      variant="dialogDefault"
      className={className}
    >
      {text}
    </Button>
  );
};
