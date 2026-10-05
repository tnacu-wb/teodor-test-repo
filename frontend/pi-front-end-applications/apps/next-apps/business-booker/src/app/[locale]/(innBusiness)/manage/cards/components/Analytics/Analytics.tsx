'use client';

import { CARD_STATUS_WL_TYPE, CARD_STATUS_TYPE, CardReplaceAddress } from '@whitbread-eos/api';
import { analytics } from '@whitbread-eos/utils';
import { useEffect } from 'react';

const isCDHCardExpired = (expiryDate: string) => {
  const month = parseInt(expiryDate.slice(0, 2), 10);
  const year = parseInt(expiryDate.slice(-2), 10) + 2000;
  const now = new Date();
  return now.getFullYear() > year || (now.getFullYear() === year && now.getMonth() + 1 > month);
};

const getInnPayCardType = (status: string, isActivated: boolean) => {
  if (status === CARD_STATUS_WL_TYPE.HOT) {
    return CARD_STATUS_TYPE.ACTIVE;
  }

  if (status === CARD_STATUS_WL_TYPE.CURRENT) {
    return isActivated ? CARD_STATUS_TYPE.ACTIVE : CARD_STATUS_TYPE.ACTIVATE;
  }

  if (status === CARD_STATUS_WL_TYPE.PENDING) {
    return CARD_STATUS_TYPE.DISPATCHING;
  }

  return CARD_STATUS_TYPE.CANCELLED;
};

type Props = {
  pageName?: string;
  tab?: string;
  rowsData?: any;
  pageIndex?: number;
  track?: string;
  cardUpdateId?: string;
  cardType?: string;
  totalPages?: number;
  isCancelledCardsFilterOn?: boolean;
  selectedSetCreditLimit?: boolean;
  selectedRestrictedUsage?: boolean;
  cardReplaceReason?: string;
  cardReplaceAddress?: CardReplaceAddress | null;
  validation?: string;
};

