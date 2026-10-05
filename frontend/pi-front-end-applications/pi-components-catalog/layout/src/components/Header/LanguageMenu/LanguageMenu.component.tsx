import { CountryInnB, LOCALES } from '@whitbread-eos/api';
import {
  Button,
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuCheckboxItem,
  DropdownMenuTrigger,
} from '@whitbread-eos/atoms/ui';
import { InnBLink } from 'business-booker/src/components/innBusiness/InnBLink';
import Image from 'next/image';

interface LanguageMenuProps {
  languages: CountryInnB[];
  language: string;
  enIcon: string;
  deIcon: string;
}

const LanguageMenu = ({ languages, language = 'en', enIcon, deIcon }: LanguageMenuProps) => {
  const country = language === 'en' ? 'gb' : 'de';
  const selectedLanguage = languages?.find((lang) => lang?.code?.toLowerCase() === country);

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          className={buttonStyle + ' focus-visible:ring-2 focus-visible:ring-primaryColor'}
          data-testid="Language-Switcher-Button"
        >
          <Image
            className={iconStyle}
            src={language === 'en' ? enIcon : deIcon}
            alt={language === 'en' ? 'English' : 'Deutsch'}
            width={24}
            height={24}
          />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent
        className={dropdownStyle}
        align="end"
        avoidCollisions={false}
        data-testid="Language-Switcher-Dropdown"
      >
        {languages?.map((language) => (
          <InnBLink
            key={language.code}
            href={`/${language.code === 'de' ? LOCALES.DE : LOCALES.EN}/homepage`}
            prefetch={false}
          >
            <DropdownMenuCheckboxItem
              checked={selectedLanguage?.code === language.code}
              className={itemStyle}
              indicatorPosition="right"
              data-testid={`${language.language}-Button`}
            >
              <Image
                className={dropdownIconStyle}
                loading="eager"
                src={language.code === 'gb' ? enIcon : deIcon}
                alt={language?.language ?? ''}
                width={24}
                height={24}
              />
              {language.language}
            </DropdownMenuCheckboxItem>
          </InnBLink>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default LanguageMenu;

const buttonStyle =
  'focus-visible:ring-0 focus-visible:ring-offset-0 rounded-full w-11 h-11 bg-lightGrey5 rounded-full hover:bg-initial shrink-0';
const dropdownStyle = 'w-[12.5rem] shadow p-0 box-border border-lightGrey3 rounded';
const itemStyle = 'text-base px-4 py-2.5 hover:bg-lightGrey5 gap-4';
const iconStyle = 'text-transparent';
const dropdownIconStyle = 'text-transparent';
