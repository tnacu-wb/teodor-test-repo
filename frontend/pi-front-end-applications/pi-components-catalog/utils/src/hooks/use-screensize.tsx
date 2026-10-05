'use client';

import { useMediaQuery, useTheme } from '@chakra-ui/react';
import { useEffect } from 'react';

import { useAppData, useAppDataDispatch } from '../store/AppDataContext';

/**
 * Sets the current breakpoint in app context on screen size change.
 */
export default function useSetScreenSize() {
  const dispatch = useAppDataDispatch();
  const theme = useTheme();
  const [base, mobile, xs, sm, md, lg, xl] = useMediaQuery([
    `(max-width: calc(${theme?.breakpoints?.mobile} - 1px))`, // base: 0px - 319px
    `(min-width: ${theme?.breakpoints?.mobile}) and (max-width: calc(${theme?.breakpoints?.xs} - 1px))`, // mobile: 320px - 374px
    `(min-width: ${theme?.breakpoints?.xs}) and (max-width: calc(${theme?.breakpoints?.sm} - 1px))`, // xs: 375px - 575px
    `(min-width: ${theme?.breakpoints?.sm}) and (max-width: calc(${theme?.breakpoints?.md} - 1px))`, // sm: 576px - 767px
    `(min-width: ${theme?.breakpoints?.md}) and (max-width: calc(${theme?.breakpoints?.lg} - 1px))`, // md: 768px - 1279px
    `(min-width: ${theme?.breakpoints?.lg}) and (max-width: calc(${theme?.breakpoints?.xl} - 1px))`, // lg: 1280px - 1439px
    `(min-width: ${theme?.breakpoints?.xl})`, // xl: 1440px - ...
  ]);

  useEffect(() => {
    switch (true) {
      case base:
        dispatch({ type: 'ui/screenSize', payload: 'base' });
        break;
      case mobile:
        dispatch({ type: 'ui/screenSize', payload: 'mobile' });
        break;
      case xs:
        dispatch({ type: 'ui/screenSize', payload: 'xs' });
        break;
      case sm:
        dispatch({ type: 'ui/screenSize', payload: 'sm' });
        break;
      case md:
        dispatch({ type: 'ui/screenSize', payload: 'md' });
        break;
      case lg:
        dispatch({ type: 'ui/screenSize', payload: 'lg' });
        break;
      case xl:
        dispatch({ type: 'ui/screenSize', payload: 'xl' });
        break;
    }
  }, [base, mobile, xs, sm, md, lg, xl, dispatch]);
}

/**
 * Returns which breakpoints the current screen size is less than.
 */
export function useScreenSize() {
  const sizes = {
    isLessThanMobile: undefined,
    isLessThanXs: undefined,
    isLessThanSm: undefined,
    isLessThanMd: undefined,
    isLessThanLg: undefined,
    isLessThanXl: undefined,
  };
  const appData = useAppData();

  switch (appData?.screenSize) {
    case 'base':
      return {
        isLessThanMobile: true,
        isLessThanXs: true,
        isLessThanSm: true,
        isLessThanMd: true,
        isLessThanLg: true,
        isLessThanXl: true,
      };
    case 'mobile':
      return {
        isLessThanMobile: false,
        isLessThanXs: true,
        isLessThanSm: true,
        isLessThanMd: true,
        isLessThanLg: true,
        isLessThanXl: true,
      };
    case 'xs':
      return {
        isLessThanMobile: false,
        isLessThanXs: false,
        isLessThanSm: true,
        isLessThanMd: true,
        isLessThanLg: true,
        isLessThanXl: true,
      };
    case 'sm':
      return {
        isLessThanMobile: false,
        isLessThanXs: false,
        isLessThanSm: false,
        isLessThanMd: true,
        isLessThanLg: true,
        isLessThanXl: true,
      };
    case 'md':
      return {
        isLessThanMobile: false,
        isLessThanXs: false,
        isLessThanSm: false,
        isLessThanMd: false,
        isLessThanLg: true,
        isLessThanXl: true,
      };
    case 'lg':
      return {
        isLessThanMobile: false,
        isLessThanXs: false,
        isLessThanSm: false,
        isLessThanMd: false,
        isLessThanLg: false,
        isLessThanXl: true,
      };
    case 'xl':
      return {
        isLessThanMobile: false,
        isLessThanXs: false,
        isLessThanSm: false,
        isLessThanMd: false,
        isLessThanLg: false,
        isLessThanXl: false,
      };
    default:
      return sizes;
  }
}
