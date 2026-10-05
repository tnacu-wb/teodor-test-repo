'use client';

import { LOCALES, requestStatus } from '@whitbread-eos/api';
import { Checkbox, SanitizedContent, useToast, Notification } from '@whitbread-eos/atoms/ui';
import {
  useTranslation,
  updateContactPreferences,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { useRouter, usePathname } from 'next/navigation';
import { useEffect, useState, useRef, useCallback } from 'react';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';

type ContactPreferences = {
  optIn: boolean;
  secondPartyOptIn: boolean;
  thirdPartyVendorsOptIn: boolean;
};

type ContactPreferencesFormProps = {
  icons: Record<string, string>;
  email: string;
  token: string;
  initialPreferences: ContactPreferences;
};

const BRANDS = {
  premierInn: [
    { name: 'Premier Inn', iconKey: 'icon.brand.pi.round' },
    { name: 'Premier Inn Zip', iconKey: 'icon.brand.zip.round' },
    { name: 'Premier Inn hub', iconKey: 'icon.brand.hub.round' },
  ],
  premierInnDE: [{ name: 'Premier Inn', iconKey: 'icon.brand.pi.round' }],
  restaurants: [
    { name: 'Beefeater', iconKey: 'icon.brand.beefeater.round' },
    { name: 'Cookhouse + Pub', iconKey: 'icon.brand.cookhouseandpub.round' },
    { name: 'Table Table', iconKey: 'icon.brand.tabletable.round' },
    { name: 'Brewers Fayre', iconKey: 'icon.brand.brewersfayre.round' },
    { name: 'Bar + Block Steakhouse', iconKey: 'icon.brand.barandblock.round' },
    { name: 'Whitbread Inns', iconKey: 'icon.brand.whitbreadinns.round' },
  ],
} as const;

export function ContactPreferencesForm({
  icons,
  email,
  initialPreferences,
  token,
}: ContactPreferencesFormProps) {
  const baseDataTestId = 'ContactPreferences';
  const [preferences, setPreferences] = useState<ContactPreferences>({
    optIn: initialPreferences?.optIn ?? false,
    secondPartyOptIn: initialPreferences?.secondPartyOptIn ?? false,
    thirdPartyVendorsOptIn: initialPreferences?.thirdPartyVendorsOptIn ?? false,
  });
  const [isUpdating, setIsUpdating] = useState(false);
  const [showFailError, setShowFailError] = useState(false);

  const { t } = useTranslation('profile');
  const { toast } = useToast();
  const router = useRouter();
  const pathname = usePathname();
  const locale = pathname?.split('/')[1];
  const isGerman = locale === LOCALES.DE;
  const errorRef = useRef<HTMLDivElement>(null);

  const handlePreferencesUpdate = useCallback(
    async (newPreferences: ContactPreferences) => {
      try {
        setIsUpdating(true);
        setShowFailError(false);

        const response = await updateContactPreferences(token, locale, newPreferences);

        if (response?.status === requestStatus.success) {
          const successMessage = isGerman ? (
            <SanitizedContent>
              {t('contact.center.preferences.update.confirmation')}
            </SanitizedContent>
          ) : (
            t('profile.notification.success')
          );

          toast({
            content: successMessage,
            icon: formatIBAssetsUrl(icons['icon.notification.success']),
          });
          router.push(`/${locale}/profile`);
        } else {
          setShowFailError(true);
        }
      } catch (error) {
        setShowFailError(true);
        console.error('Failed to update preferences:', error);
      } finally {
        setIsUpdating(false);
      }
    },
    [token, locale, router, toast, isGerman, icons, t]
  );

  useEffect(() => {
    if (showFailError && errorRef.current) {
      const yOffset = -100;
      const y = errorRef.current.getBoundingClientRect().top + window.pageYOffset + yOffset;
      window.scrollTo({ top: y, behavior: 'smooth' });
    }
  }, [showFailError]);

  const handleCheckboxChange = useCallback(
    (field: keyof ContactPreferences) => (checked: boolean) => {
      setPreferences((prev) => {
        if (field === 'optIn' && !checked) {
          // If main checkbox is unchecked, uncheck dependent checkboxes
          return {
            ...prev,
            optIn: false,
            secondPartyOptIn: false,
            thirdPartyVendorsOptIn: false,
          };
        }
        return { ...prev, [field]: checked };
      });
    },
    []
  );

  const handleBack = useCallback(() => {
    router.push(`/${locale}/profile`);
  }, [router, locale]);

  const handleSubmit = useCallback(() => {
    handlePreferencesUpdate(preferences);
  }, [preferences, handlePreferencesUpdate]);

  const renderBrandList = useCallback(
    (brands: typeof BRANDS.premierInn | typeof BRANDS.restaurants | typeof BRANDS.premierInnDE) => (
      <div className={brandListContainerStyle}>
        {brands.map((brand, index) => (
          <div key={index} className={brandItemStyle}>
            <div className={brandIconContainerStyle}>
              <div className={brandIconBackgroundStyle} />
              <div className={brandIconWrapperStyle}>
                <Image
                  src={formatIBAssetsUrl(icons[brand.iconKey])}
                  alt={brand.name}
                  width={32}
                  height={32}
                  className={brandIconStyle}
                />
              </div>
            </div>
            <span className={brandNameStyle}>{brand.name}</span>
          </div>
        ))}
      </div>
    ),
    [icons]
  );

  return (
    <div data-testid={baseDataTestId}>
      <div ref={errorRef}>
        {showFailError && (
          <div className={errorContainerStyle}>
            <Notification
              type="error"
              icon={formatIBAssetsUrl(icons['icon.notification.error'])}
              message={t('notification.message.error')}
              data-testid={`${baseDataTestId}-Error`}
            />
          </div>
        )}
      </div>

      <button
        onClick={handleBack}
        className={backButtonStyle}
        data-testid={`${baseDataTestId}-BackButton`}
      >
        <Image
          src={formatIBAssetsUrl(icons['icon.chevron.left.purple'])}
          alt="arrow-left"
          width={16}
          height={16}
        />
        <span className={backButtonTextStyle}>{t('centre.button.back')}</span>
      </button>

      <div className={bannerContainerStyle}>
        <h1 className={bannerTitleStyle}>{t('centre.banner.title')}</h1>
        <p className={bannerTextStyle}>{t('centre.banner.firstLine')}</p>
      </div>

      <div className={secondBannerContainerStyle}>
        <p className={bannerTextStyle}>{t('centre.banner.secondLine')}</p>
        <ul className={bannerListStyle}>
          <li className={bannerListItemStyle}>{t('centre.banner.thirdLine')}</li>
          <li>{t('centre.banner.fifthLine')}</li>
        </ul>
      </div>

      <div className={emailSectionStyle}>
        <h2 className={emailTitleStyle}>{t('preference.email.title')}</h2>
        <p className={descriptionStyle}>{t('preference.email.description')}</p>
        <p className={emailStyle}>
          <b>{email}</b>
        </p>
      </div>

      {isGerman && (
        <div className={germanNotificationContainerStyle}>
          <Notification
            type="info"
            icon={formatIBAssetsUrl(icons['icon.notification.info'])}
            message={t('contact.center.preferences.marketing.consent.required')}
            data-testid={`${baseDataTestId}-InfoNotification`}
          />
        </div>
      )}

      <div className={isGerman ? premierInnContainerStyleDE : premierInnContainerStyle}>
        <div className={flexContainerStyle}>
          <div className={flexOneStyle}>
            <div className={titleContainerStyle}>
              <h2 className={titleStyle}>{t('form.firstParty.title')}</h2>
              <p className={descriptionStyle}>{t('form.firstParty.description')}</p>
            </div>
            <div className={checkboxWrapperStyle}>
              <Checkbox
                id="premierinn"
                checked={preferences.optIn}
                onCheckedChange={handleCheckboxChange('optIn')}
                className={checkboxStyle}
                data-testid={`${baseDataTestId}-PremierInnCheckbox`}
                aria-checked={preferences.optIn}
              />
              <label htmlFor="premierinn" className={labelStyle}>
                <SanitizedContent>{t('form.firstParty.subscribe.email')}</SanitizedContent>
              </label>
            </div>
            {isGerman && (
              <p className={germanNoteStyle}>
                {t('contact.center.preferences.brands.unavailable.germany')}
              </p>
            )}
          </div>
          <div className={brandContainerStyle}>
            {renderBrandList(isGerman ? BRANDS.premierInnDE : BRANDS.premierInn)}
          </div>
        </div>
      </div>

      {!isGerman && (
        <>
          <div className="p-6 border border-lightGrey4 bg-lightGrey5 border-b-0 border-t-0 mb-0 flex-col md:flex-row flex gap-10">
            <div className={checkboxContainerStyle}>
              <div className={checkboxHeaderStyle}>
                <h2 className={titleStyle}>{t('form.secondParty.title')}</h2>
                <p className={descriptionStyle}>{t('form.secondParty.description')}</p>
              </div>
              <div className={checkboxWrapperStyle}>
                <Checkbox
                  id="restaurants"
                  checked={preferences.secondPartyOptIn}
                  onCheckedChange={handleCheckboxChange('secondPartyOptIn')}
                  className={`${checkboxStyle} ${!preferences.optIn ? uncheckedStyle : ''}`}
                  disabled={!preferences.optIn}
                  data-testid={`${baseDataTestId}-RestaurantsCheckbox`}
                  aria-checked={preferences.secondPartyOptIn}
                />
                <label htmlFor="restaurants" className={labelStyle}>
                  <SanitizedContent>{t('form.firstParty.subscribe.email')}</SanitizedContent>
                </label>
              </div>
            </div>
            {renderBrandList(BRANDS.restaurants)}
          </div>

          <div className="p-6 border border-lightGrey4 bg-lightGrey5 rounded-t-none mb-8">
            <div className={checkboxContainerStyle}>
              <div className={checkboxHeaderStyle}>
                <h2 className={titleStyle}>{t('form.thirdParty.title')}</h2>
                <p className={descriptionStyle}>{t('form.thirdParty.description')}</p>
              </div>
              <div className={checkboxWrapperStyle}>
                <Checkbox
                  id="thirdparty"
                  checked={preferences.thirdPartyVendorsOptIn}
                  onCheckedChange={handleCheckboxChange('thirdPartyVendorsOptIn')}
                  className={`${checkboxStyle} ${!preferences.optIn ? uncheckedStyle : ''}`}
                  disabled={!preferences.optIn}
                  data-testid={`${baseDataTestId}-ThirdPartyCheckbox`}
                  aria-checked={preferences.thirdPartyVendorsOptIn}
                />
                <label htmlFor="thirdparty" className={labelStyle}>
                  <SanitizedContent>{t('form.firstParty.subscribe.email')}</SanitizedContent>
                </label>
              </div>
            </div>
          </div>
        </>
      )}

      <div className={buttonContainerStyle}>
        <button
          onClick={handleSubmit}
          disabled={isUpdating}
          className={submitButtonStyle}
          data-testid={`${baseDataTestId}-SubmitButton`}
        >
          {t('form.button.save')}
        </button>
        <button
          onClick={handleBack}
          className={cancelButtonStyle}
          data-testid={`${baseDataTestId}-CancelButton`}
        >
          {t('form.button.cancel')}
        </button>
      </div>

      {!isUpdating && <ReviewChanges />}
    </div>
  );
}

const errorContainerStyle = 'mb-6';
const backButtonStyle = 'flex items-center text-secondaryColor group mb-6 md:mb-8';
const backButtonTextStyle = 'text-sm font-medium leading-[140%] underline underline-offset-2';
const bannerContainerStyle = 'w-full md:w-[620px]';
const bannerTitleStyle =
  'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl';
const bannerTextStyle = 'text-darkGrey1 text-base font-normal leading-[150%] mt-4';
const secondBannerContainerStyle = 'w-full md:w-[620px] mb-6 md:mb-8';
const bannerListStyle =
  'list-disc pl-5 md:pl-8 text-darkGrey1 text-base font-normal leading-[150%]';
const bannerListItemStyle = 'mb-1';
const emailStyle = 'text-darkGrey1 text-base font-normal mt-4';
const checkboxContainerStyle = 'flex-1 flex flex-col gap-6';
const checkboxHeaderStyle = 'flex flex-col gap-2';
const titleStyle = 'text-darkGrey1 text-xl font-semibold';
const emailTitleStyle = 'text-darkGrey1 text-xl font-semibold mb-4';
const emailSectionStyle = 'w-full md:w-[620px] mb-10 md:mb-12';
const germanNotificationContainerStyle = 'w-full mb-6';
const premierInnContainerStyle = 'p-6 border border-lightGrey4 bg-white rounded-b-none mb-0';
const premierInnContainerStyleDE = 'p-6 border border-lightGrey4 bg-white rounded mb-8';
const flexContainerStyle = 'flex flex-col md:flex-row gap-10';
const flexOneStyle = 'flex-1';
const titleContainerStyle = 'mb-6';
const germanNoteStyle = 'text-darkGrey1 text-base font-normal mt-4';
const brandContainerStyle = 'w-full md:w-[310px]';
const descriptionStyle = 'text-darkGrey1 text-base font-normal leading-normal';
const checkboxWrapperStyle = 'flex items-center gap-2 mb-4';
const checkboxStyle = 'w-5 h-5 border-lightGrey1 text-primaryColor';
const labelStyle = 'text-darkGrey1 text-base font-normal';
const brandListContainerStyle = 'w-full md:w-[310px] grid grid-cols-1 gap-4';
const brandItemStyle = 'flex items-center gap-3';
const brandIconContainerStyle = 'relative w-10 h-10';
const brandIconBackgroundStyle = 'absolute inset-0 rounded-full';
const brandIconWrapperStyle = 'absolute inset-0 flex items-center justify-center';
const brandIconStyle = 'object-contain';
const brandNameStyle = 'text-darkGrey1 text-base';
const buttonContainerStyle = 'space-y-4 w-full';
const submitButtonStyle =
  'w-full md:w-[298px] h-14 flex justify-center items-center px-12 py-4 bg-primaryColor text-baseWhite rounded focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 disabled:bg-lightGrey3 disabled:text-lightGrey1';
const cancelButtonStyle = 'text-secondaryColor text-sm font-medium underline underline-offset-2';
const uncheckedStyle = 'opacity-50 cursor-not-allowed';