export function Analytics({
  pageName,
  tab,
  rowsData,
  pageIndex,
  track,
  cardUpdateId = '',
  cardType = '',
  totalPages = NaN,
  isCancelledCardsFilterOn,
  selectedSetCreditLimit,
  selectedRestrictedUsage,
  cardReplaceReason,
  cardReplaceAddress,
  validation,
}: Props) {
  const resetCardAnalytics = (data: any = {}) => {
    analytics.update({
      innBusiness: {
        ...data,
        activeCards: 0,
        expiredCards: 0,
        dispatchedCards: 0,
        activateCards: 0,
        cancelledCards: 0,
        visitedCardsPages: new Set(),
      },
    });
  };

  useEffect(() => {
    if (pageName === undefined) {
      return;
    }
    const currentValidation = window?.analyticsData?.validation ?? '';

    const currentData = window?.analyticsData ?? {};
    analytics.update({
      ...currentData,
      pageName: pageName,
    });

    if (currentValidation) {
      analytics.remove(['validation']);
    }
  }, [pageName]);

  useEffect(() => {
    if (tab === undefined) {
      return;
    }

    resetCardAnalytics({ tab: tab });
  }, [tab]);

  useEffect(() => {
    if (totalPages === pageIndex && totalPages === 1) {
      const innBusinessData = window?.analyticsData?.innBusiness ?? {};
      resetCardAnalytics(innBusinessData);
      return;
    }
  }, [totalPages, pageIndex]);

  useEffect(() => {
    const currentData = window?.analyticsData ?? {};
    const innBusinessData = window?.analyticsData?.innBusiness ?? {};
    const visitedPages = innBusinessData?.visitedCardsPages;

    if (!visitedPages?.size) {
      analytics.update({
        ...currentData,
        innBusiness: {
          ...innBusinessData,
          activeCards: 0,
          expiredCards: 0,
          dispatchedCards: 0,
          activateCards: 0,
          cancelledCards: 0,
          visitedCardsPages: new Set(),
        },
      });
    }

    if (!pageIndex || visitedPages?.has(pageIndex)) {
      return;
    }
    visitedPages?.add(pageIndex);

    if (rowsData?.some((card: Record<string, any>) => 'expiryDate' in card)) {
      //if it has expiryDate, then the cards are CDH
      const expiredCount = rowsData.filter((card: Record<string, any>) =>
        isCDHCardExpired(card.expiryDate)
      ).length;
      const activeCount = rowsData.length - expiredCount;

      analytics.update({
        ...currentData,
        innBusiness: {
          ...innBusinessData,
          activeCards: (innBusinessData?.activeCards ?? 0) + activeCount,
          expiredCards: (innBusinessData?.expiredCards ?? 0) + expiredCount,
        },
      });
    } else {
      const innPayCardsStatusCounts = rowsData?.reduce(
        (acc: Record<string, number>, card: Record<string, any>) => {
          const cardType = getInnPayCardType(card.cardStatus, card.isActivated);
          acc[cardType] = (acc[cardType] ?? 0) + 1;
          return acc;
        },
        {}
      );

      analytics.update({
        ...currentData,
        innBusiness: {
          ...innBusinessData,
          activeCards:
            (innBusinessData?.activeCards ?? 0) +
            (innPayCardsStatusCounts?.[CARD_STATUS_TYPE.ACTIVE] ?? 0),
          dispatchedCards:
            (innBusinessData?.dispatchedCards ?? 0) +
            (innPayCardsStatusCounts?.[CARD_STATUS_TYPE.DISPATCHING] ?? 0),
          cancelledCards:
            (innBusinessData?.cancelledCards ?? 0) +
            (innPayCardsStatusCounts?.[CARD_STATUS_TYPE.CANCELLED] ?? 0),
          activateCards:
            (innBusinessData?.activateCards ?? 0) +
            (innPayCardsStatusCounts?.[CARD_STATUS_TYPE.ACTIVATE] ?? 0),
        },
      });
    }
  }, [rowsData, pageIndex]);

  useEffect(() => {
    if (!cardUpdateId) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    const innBusinessData = window?.analyticsData?.innBusiness ?? {};

    analytics.update({
      ...currentData,
      innBusiness: {
        ...innBusinessData,
        cardId: cardUpdateId,
      },
    });
  }, [cardUpdateId]);

  useEffect(() => {
    if (!cardType) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    const innBusinessData = window?.analyticsData?.innBusiness ?? {};

    analytics.update({
      ...currentData,
      innBusiness: {
        ...innBusinessData,
        cardType: cardType,
      },
    });
  }, [cardType]);

  useEffect(() => {
    if (typeof isCancelledCardsFilterOn === 'undefined') {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    const innBusinessData = window?.analyticsData?.innBusiness ?? {};

    if (isCancelledCardsFilterOn === false) {
      delete window?.analyticsData?.innBusiness?.showCancelledCards;
    } else {
      analytics.update({
        ...currentData,
        innBusiness: {
          ...innBusinessData,
          showCancelledCards: true,
        },
      });
    }
  }, [isCancelledCardsFilterOn]);

  useEffect(() => {
    if (typeof selectedSetCreditLimit === 'undefined') {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    const innBusinessData = window?.analyticsData?.innBusiness ?? {};

    if (selectedSetCreditLimit === false) {
      delete window?.analyticsData?.innBusiness?.selectedSetCreditLimit;
    } else {
      analytics.update({
        ...currentData,
        innBusiness: {
          ...innBusinessData,
          selectedSetCreditLimit: true,
        },
      });
    }
  }, [selectedSetCreditLimit]);

  useEffect(() => {
    if (typeof selectedRestrictedUsage === 'undefined') {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    const innBusinessData = window?.analyticsData?.innBusiness ?? {};

    if (selectedRestrictedUsage === false) {
      delete window?.analyticsData?.innBusiness?.selectedRestrictedUsage;
    } else {
      analytics.update({
        ...currentData,
        innBusiness: {
          ...innBusinessData,
          selectedRestrictedUsage: true,
        },
      });
    }
  }, [selectedRestrictedUsage]);

  useEffect(() => {
    if (!cardReplaceReason) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    const innBusinessData = window?.analyticsData?.innBusiness ?? {};

    analytics.update({
      ...currentData,
      innBusiness: {
        ...innBusinessData,
        cardReplaceReason,
      },
    });
  }, [cardReplaceReason]);

  useEffect(() => {
    if (!cardReplaceAddress) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    const innBusinessData = window?.analyticsData?.innBusiness ?? {};

    analytics.update({
      ...currentData,
      innBusiness: {
        ...innBusinessData,
        cardReplaceAddress,
      },
    });
  }, [cardReplaceAddress]);

  useEffect(() => {
    if (typeof validation === 'undefined') {
      return;
    }
    if (validation === '') {
      analytics.remove(['validation']);
      return;
    }
    const currentData = window?.analyticsData ?? {};

    analytics.update({
      ...currentData,
      validation,
    });
  }, [validation]);

  useEffect(() => {
    if (track) {
      window?._satellite?.track(track);
    }
  }, [track]);

  return null;
}
