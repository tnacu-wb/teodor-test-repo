import type { StayDetailsStateLocalStorageType } from '@whitbread-eos/api';

// Initial value for StayDetailsState
export const STAY_DETAILS_STATE_INITIAL_VALUE: StayDetailsStateLocalStorageType = {
  data: {
    info: {
      suggestion: {
        location: {
          latitude: '',
          longitude: '',
        },
        placeId: '',
        hotelId: '',
      },
    },
  },
};

export const SEARCH_REFERRER_INITIAL_VALUE = {
  data: {
    referrer: '',
  },
};

export const DISTANCE_FROM_SEARCH_INITIAL_VALUE = {
  data: {
    distance: 0,
  },
};
