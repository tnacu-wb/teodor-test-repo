import type { PromotionOffer } from '@whitbread-eos/api';
import { useRouter } from 'next/router';
import { useMemo } from 'react';

export interface Promotion {
  numberOfNights: number;
  maxRooms: number;
  maxRoomsAmend: number;
  promoCode: string;
  page: string;
  enabled: boolean;
}

// Union type combining all properties from PromotionOffer and Promotion
export type UnifiedOffer = PromotionOffer & {
  // Promotion-specific properties (optional when from offer)
  promoCode?: string;
  maxRoomsAmend?: number;
  enabled?: boolean;
};

export interface OfferDetectionResult {
  cellCodes: string | null;
  corpId: string | null;
  promoId: string | null;
  // Unified matchedOffer - picks first match from: offer > promotion (only if enabled)
  matchedOffer: UnifiedOffer | null;
  hasActiveMatch: boolean;
}

/**
 * Custom hook that detects offers and promotions based on the current page path
 * Replicates the logic from pi-header HeaderModel.findOffer()
 *
 * @param offers - Array of promotional offers from translations/config
 * @param promotions - Array of promotions from translations/config
 * @returns Object containing cellCodes, corpId, promoId, matched offer, and matched promotion
 */
export function useOfferDetection(
  offers: PromotionOffer[] = [],
  promotions: Promotion[] = []
): OfferDetectionResult {
  const router = useRouter();

  return useMemo(() => {
    // Get URL query params for offers and promotions
    const { CELLCODES, CORPID, PROMOID } = router.query;
    const cellCodesFromUrl = CELLCODES as string | undefined;
    const corpIdFromUrl = CORPID as string | undefined;
    const promoIdFromUrl = PROMOID as string | undefined;

    // Get current page path (similar to History.currentPage() in pi-header)
    const currentPage = getCurrentPagePath(router.asPath);

    // Find offer matching the current page or cellCode/corpId
    const foundOffer = findOfferForPage(offers, currentPage, cellCodesFromUrl, corpIdFromUrl);

    // Find promotion matching the current page or promoCode
    const foundPromotion = findPromotionForPage(promotions, currentPage, promoIdFromUrl);

    // Convert promotion to UnifiedOffer format (includes all promotion-specific properties)
    const promotionAsUnifiedOffer: UnifiedOffer | null = foundPromotion
      ? {
          // PromotionOffer base properties
          maxRooms: foundPromotion.maxRooms,
          numberOfNights: foundPromotion.numberOfNights,
          cellCode: '',
          corpId: '',
          page: foundPromotion.page,
          ratePlanCode: '',
          // Promotion-specific properties
          promoCode: foundPromotion.promoCode,
          maxRoomsAmend: foundPromotion.maxRoomsAmend,
          enabled: foundPromotion.enabled,
        }
      : null;

    const isActiveOffer = isOfferActive(foundOffer);
    const isActivePromotion = isPromotionActive(foundPromotion) && foundPromotion?.enabled === true;

    const matchedOffer: UnifiedOffer | null = isActiveOffer
      ? foundOffer
      : isActivePromotion
        ? promotionAsUnifiedOffer
        : null;

    const hasActiveMatch = matchedOffer !== null;

    return {
      cellCodes: matchedOffer?.cellCode ?? cellCodesFromUrl ?? null,
      corpId: matchedOffer?.corpId ?? corpIdFromUrl ?? null,
      promoId: matchedOffer?.promoCode ?? promoIdFromUrl ?? null,
      matchedOffer,
      hasActiveMatch,
    };
  }, [router.asPath, router.query, offers, promotions]);
}

/**
 * Extract the current page path from the full URL
 * Removes query parameters and hash
 */
function getCurrentPagePath(asPath: string): string {
  // Remove query string and hash
  const pathWithoutQuery = asPath.split('?')[0].split('#')[0];
  return pathWithoutQuery;
}

