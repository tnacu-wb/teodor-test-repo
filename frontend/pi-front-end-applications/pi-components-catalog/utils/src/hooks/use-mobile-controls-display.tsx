import { FT_PI_BB_CONTROLS_MOBILE_DISPLAY } from '@whitbread-eos/api';

import useFeatureToggle from './use-feature-toggle';
import { useScreenSize } from './use-screensize';

const CHANNELS_LIST = new Set(['pi', 'bb']);

export default function useMobileControlsDisplay(channel?: string) {
  const { isLessThanSm, isLessThanMd } = useScreenSize();
  const featureToggles = useFeatureToggle();
  const isMobileDisplayEnabled = featureToggles[FT_PI_BB_CONTROLS_MOBILE_DISPLAY];

  if (!channel) return false;

  const normalizedChannel = channel.toLowerCase();
  const isValidChannel = CHANNELS_LIST.has(normalizedChannel);
  const isMobileScreen = isLessThanSm || isLessThanMd;

  return isMobileDisplayEnabled && isValidChannel && isMobileScreen;
}
