export const ROOM_TYPE = {
  STANDARD: 'DBLWIN',
  STANDARD_EXTRA: 'EXTDBL',
  STANDARD_BIGGER: 'BIGWIN',
  PREMIER_PLUS: 'PPLDBL',
  PREMIER_PLUS_VIEW: 'VPPDBL',
  // Family rooms, also used for Standard Twin rooms (Double + Sofa bed)
  FAMILY_TRIPLE: 'FMTRPL',
  FAMILY_THRE: 'FMTHRE',
  FAMILY_QUAD: 'FMQUAD',
  FAMILY_FOUR: 'FMFOUR',
  TRIPLE_SOFA_CHAIR: 'FMTRSC',
  FAMILY_SOFA_BUNK_BED: 'FMTBUNK',
  PREMIER_PLUS_FAMILY: 'PFAMIL',

  // Improved Twin rooms (2 separate beds):
  TWIN_RM: 'TWINRM', // Two single beds
  TWIN_DOUBLE: 'DBLDBL', // Twin double beds
  TWIN_DOUBLE_ZIP_LINK: 'ZPLDBL', // Two doubles through inner zip link door
  PDBZPL: 'PDBZPL', // Premier Plus  twin room
  BRFTWN: 'BRFTWN',
  BRFZPL: 'BRFZPL',
  BRFDBL: 'BRFDBL',

  // Twin room types
  TWIN_TWO_BEDS: 'twobeds', // improved twin
  TWIN_DOUBLE_SOFA: 'doublesofa', // standard twin

  TWIN: 'TWIN',
  DOUBLE: 'DOUBLE',
  SINGLE: 'SINGLE',
  LOWERED_DOUBLE: 'LOWDBL',
  WET_DOUBLE: 'WETDBL',
  LOWERED_TWIN: 'LOWTWN',
  WET_TWIN: 'WETTWN',
  ACCESSIBLE_TWIN: 'ACCWIN',
  LOWERED: 'lowered',
  WET: 'wet',
  STANDARD_ACCESSIBLE: 'str',
  BARRIER_FREE: 'bfr',

  //premier plus accessible room types
  PREMIER_PLUS_LOWERED_DOUBLE: 'PPDLOW',
  PREMIER_PLUS_WET_DOUBLE: 'PPDWET',

  // Hub windowless room types
  STANDARD_WINDOWLESS: 'DBLNWD',
  BIGGER_WINDOWLESS: 'BIGNWD',
  ACCESSIBLE_WINDOWLESS: 'ACCNWD',

  //Superior room types
  LARGER_DOUBLE: 'LDOUBL',
  LARGER_FAMILY_FOUR: 'LFMFOR',
};

export const loweredBathRoomTypes = [
  ROOM_TYPE.LOWERED_DOUBLE,
  ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE,
  ROOM_TYPE.LOWERED_TWIN,
];

export const wetBathRoomTypes = [
  ROOM_TYPE.WET_DOUBLE,
  ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE,
  ROOM_TYPE.WET_TWIN,
];

export const barrierFreeRoomsSpecialRequestCodes = ['BFRE'];