/**
 * Check if offer.page matches the current page
 * Simple string matching - checks if offer.page value exists within the currentPage URL
 *
 * Examples:
 * - offer.page: "summer-sale"
 * - currentPage: "/gb/en/promotions/summer-sale.html" -> Match ✓
 * - currentPage: "/gb/en/hotels/london" -> No match ✗
 */
function isPageMatch(offerPage: string | undefined, currentPage: string): boolean {
  if (!offerPage) {
    return false;
  }

  // Check if the offer.page value exists anywhere within the current page URL
  return currentPage.includes(offerPage);
}

/**
 * Find an offer that matches the current page or has matching cellCode/corpId
 * Replicates the logic from HeaderModel.findOffer()
 *
 * Priority:
 * 1. Offer matched by page (only checked on /offers/ pages)
 * 2. Offer matched by cellCode from URL
 * 3. Offer matched by corpId from URL
 */
function findOfferForPage(
  offers: PromotionOffer[],
  currentPage: string,
  cellCodesFromUrl: string | undefined,
  corpIdFromUrl: string | undefined
): PromotionOffer | null {
  if (!offers || offers.length === 0) {
    return null;
  }

  const isOnOffersPage = currentPage.includes('/offers/') || currentPage.includes('/angebote/');

  // Find offer matching page or cellCode/corpId
  const matchedOffer = offers.find(
    (offer: PromotionOffer) =>
      // Priority 1: Page match (only on /offers/ pages)
      (isOnOffersPage && isPageMatch(offer.page, currentPage)) ||
      (offer.cellCode && cellCodesFromUrl && offer.cellCode === cellCodesFromUrl) ||
      (offer.corpId != null && corpIdFromUrl != null && +offer.corpId === +corpIdFromUrl)
  );

  return matchedOffer || null;
}

/**
 * Check if current page is a promotion landing page
 * Matches patterns:
 * - /gb/en/promotions/<promo>.html
 * - /de/de/aktionen/<promo>.html
 * - /en/promotions/<promo>.html
 * - /de/aktionen/<promo>.html
 */
function isPromotionLandingPage(currentPage: string): boolean {
  const promoLandingPageRegex =
    /\/(gb\/en\/promotions|de\/de\/aktionen|en\/promotions|de\/aktionen|promotions|aktionen)\/([^/]+)\.html/;
  return promoLandingPageRegex.test(currentPage);
}

/**
 * Extract promotion page name from URL path
 * e.g., "/gb/en/promotions/10-off.html" => "10-off"
 * e.g., "/en/promotions/10-off.html" => "10-off"
 */
function getPromotionPageName(currentPage: string): string | null {
  const promoLandingPageRegex =
    /\/(gb\/en\/promotions|de\/de\/aktionen|en\/promotions|de\/aktionen|promotions|aktionen)\/([^/]+)\.html/;
  const match = currentPage.match(promoLandingPageRegex);
  return match ? match[2] : null;
}

/**
 * Find a promotion that matches the current page or has matching promoCode
 *
 * Priority:
 * 1. Promotion matched by page (only checked on promotion landing pages)
 * 2. Promotion matched by promoCode (PROMOID) from URL
 */
function findPromotionForPage(
  promotions: Promotion[],
  currentPage: string,
  promoIdFromUrl: string | undefined
): Promotion | null {
  if (!promotions || promotions.length === 0) {
    return null;
  }

  const isOnPromoPage = isPromotionLandingPage(currentPage);
  const promoPageName = isOnPromoPage ? getPromotionPageName(currentPage) : null;

  // Find promotion matching page or promoCode
  return (
    promotions.find(
      (promotion: Promotion) =>
        // Only check enabled promotions
        promotion.enabled &&
        ((isOnPromoPage && promoPageName && promotion.page === promoPageName) ||
          (promotion.promoCode && promoIdFromUrl && promotion.promoCode === promoIdFromUrl))
    ) ?? null
  );
}

export function isOfferActive(offer: PromotionOffer | null): boolean {
  return offer !== undefined && offer !== null && Object.keys(offer).length > 0;
}

export function isPromotionActive(promotion: Promotion | null): boolean {
  return promotion != null && promotion.enabled && Object.keys(promotion).length > 0;
}
