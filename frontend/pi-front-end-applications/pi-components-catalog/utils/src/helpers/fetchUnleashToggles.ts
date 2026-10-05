import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

import { getDefaultSessionTracing } from '../utils/tracing';
import { DynamicContext, DynamicObject, getUnleashTogglesServerOrClient } from '../utils/unleash';

/**
 * Object for fetching Unleash feature toggles.
 * @property {GetServerSidePropsContext} props - The props object from GetServerSidePropsContext.
 * @property {string} label - The current page & application Example: pi:payment.
 * @property {DynamicObject} flagsWithFallback - The feature toggles with default values for fallback.
 * @property {DynamicContext} [context={}] - Additional context object.
 */

/**
 * Fetches Unleash feature toggles from the server.
 * @returns {DynamicObject} - The feature toggles fetched from Unleash.
 *  * @example
 * // Example usage:
 * const props = { ... }; // GetServerSidePropsContext object
 * const label = 'pi:payment';
 * const flagsWithFallback = { feature1: true, feature2: false };
 * const toggles = await getUnleashToggles(props, label, flagsWithFallback);
 */
export async function getUnleashToggles(
  props: GetServerSidePropsContext,
  label: string,
  flagsWithFallback: DynamicObject,
  context: DynamicContext = {},
  ccuiSession?: string
) {
  const cookies = new Cookies(props.req, props.res);
  const sessionTracing = getDefaultSessionTracing(cookies);
  return getUnleashTogglesServerOrClient(
    cookies,
    label,
    flagsWithFallback,
    props?.req?.url ?? '',
    props.query,
    sessionTracing,
    context,
    ccuiSession
  );
}
