import { Button } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import Image from 'next/image';

type Props = {
  onClick: () => void;
  icons: Record<string, string>;
  buttonStyle: string;
  buttonIconStyle: string;
  baseDataTestId: string;
};

export function AddCorrespondenceButton({
  onClick,
  icons,
  buttonStyle,
  buttonIconStyle,
  baseDataTestId,
}: Props) {
  const { t } = useTranslation('payApplication');

  return (
    <Button
      variant="outline"
      className={buttonStyle}
      data-testid={`${baseDataTestId}-button-add-correspondence`}
      onClick={onClick}
    >
      <Image
        alt="Add correspondence button"
        src={formatIBAssetsUrl(icons['icon.addLocation-icon'])}
        width={24}
        height={24}
        className={buttonIconStyle}
        data-testid={`${baseDataTestId}-button-add-correspondence-icon`}
      />
      {t('companyDetails.correspondence.address')}
    </Button>
  );
}
