/* eslint-disable prettier/prettier */
'use client';

import { Tabs, Columns, LinkItems, SocialMediaIcons } from '@whitbread-eos/api';
import { renderSanitizedHtml } from '@whitbread-eos/utils';
import { useTranslation, formatIBAssetsUrl, cn } from '@whitbread-eos/utils';
import Image from 'next/image';
import Link from 'next/link';
import React from 'react';

/* eslint-disable prettier/prettier */

const Footer = () => {
  const { t } = useTranslation(['common', 'footer']);

  const addSocialMediaButtons = () => {
    const socialMediaIcons = t('footer.socialMediaIcons');

    if (!Array.isArray(socialMediaIcons)) return;
    return socialMediaIcons?.map((item: SocialMediaIcons) => (
      <Link key={item?.label} href={item.linkSrc ?? '/'} target="_blank">
        <Image
          width={24}
          height={24}
          alt={item?.label ?? ''}
          src={formatIBAssetsUrl(item?.iconSrc)}
        ></Image>
      </Link>
    ));
  };

  const renderLinks = (links: LinkItems[] | undefined) => {
    if (!links || !links?.length) return;
    return links.map((link: LinkItems, item: number) => (
      <Link
        key={`${link?.name}-${item}`}
        className={linkStyle}
        href={link?.linkSrc ?? '/'}
      >
        {link?.name}
      </Link>
    ));
  };

  const renderColumns = (columns: Columns[] | undefined) => {
    if (!columns || !columns?.length) return;
    return columns?.map((column: Columns, i: number) => (
      <div key={`${column?.name}-${i}`} className={linksColumnStyle}>
        <span className={linksTitle}>{column?.name}</span>
        <div className={linksStyle}>{renderLinks(column?.linkItems)}</div>
      </div>
    ));
  };

  const addTabs = () => {
    const tabs = t('footer.tabs');
    if (!Array.isArray(tabs)) return;
    return tabs?.map((tab: Tabs, index: number) => (
      <div key={`${tab?.name}-${index}`} className={cn('links-container', linksContainerStyle)}>
        {renderColumns(tab?.columns)}
      </div>
    ));
  };

  const addBottomLinks = () => {
    const bottomLinks = t('footer.bottomLinks');
    if (!Array.isArray(bottomLinks)) return;
    return bottomLinks?.map((link: LinkItems) => (
      <React.Fragment key={link?.name}>
        <span className={separatorStyle}></span>
        <Link href={link?.linkSrc ?? '/'} target={link?.openInNewTab ? '_blank' : ''}>
          {link?.name}
        </Link>
      </React.Fragment>
    ));
  };

  return (
    <div data-testid="IB-Footer" className={footerWrapperStyle}>
      {addTabs()}
      <div className={bottomSectionStyle}>
        <div data-testid="IB-Footer-Quick-Links" className={quickLinksStyle}>
          <span>{renderSanitizedHtml(t('footer.copyrightInfo'))}</span>
          {addBottomLinks()}
        </div>
        <div data-testid="IB-Social-Media-Links" className={socialMediaContainerStyle}>
          {addSocialMediaButtons()}
        </div>
      </div>
    </div>
  );
};

export default Footer;

const footerWrapperStyle = 'py-12 px-12 w-full mobile:py-6 mobile:px-4';
const linksContainerStyle =
  'flex flex-wrap border-y-[1px] border-lightGrey3 pt-12 pb-12 w-full gap-x-4 gap-y-12 mobile:flex-col';
const linksColumnStyle = 'flex flex-grow flex-col w-[calc(25%-1rem)] mobile:w-full';
const linksTitle = 'text-lg leading-6 mb-4 font-bold text-darkGrey1';
const linksStyle = 'flex flex-col gap-2';
const linkStyle = 'leading-6 text-darkGrey1';
const bottomSectionStyle = 'flex mt-8 justify-between mobile:flex-col mobile:gap-y-6';
const separatorStyle = 'w-[4px] h-[4px] rounded-full bg-lightGrey1';
const quickLinksStyle = 'flex items-center gap-4 text-darkGrey1 text-sm flex-wrap';
const socialMediaContainerStyle = 'flex gap-4';