export const roomTypeMapping: any = {
  'Accessible Double': {
    [ROOM_TYPE.LOWERED]: ROOM_TYPE.LOWERED_DOUBLE,
    [ROOM_TYPE.WET]: ROOM_TYPE.WET_DOUBLE,
    [ROOM_TYPE.BARRIER_FREE]: ROOM_TYPE.BRFDBL,
    [ROOM_TYPE.STANDARD_ACCESSIBLE]: ROOM_TYPE.WET_DOUBLE,
    premierPlus: {
      [ROOM_TYPE.LOWERED]: ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE,
      [ROOM_TYPE.WET]: ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE,
    },
  },
  'Barrierefreies Doppelzimmer': {
    [ROOM_TYPE.LOWERED]: ROOM_TYPE.LOWERED_DOUBLE,
    [ROOM_TYPE.WET]: ROOM_TYPE.WET_DOUBLE,
    [ROOM_TYPE.BARRIER_FREE]: ROOM_TYPE.BRFDBL,
    [ROOM_TYPE.STANDARD_ACCESSIBLE]: ROOM_TYPE.WET_DOUBLE,
    premierPlus: {
      [ROOM_TYPE.LOWERED]: ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE,
      [ROOM_TYPE.WET]: ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE,
    },
  },
  'Accessible Twin': {
    [ROOM_TYPE.LOWERED]: ROOM_TYPE.LOWERED_TWIN,
    [ROOM_TYPE.WET]: ROOM_TYPE.WET_TWIN,
  },
  'Barrierefreies Zweibettzimmer': {
    [ROOM_TYPE.LOWERED]: ROOM_TYPE.LOWERED_TWIN,
    [ROOM_TYPE.WET]: ROOM_TYPE.WET_TWIN,
  },
  'Rollstuhlgerechtes Doppelzimmer': {
    [ROOM_TYPE.LOWERED]: ROOM_TYPE.LOWERED_DOUBLE,
    [ROOM_TYPE.WET]: ROOM_TYPE.WET_DOUBLE,
    [ROOM_TYPE.STANDARD_ACCESSIBLE]: ROOM_TYPE.WET_DOUBLE,
    [ROOM_TYPE.BARRIER_FREE]: ROOM_TYPE.BRFDBL,
    premierPlus: {
      [ROOM_TYPE.LOWERED]: ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE,
      [ROOM_TYPE.WET]: ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE,
    },
  },
};

export const twinRoomImprovedSpecialRequests: string[] = ['TW2S'];
export const twinRoomStandardSpecialRequests: string[] = ['TWDS'];

export const twinRoomSpecialRequests: string[] = [
  ...twinRoomImprovedSpecialRequests,
  ...twinRoomStandardSpecialRequests,
];

export const PREMIER_PLUS_ROOM_TYPES = [
  ROOM_TYPE.PREMIER_PLUS,
  ROOM_TYPE.PDBZPL,
  ROOM_TYPE.PREMIER_PLUS_FAMILY,
  ROOM_TYPE.PREMIER_PLUS_VIEW,
  ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE,
  ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE,
];

export const ACCESSIBLE_ROOM_TYPES = [
  ROOM_TYPE.LOWERED_DOUBLE,
  ROOM_TYPE.WET_DOUBLE,
  ROOM_TYPE.LOWERED_TWIN,
  ROOM_TYPE.WET_TWIN,
  ROOM_TYPE.ACCESSIBLE_TWIN,
  ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE,
  ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE,
  ROOM_TYPE.BRFDBL,
  ROOM_TYPE.BRFZPL,
  ROOM_TYPE.BRFTWN,
  ROOM_TYPE.ACCESSIBLE_WINDOWLESS,
];

export const ACCESSIBLE_BARRIER_ROOM_TYPES = [
  ROOM_TYPE.LOWERED_DOUBLE,
  ROOM_TYPE.WET_DOUBLE,
  ROOM_TYPE.BRFDBL,
  ROOM_TYPE.BRFZPL,
  ROOM_TYPE.BRFTWN,
  ROOM_TYPE.ACCESSIBLE_WINDOWLESS,
];

export const ACCESSIBLE_ROOM_TYPE = 'DIS';

export const ACCESSIBLE = 'accessible';
export const TWINROOM = 'twinRoom';
export const PREMIER_PLUS_ACCESSIBLE = 'premierPlusAccessible';
export const ACCESSIBLE_BARRIER_FREE = 'accessibleBarrierFree';

export const isIconForRoomTypes: { [key: string]: boolean } = {
  [ACCESSIBLE]: true,
  [PREMIER_PLUS_ACCESSIBLE]: true,
  [ACCESSIBLE_BARRIER_FREE]: true,
};

