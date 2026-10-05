'use client';

import { FS_SILENT_SUBSTITUTION } from '@whitbread-eos/api';
import type {
  ReservationById,
  ReservationRoomType,
  SilentSubstitutionLocalStorage,
} from '@whitbread-eos/api';

import { matchSilentSubstitutions } from '../formatters/formatters';
import { getCurrentReservationStorageData } from '../selectors/ancillaries';
import { updateSilentSubstLocalStorage } from '../selectors/hotel-details';
import useFeatureSwitch from './use-feature-switch';

export default function useSilentRoomsMatch(
  basketReference: string,
  reservations: ReservationById[]
): ReservationRoomType[] {
  const isSilentFeatureFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SILENT_SUBSTITUTION,
  });

  if (!isSilentFeatureFlagEnabled || !reservations.length || !basketReference) {
    return [];
  }

  const substitutionsStorage: SilentSubstitutionLocalStorage | null =
    getCurrentReservationStorageData(basketReference);
  if (!substitutionsStorage?.value?.length) {
    return [];
  }

  const substitutions = substitutionsStorage.value;
  const alreadyMatched = substitutions.find(
    (substitution: ReservationRoomType) => substitution?.filtered !== undefined
  );
  if (alreadyMatched) {
    return substitutions;
  }

  const matchedSubstitutions = matchSilentSubstitutions(reservations, substitutions);
  updateSilentSubstLocalStorage(basketReference, matchedSubstitutions);

  return matchedSubstitutions;
}
