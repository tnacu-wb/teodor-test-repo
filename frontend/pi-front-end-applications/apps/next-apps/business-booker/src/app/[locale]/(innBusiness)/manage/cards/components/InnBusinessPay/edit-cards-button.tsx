'use client';

import { Scheme } from '@whitbread-eos/api';
import { WorldlineLink, Button } from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Image from 'next/image';

type Props = {
  tetheredGuid: string;
  returnUrl: string;
  icons: Record<string, string>;
  scheme: Scheme;
};

export function EditCardsButton({ tetheredGuid, scheme, returnUrl, icons }: Props) {
  const { t } = useTranslation('cards');

  return (
    <WorldlineLink
      tetheredGuid={tetheredGuid}
      className={editCardsButtonStyle}
      worldlinePostUrl={
        scheme === 'GB'
          ? process.env.NEXT_PUBLIC_WORLDLINE_POST_URL
          : process.env.NEXT_PUBLIC_WORLDLINE_POST_URL_DE
      }
      worldlineRequestedPage="CardsList.aspx"
      worldlineReturnUrl={returnUrl}
      formClassName={worldlineLinkFormStyle}
      scheme={scheme}
      renderButton={(onClick: (e: Event) => void) => (
        <Button
          variant="outline"
          data-testid="InnBusinessPayTab-edit-cards-button"
          className={editCardsButtonStyle}
          onClick={(e: MouseEvent) => {
            window?._satellite?.track('editCards');
            onClick(e);
          }}
        >
          {t('cardMgmt.editCards.label')}
          <Image
            alt="external-link"
            src={formatIBAssetsUrl(icons['icon.manageEmployees-icon'])}
            width={16}
            height={16}
            className={editCardsButtonIconStyle}
          />
        </Button>
      )}
    ></WorldlineLink>
  );
}

const worldlineLinkFormStyle = 'mobile:w-full';
const editCardsButtonStyle =
  'text-lg font-semibold mr-6 mobile:mr-0 px-10 mobile:w-full mobile:mt-4';
const editCardsButtonIconStyle = 'ml-2';
