'use client';

import { useTranslation } from '@whitbread-eos/utils';
import { useRouter } from 'next/navigation';

import { revalidateCacheOnLink } from '../../revalidate-link';

export type EditButtonProps = {
  href: string;
};

export function EditButton({ href }: EditButtonProps) {
  const { t } = useTranslation(['cards']);
  const router = useRouter();

  const handleClick = async () => {
    await revalidateCacheOnLink(href);
    router.push(href);
  };

  return (
    <button
      type="button"
      className={editStyle}
      onClick={handleClick}
      data-testid={'Edit-WL-Card-Button'}
    >
      {t('cards.cardMgmt.columns.edit')}
    </button>
  );
}

const editStyle = 'text-secondaryColor underline';