export const GALLERY_IMAGE_MAPPING = [
  {
    type: ACCESSIBLE,
    images: [
      {
        pathKey: 'accessible.loweredBath.image.path1',
        titleKey: 'hoteldetails.accessibleLoweredBathroom',
      },
      {
        pathKey: 'accessible.wetRoom.image.path2',
        titleKey: 'hoteldetails.accessibleWetRoom',
      },
    ],
  },
  {
    type: TWINROOM,
    images: [
      {
        pathKey: 'twinroom.improvedTwin.image.path1',
        titleKey: 'twinroom.improvedTwin.title',
      },
      {
        pathKey: 'twinroom.standardTwin.image.path2',
        titleKey: 'twinroom.standardTwin.title',
      },
    ],
  },
  {
    type: PREMIER_PLUS_ACCESSIBLE,
    images: [
      {
        pathKey: 'accessible.premierplus.loweredBath.image.path1',
        titleKey: 'hoteldetails.accessibleLoweredBathroom',
      },
      {
        pathKey: 'accessible.premierplus.wetRoom.image.path2',
        titleKey: 'hoteldetails.accessibleWetRoom',
      },
    ],
  },
  {
    type: ACCESSIBLE_BARRIER_FREE,
    images: [
      {
        pathKey: 'standard.accessible.image.path1',
        titleKey: 'hoteldetails.standardAccessibleRoom',
      },
      {
        pathKey: 'barrierfree.room.image.path2',
        titleKey: 'hoteldetails.barrierFreeRoom',
        icon: ACCESSIBLE_BARRIER_FREE,
      },
    ],
  },
];

export const ROOM_TYPE_TO_DISPLAY_TEXT_MAPPING = {
  [ROOM_TYPE.DOUBLE]: 'search.roomTypes.double',
  [ROOM_TYPE.SINGLE]: 'hoteldetails.rates.standardroomstitle',
  [ROOM_TYPE.STANDARD]: 'hoteldetails.rates.standardroomstitle',
  [ROOM_TYPE.PREMIER_PLUS]: 'hoteldetails.rates.premierplus',
  [ROOM_TYPE.STANDARD_EXTRA]: 'hoteldetails.standardRoomExtra',
  [ROOM_TYPE.STANDARD_BIGGER]: 'pihotelinfo.hubBiggerRoomTitle',
  [ROOM_TYPE.FAMILY_TRIPLE]: 'search.roomTypes.family',
  [ROOM_TYPE.FAMILY_THRE]: 'search.roomTypes.family',
  [ROOM_TYPE.FAMILY_FOUR]: 'search.roomTypes.family',
  [ROOM_TYPE.FAMILY_QUAD]: 'search.roomTypes.family',
  [ROOM_TYPE.WET_DOUBLE]: 'accessible.double',
  [ROOM_TYPE.LOWERED_DOUBLE]: 'accessible.double',
  [ROOM_TYPE.BRFDBL]: 'accessible.double',
  [ROOM_TYPE.BRFZPL]: 'accessible.double',
  [ROOM_TYPE.BRFTWN]: 'accessible.double',
  [ROOM_TYPE.WET_TWIN]: 'accessible.twin',
  [ROOM_TYPE.LOWERED_TWIN]: 'accessible.twin',
  [ROOM_TYPE.ACCESSIBLE_TWIN]: 'accessible.twin',
  [ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE]: 'accessible.double',
  [ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE]: 'accessible.double',
  [ROOM_TYPE.TWIN_RM]: 'roomrequirements.type.single',
  [ROOM_TYPE.TWIN_DOUBLE]: 'roomrequirements.type.single',
  [ROOM_TYPE.TWIN_DOUBLE_ZIP_LINK]: 'groupBooking.roomRequirements.type.twin.title',
  [ROOM_TYPE.STANDARD_WINDOWLESS]: 'pihotelinfo.dblnwd.title',
  [ROOM_TYPE.BIGGER_WINDOWLESS]: 'pihotelinfo.bignwd.title',
  [ROOM_TYPE.ACCESSIBLE_WINDOWLESS]: 'pihotelinfo.accnwd.title',
};

export const ROOM_TYPE_TO_BATHROOM_MAPPING = {
  [ROOM_TYPE.LOWERED_DOUBLE]: { loweredKey: 'LOWDBL', wetKey: 'WETDBL' },
  [ROOM_TYPE.PREMIER_PLUS_LOWERED_DOUBLE]: { loweredKey: 'LOWDBL', wetKey: 'WETDBL' },
  [ROOM_TYPE.LOWERED_TWIN]: { loweredKey: 'LOWTWN', wetKey: 'WETTWN' },
  [ROOM_TYPE.WET_DOUBLE]: { loweredKey: 'LOWDBL', wetKey: 'WETDBL' },
  [ROOM_TYPE.PREMIER_PLUS_WET_DOUBLE]: { loweredKey: 'LOWDBL', wetKey: 'WETDBL' },
  [ROOM_TYPE.WET_TWIN]: { loweredKey: 'LOWTWN', wetKey: 'WETTWN' },
};
