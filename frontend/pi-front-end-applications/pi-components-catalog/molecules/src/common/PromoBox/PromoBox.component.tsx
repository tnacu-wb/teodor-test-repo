import { Box, Flex, Collapse, Link, FlexProps } from '@chakra-ui/react';
import { useQueryClient, type QueryClient } from '@tanstack/react-query';
import {
  Channel,
  FT_PI_SHOW_PROMOTION_BOX,
  FT_CCUI_SHOW_PROMOTION_BOX,
  FT_BB_SHOW_PROMOTION_BOX,
  type HIRoomRate,
  HotelBrand,
  PromoKind,
  BASKET_DETAILS_STORAGE_KEY,
  FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import {
  ChevronDown24,
  ChevronUp24,
  Input,
  Button,
  Notification,
  Error,
  Success,
} from '@whitbread-eos/atoms';
import {
  useFeatureToggle,
  formatDataTestId,
  type PromoActionsType,
  getGQLClient,
  hasMatchingPromotionCode,
  PromotionsInformation,
  analytics,
  getSelectedRoomClassCode,
  useCustomLocale,
  useSemanticTypography,
  setCookie,
  getCookie,
  deleteCookie,
  getPromoId,
} from '@whitbread-eos/utils';
import { GraphQLClient } from 'graphql-request';
import { useRouter } from 'next/router';
import React, { useState, useEffect, useRef, type MutableRefObject, useMemo } from 'react';
import { useForm } from 'react-hook-form';

import { getPromotionsInformation } from '../../amend/utilities';

type PromoStateType = React.Dispatch<React.SetStateAction<PromoState>>;
type PromoStateString = React.Dispatch<React.SetStateAction<string>>;
type PromoStateBoolean = React.Dispatch<React.SetStateAction<boolean>>;

export type PromoState = {
  code: string | undefined;
  error: string;
  success: string;
  isApplied: boolean;
  isOpen: boolean;
  shouldShowRemoveButton: boolean;
  type?: PromoKind;
  rateName?: string;
  roomClass?: string;
  isPromoBox?: boolean;
};

export type SearchQueryType = {
  arrival: string;
  departure: string;
  country: string;
  language: string;
  hotelBrand: HotelBrand;
  roomRates: HIRoomRate[];
};

type MetaSearchConfig = {
  rate: string;
  code: string;
};

type Props = {
  channel: Channel;
  promoActions: PromoActionsType;
  metaSearchConfigs: MetaSearchConfig[];
};

const PROMOCODE_COOKIE = 'appliedPromoBoxCode';

export const getPromoMessage = (
  promoInfo: PromotionsInformation | undefined,
  status: string
): string => {
  if (promoInfo?.errorRateAndRoomMessage) {
    return promoInfo?.errorRateAndRoomMessage;
  }
  const promoBox = promoInfo?.promoBox;
  if (!promoBox) return '';
  switch (status) {
    case 'MIN_ROOMS_NOT_MET':
      return promoBox?.whenMinRoomsNotMet;
    case 'MAX_ROOMS_EXCEEDED':
      return promoBox?.whenMaxRoomsExceeded;
    case 'EMPTY':
      return promoBox?.whenEmpty;
    case 'INVALID':
      return promoBox?.whenInvalid;
    case 'CODE_ALREADY_APPLIED':
      return promoBox?.whenCodeAlreadyApplied ?? '';
    case 'CODE_EXPIRED':
      return promoBox?.whenCodeExpired ?? '';
    case 'UNAVAILABLE':
      return promoBox?.whenUnavailable ?? '';
    case 'SUCCESS':
      return promoBox?.whenSuccess;
    default:
      return '';
  }
};

export const handlePromoSubmit = (
  setPromoState: PromoStateType,
  code: string,
  error: string,
  success: string,
  isApplied: boolean,
  shouldShowRemoveButton: boolean,
  type?: PromoKind,
  rateName?: string,
  roomClass?: string,
  isPromoBox?: boolean
) => {
  setPromoState?.({
    code,
    error,
    success,
    isApplied,
    isOpen: true,
    shouldShowRemoveButton,
    type,
    rateName,
    roomClass,
    isPromoBox,
  });
};

export const handlePromoCodeChange = (
  value: string,
  setPromoInput: PromoStateString,
  setError: PromoStateString,
  setSuccess: PromoStateString,
  promotionBannerData: PromotionsInformation,
  appliedPromoCode: MutableRefObject<string>
) => {
  const sanitized = value.trim();
  const isValid = /^[a-zA-Z0-9-_]*$/.test(sanitized);

  if (appliedPromoCode) appliedPromoCode.current = sanitized;

  setPromoInput(sanitized);
  setError(isValid ? '' : getPromoMessage(promotionBannerData, 'INVALID'));
  setSuccess('');
};

export const handlePromoCodeChangeCookie = (
  promotionBoxCodeCookie: string,
  setPromoInput: PromoStateString,
  setError: PromoStateString,
  setSuccess: PromoStateString,
  promotionBannerData: PromotionsInformation,
  appliedPromoCode: MutableRefObject<string>,
  setIsOpen: PromoStateBoolean
) => {
  const sanitized = promotionBoxCodeCookie.trim();
  const isValid = /^[a-zA-Z0-9-_]*$/.test(sanitized);
  if (appliedPromoCode) appliedPromoCode.current = sanitized;

  setPromoInput(sanitized);
  setIsOpen(true);

  if (isValid && promotionBannerData?.promoBoxStatus === 'SUCCESS') {
    setSuccess(getPromoMessage(promotionBannerData, 'SUCCESS'));
    setError('');
  } else if (
    promotionBannerData?.promoBoxStatus &&
    promotionBannerData?.promoBoxStatus !== 'SUCCESS'
  ) {
    setError(getPromoMessage(promotionBannerData, promotionBannerData?.promoBoxStatus));
    setSuccess('');
  }
};

export const getInputStyles = (errorMsg: string, successMsg: string) => {
  let borderColor = '';
  if (errorMsg) borderColor = 'error';
  else if (successMsg) borderColor = 'success';
  return {
    ...styles.input,
    inputElementStyles: { ...styles.input.inputElementStyles, borderColor },
  };
};

type SubmitPromoPropsType = {
  promoInput: string;
  searchQuery: SearchQueryType;
  channel: Channel;
  queryClient: QueryClient;
  client: GraphQLClient;
  setPromoState: PromoStateType;
  setReRender: PromoStateBoolean;
  setSuccess: PromoStateString;
  setError: PromoStateString;
  language?: string;
  metaSearchConfigs?: MetaSearchConfig[];
  setErrorRateRoom: PromoStateString;
  isPromotionsInHotelAvailabilityEnabled: boolean;
};

export const getMappedRateName = (rateName: string, configs: MetaSearchConfig[]): string => {
  if (!rateName) return '';

  const exactMatch = configs.find((item) => item.rate === rateName);
  if (exactMatch) {
    return rateName;
  }

  const startsWithMatch = configs.find((item) => rateName.startsWith(item.code));

  if (startsWithMatch) {
    return startsWithMatch.rate;
  }
  return rateName;
};

export const onSubmitPromo = async ({
  promoInput,
  searchQuery,
  channel,
  queryClient,
  client,
  setPromoState,
  setReRender,
  setSuccess,
  setError,
  language,
  metaSearchConfigs,
  setErrorRateRoom,
  isPromotionsInHotelAvailabilityEnabled,
}: SubmitPromoPropsType) => {
  const brand = searchQuery?.hotelBrand?.toUpperCase() ?? '';

  const basketDetailsState = getLatestBasket();

  const rateName = basketDetailsState?.selectedRate?.ratePlanCode || '';

  const mappedRateName = getMappedRateName(rateName, metaSearchConfigs ?? []);
  const roomClass = getSelectedRoomClassCode(basketDetailsState?.roomClass, language as string);

  if (isPromotionsInHotelAvailabilityEnabled) {
    handlePromoSubmit(
      setPromoState,
      promoInput,
      '',
      '',
      true,
      true,
      undefined,
      mappedRateName,
      roomClass,
      true
    );

    setError('');
    setErrorRateRoom('');
    setSuccess('');

    setReRender((prev) => !prev);
    return;
  }

  const promoInfo = await getPromotionsInformation(
    searchQuery?.arrival,
    searchQuery?.departure,
    searchQuery?.country,
    searchQuery?.language,
    brand,
    channel,
    '',
    queryClient,
    client,
    true,
    promoInput,
    true,
    mappedRateName,
    roomClass
  );

  if (!promoInfo) {
    setSuccess('');
    setError(
      getPromoMessage(undefined, 'UNAVAILABLE') || 'Something went wrong. Please try again.'
    );
    return;
  }

  const status = promoInfo?.promoBoxStatus ?? '';
  const showPromo = promoInfo?.showPromo ?? '';
  const isWithinPromoWindow = promoInfo?.isWithinPromoWindow ?? '';
  let message = getPromoMessage(promoInfo, status);
  const errorRateAndRoomMessage = promoInfo?.errorRateAndRoomMessage ?? '';
  const noApplicableErrorMessage = promoInfo?.promoInvalidMessage;
  if (errorRateAndRoomMessage && showPromo === false) {
    message = errorRateAndRoomMessage;
  }

  let shouldShowRemoveButton = false;
  let code = promoInput;
  const isApplied = true;

  if (status === 'SUCCESS' && showPromo === true && isWithinPromoWindow === true) {
    shouldShowRemoveButton = true;
    code = promoInfo?.promotionCode ?? '';
    setError('');
    setErrorRateRoom('');
    setSuccess(message);
    handlePromoSubmit(
      setPromoState,
      code,
      '',
      message,
      isApplied,
      shouldShowRemoveButton,
      promoInfo?.promoKind
    );
  } else {
    if (!message) {
      message = noApplicableErrorMessage ?? '';
    }
    setError(message);
    setErrorRateRoom(errorRateAndRoomMessage);
  }
  setReRender((prev) => !prev);
};

export function getLatestBasket() {
  const latestBasket =
    typeof window !== 'undefined'
      ? JSON.parse(localStorage.getItem(BASKET_DETAILS_STORAGE_KEY) || '{}')
      : {};

  return latestBasket;
}

export default function PromoBox({ channel, promoActions, metaSearchConfigs }: Readonly<Props>) {
  const router = useRouter();
  const { language } = useCustomLocale();
  const getTypographyProps = useSemanticTypography();
  const {
    promotionBannerData,
    searchQuery,
    setPromoState,
    promoState,
    appliedPromoCode,
    isFetching,
  } = promoActions ?? {};

  const {
    [FT_PI_SHOW_PROMOTION_BOX]: isShowPromoBoxPIEnabled,
    [FT_CCUI_SHOW_PROMOTION_BOX]: isShowPromoBoxCCUIEnabled,
    [FT_BB_SHOW_PROMOTION_BOX]: isShowPromoBoxBBEnabled,
    [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: isPromoBoxAppliedCodeCookieEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = useFeatureToggle();

  const dataTestId = 'promoBox';
  const promoBoxTitle = promotionBannerData?.promoBox?.title ?? '';
  const promoBoxButton = promotionBannerData?.promoBox?.button ?? '';

  const queryClient = useQueryClient();
  const client = getGQLClient();

  const [promoInput, setPromoInput] = useState('');
  const [errorRateRoom, setErrorRateRoom] = useState('');
  const [isOpen, setIsOpen] = useState(promoState?.isOpen ?? false);
  const [error, setError] = useState(promoState?.error ?? '');
  const [success, setSuccess] = useState(promoState?.success ?? '');
  const [, setReRender] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);
  const isInitialMount = useRef(true);
  const isReady = router.isReady;
  const { PROMOID, CORPID } = router.query;
  const promoIdFromQuery = getPromoId(PROMOID);
  const prevRateRef = useRef<string | undefined>(undefined);
  const prevRoomRef = useRef<string | undefined>(undefined);

  const basketDetailsState = getLatestBasket();
  const rateName = promoState?.isApplied
    ? basketDetailsState?.selectedRate?.ratePlanCode || ''
    : '';
  const rateNameCode = basketDetailsState?.selectedRate?.ratePlanCode;
  const roomClass = getSelectedRoomClassCode(basketDetailsState?.roomClass, language);
  const isRateTagsExist =
    !!basketDetailsState?.rateTags && basketDetailsState?.rateTags?.length > 0;
  const analyticsTrackedReference = useRef(false);
  const promotionBoxCodeCookie = getCookie(PROMOCODE_COOKIE);
  const isFirstRateRoomChangeRef = useRef(true);

  useEffect(() => {
    if (!promotionBoxCodeCookie) return;
    if (promoIdFromQuery && typeof promoIdFromQuery === 'string' && promoIdFromQuery?.trim()) {
      deleteCookie?.(PROMOCODE_COOKIE);
    }
  }, []);

  useEffect(() => {
    const promotionBoxCodeCookie = getCookie?.(PROMOCODE_COOKIE);
    if (!promotionBoxCodeCookie) return;

    const isRateTagsExist =
      !!basketDetailsState?.rateTags && basketDetailsState?.rateTags?.length > 0;
    if (isRateTagsExist)
      setPromoState?.((prev) => ({
        ...prev,
        code: promotionBoxCodeCookie,
        isApplied: true,
        isOpen: true,
        shouldShowRemoveButton: true,
      }));

    setPromoInput?.(promotionBoxCodeCookie);
    if (isRateTagsExist) {
      setSuccess?.(getPromoMessage?.(promotionBannerData, 'SUCCESS'));
    }
    setIsOpen?.(true);
  }, []);

  useEffect(() => {
    const currentData = window?.analyticsData?.promo ?? {};
    if (promoInput && (error || success)) {
      analytics.update({
        promo: {
          ...currentData,
          promoCode: promoInput,
          promoName: promoState?.type ?? promotionBannerData?.promoKind,
        },
        validation: error ? error : success,
      });
    }
    if (promoInput && error) {
      analytics.track('error_event', {
        error_message: error,
        error_code: error,
        promoCode: promoInput,
        promoInputType: 'pasted',
        journey_name: 'Promotion Box',
      });
    }
    if (success && isPromoBoxAppliedCodeCookieEnabled && hasValidPromotion) {
      setCookie(PROMOCODE_COOKIE, promoInput, undefined);
    }
  }, [promoInput, error, success, isLoading]);

  useEffect(() => {
    if (!isPromotionsInHotelAvailabilityEnabled) return;

    const promoInfo = promotionBannerData;

    if (!promoState?.isApplied) return;

    if (promoInfo?.errorRateAndRoomMessage) {
      setError(promoInfo.errorRateAndRoomMessage);
      setSuccess('');
      setErrorRateRoom(promoInfo.errorRateAndRoomMessage);
      return;
    }

    if (
      promoInfo?.promoBoxStatus === 'SUCCESS' &&
      promoInfo?.showPromo &&
      promoInfo?.isWithinPromoWindow
    ) {
      setError('');
      setErrorRateRoom('');
      setSuccess(getPromoMessage(promoInfo, 'SUCCESS'));
    }
  }, [promotionBannerData, promoState?.isApplied, isPromotionsInHotelAvailabilityEnabled]);

  useEffect(() => {
    if (!promotionBoxCodeCookie) return;
    if (promotionBannerData?.promoBoxStatus && promotionBoxCodeCookie && !promoIdFromQuery) {
      handlePromoCodeChangeCookie(
        promotionBoxCodeCookie,
        setPromoInput,
        setError,
        setSuccess,
        promotionBannerData,
        appliedPromoCode,
        setIsOpen
      );
    }
  }, [promotionBannerData, promotionBoxCodeCookie]);

  useEffect(() => {
    if (!isReady) return;

    if (isInitialMount.current) {
      isInitialMount.current = false;
      const isRateTagsExist =
        !!basketDetailsState?.rateTags && basketDetailsState?.rateTags?.length > 0;

      if (typeof promoIdFromQuery === 'string' && promoIdFromQuery.trim() && isRateTagsExist) {
        setPromoState?.((prev) => ({
          ...prev,
          code: promoIdFromQuery.trim(),
        }));
      }
      return;
    }
  }, [isReady, promoIdFromQuery, setPromoState]);

  useEffect(() => {
    if (isOpen) {
      analytics.track('promoBoxExpand');
    }
    if (isOpen && inputRef.current) {
      inputRef.current.focus();
      analytics.track('promoBoxClick');
    }
  }, [isOpen]);

  const { handleSubmit } = useForm<{ promocode: string }>({
    defaultValues: { promocode: promoState?.code ?? '' },
  });

  const onSubmit = async () => {
    setIsLoading(true);

    try {
      await onSubmitPromo({
        promoInput,
        searchQuery,
        channel,
        queryClient,
        client,
        setPromoState: setPromoState as PromoStateType,
        setReRender,
        setSuccess,
        setError,
        language,
        metaSearchConfigs,
        setErrorRateRoom,
        isPromotionsInHotelAvailabilityEnabled,
      });
    } finally {
      setIsLoading(false);
    }
  };
  useEffect(() => {
    const prevRate = prevRateRef.current;
    const prevRoom = prevRoomRef.current;

    const isRateChanged = prevRate !== undefined && prevRate !== rateNameCode;

    const isRoomChanged = prevRoom !== undefined && prevRoom !== roomClass;

    if (isRateChanged || isRoomChanged) {
      if (!isFirstRateRoomChangeRef.current) {
        setError('');
      }
      isFirstRateRoomChangeRef.current = false;
    }

    if (promoInput && errorRateRoom && !isRateTagsExist && (isRateChanged || isRoomChanged)) {
      onSubmit();
    }
    prevRateRef.current = rateNameCode;
    prevRoomRef.current = roomClass;
  }, [rateNameCode, roomClass]);

  useEffect(() => {
    if (!promoState?.isApplied || !promoState?.code || isFetching) return;

    setPromoInput(promoState?.code);
    const valid = hasMatchingPromotionCode(promoState?.code, searchQuery?.roomRates);
    if (valid && isRateTagsExist) {
      setSuccess(getPromoMessage(promotionBannerData, 'SUCCESS'));
      setError('');
    } else if (promotionBannerData?.promoBoxStatus !== 'SUCCESS') {
      setError(getPromoMessage(promotionBannerData, promotionBannerData?.promoBoxStatus as string));
      setSuccess('');
    } else {
      setError('');
      setSuccess('');
    }
    if (valid && isRateTagsExist && success) {
      setPromoInput(promoState?.code);
    }
  }, [
    promoState?.isApplied,
    promoState?.code,
    rateNameCode,
    roomClass,
    isRateTagsExist,
    isFetching,
  ]);

  useEffect(() => {
    if (!promoState?.isApplied) {
      analyticsTrackedReference.current = false;
      return;
    }
    const valid = hasMatchingPromotionCode(promoState?.code, searchQuery?.roomRates);
    if (
      valid &&
      promoState?.code &&
      basketDetailsState?.rateTags?.length > 0 &&
      !analyticsTrackedReference.current
    ) {
      analyticsTrackedReference.current = true;
      analytics.track('discountCodeApplied', {
        promoCode: promoState?.code,
        promoName: rateName ?? promotionBannerData?.promoKind,
        promoType: promoState?.type,
        promoInputType: 'pasted',
        discountTags: basketDetailsState?.rateTags,
        journey_name: 'Promotion Box',
      });
    }
  }, [promoState?.isApplied, promoState?.code, basketDetailsState?.rateTags]);

  const shouldDisplayPromoBox = useMemo(() => {
    switch (channel) {
      case Channel.Pi:
        return isShowPromoBoxPIEnabled;
      case Channel.Ccui:
        return isShowPromoBoxCCUIEnabled;
      case Channel.Bb:
        return isShowPromoBoxBBEnabled;
      default:
        return false;
    }
  }, [channel, isShowPromoBoxPIEnabled, isShowPromoBoxCCUIEnabled, isShowPromoBoxBBEnabled]);

  const hasValidPromotion = Boolean(isRateTagsExist);

  const isSiteWidePromo =
    promotionBannerData?.promoKind === PromoKind.SiteWide && hasValidPromotion && !promoIdFromQuery;

  const isLandingPagePromo =
    promotionBannerData?.promoKind === PromoKind.LandingPage &&
    hasValidPromotion &&
    promoIdFromQuery;

  const isInputDisabled = isLoading || isFetching || (Boolean(success) && hasValidPromotion);
  const isButtonDisabled = isInputDisabled || !promoInput;

  if (
    !shouldDisplayPromoBox ||
    !promotionBannerData ||
    isLandingPagePromo ||
    CORPID ||
    isSiteWidePromo
  ) {
    return null;
  }

  return (
    <Box sx={styles.container} data-testid={formatDataTestId(dataTestId, 'container')}>
      <Flex
        sx={styles.header}
        data-testid={`${dataTestId}-header`}
        onClick={() => setIsOpen(!isOpen)}
      >
        <Link
          {...styles.isBreakdownVisibleLayoutStyles}
          {...getTypographyProps(
            styles.isBreakdownVisibleLegacyTypography,
            promoBoxTitleSemanticTypography
          )}
          style={{ color: PROMO_TEXT_COLOR }}
          flex={{ base: 'none', lg: '1' }}
          data-testid={formatDataTestId(dataTestId, 'title')}
        >
          {promoBoxTitle}
        </Link>
        {isOpen ? (
          <ChevronUp24 data-testid={formatDataTestId(dataTestId, 'chevronUp')} />
        ) : (
          <ChevronDown24 data-testid={formatDataTestId(dataTestId, 'chevronDown')} />
        )}
      </Flex>

      <Collapse in={isOpen} data-testid={formatDataTestId(dataTestId, 'collapse')}>
        <Box
          as="form"
          onSubmit={handleSubmit(onSubmit)}
          data-testid={formatDataTestId(dataTestId, 'content')}
        >
          <Flex sx={styles.collapseContent}>
            <Box
              sx={styles.inputWrapper}
              data-testid={formatDataTestId(dataTestId, 'inputWrapper')}
            >
              <Input
                inputRef={inputRef}
                isDisabled={isInputDisabled}
                type="text"
                name="promocode"
                placeholderText=""
                value={promoInput}
                onChange={(value) => {
                  const upperValue = value?.toUpperCase();
                  handlePromoCodeChange(
                    upperValue,
                    setPromoInput,
                    setError,
                    setSuccess,
                    promotionBannerData,
                    appliedPromoCode
                  );
                }}
                styles={getInputStyles(error, isRateTagsExist ? success : '')}
                data-testid={formatDataTestId(dataTestId, 'input')}
              />
            </Box>
            <Box
              sx={styles.buttonWrapper}
              data-testid={formatDataTestId(dataTestId, 'buttonWrapper')}
            >
              <Button
                isDisabled={isButtonDisabled}
                variant="tertiary"
                size="sm"
                style={{ ...styles.button, color: PROMO_TEXT_COLOR }}
                type="submit"
                isLoading={isLoading || isFetching}
                data-testid={formatDataTestId(dataTestId, 'button')}
              >
                <span>{promoBoxButton}</span>
              </Button>
            </Box>
          </Flex>

          <Collapse
            in={!!error || !!success}
            data-testid={formatDataTestId(dataTestId, 'messageCollapse')}
          >
            {error && !isFetching && (
              <Notification
                svg={<Error style={{ marginTop: '1px' }} />}
                status="error"
                variant="error"
                prefixDataTestId={formatDataTestId(dataTestId, 'error')}
                description={`<span style="color:#D73D00">${error}</span>`}
                isInnerHTML
                wrapperStyles={styles.notificationBoxStyles as FlexProps}
              />
            )}
            {success && isRateTagsExist && !isFetching && (
              <Notification
                svg={<Success style={{ marginTop: '1px' }} />}
                status="success"
                variant="success"
                description={`<span style="color:#1c8754">${success}</span>`}
                isInnerHTML
                wrapperStyles={styles.notificationBoxStyles as FlexProps}
              />
            )}
          </Collapse>
        </Box>
      </Collapse>
    </Box>
  );
}

const PROMO_TEXT_COLOR = '#511E62';

const promoBoxTitleSemanticTypography = {
  textStyle: 'link-m-regular',
} as const;

const styles = {
  header: {
    align: 'center',
    justify: 'space-between',
    cursor: 'pointer',
    height: '1.25rem',
    userSelect: 'none',
  },
  container: {
    mw: 'full',
    pt: 0,
    border: 0,
    position: 'relative',
    overflow: 'hidden',
    mt: { base: 0, lg: '1rem' },
  },
  collapseContent: {
    justifyContent: 'space-between',
    alignItems: 'center',
    gap: '0.5rem',
    mt: '1rem',
    p: 0,
    w: '100%',
  },
  inputWrapper: { flex: '1 1 auto' },
  input: {
    inputElementStyles: {
      color: PROMO_TEXT_COLOR,
      height: '2.75rem',
      width: '100%',
      borderRadius: '0.25rem',
      _focus: { boxShadow: '0 2px 12px var(--chakra-colors-lightGrey2)' },
    },
  },
  buttonWrapper: { flex: '0 0 5.625rem' },
  button: { height: '2.75rem', width: '100%' },
  isBreakdownVisibleLayoutStyles: {
    mr: 'sm',
    textDecoration: 'underline',
    color: 'btnSecondaryEnabled',
    cursor: 'pointer',
  },
  isBreakdownVisibleLegacyTypography: {
    fontSize: 'sm',
  },
  notificationBoxStyles: {
    border: 0,
    backgroundColor: 'none',
    width: 'auto',
    flexWrap: 'nowrap',
    color: 'tertiary',
    fontSize: 'sm',
    fontWeight: 'sm',
    lineHeight: '1.225rem',
    p: 0,
    px: '0.3rem',
    mt: '0.6rem',
  },
};
