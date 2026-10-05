import { CountryInnB, MenuInnB, SidebarInnB } from '@whitbread-eos/api';
import { formatIBAssetsUrl } from '@whitbread-eos/utils/server';
import Image from 'next/image';

type Props = {
  menuLabels: MenuInnB;
  languages: CountryInnB[];
  collapseIcons: SidebarInnB;
};

export function ImagePreloader({ menuLabels, languages, collapseIcons }: Props) {
  const { home, spending, manage, bookings, contact } = menuLabels;

  const urls = [
    home?.icon,
    home?.iconActive,
    spending?.icon,
    spending?.iconActive,
    manage?.icon,
    manage?.iconActive,
    bookings?.icon,
    bookings?.iconActive,
    contact?.icon,
    contact?.iconActive,
    languages?.find((lang) => lang?.code?.toLowerCase() === 'gb')?.flagUrl,
    languages?.find((lang) => lang?.code?.toLowerCase() === 'de')?.flagUrl,
    collapseIcons?.collapse,
    collapseIcons?.expand,
  ].filter((x) => x);

  return (
    <span data-testid="ImagePreloader">
      {urls.map((url) => (
        <Image key={url} src={formatIBAssetsUrl(url)} alt="preload" width={0} height={0} priority />
      ))}
    </span>
  );
}
