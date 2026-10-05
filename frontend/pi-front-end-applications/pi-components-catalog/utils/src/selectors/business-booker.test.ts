import { UserAccessLevels } from '@whitbread-eos/api';

import { isDataCollectionMsgAndFormVisible, isGuestDetailsPageAllowed } from './business-booker';

describe('business-booker selectors', () => {
  describe('isGuestDetailsPageAllowed method', () => {
    it('should return false if accessLevel is STAYER', () => {
      expect(isGuestDetailsPageAllowed(UserAccessLevels.STAYER)).toEqual(false);
    });

    it('should return true if accessLevel is SUPER, BOOKER or SELF', () => {
      expect(isGuestDetailsPageAllowed(UserAccessLevels.SUPER)).toEqual(true);
      expect(isGuestDetailsPageAllowed(UserAccessLevels.BOOKER)).toEqual(true);
      expect(isGuestDetailsPageAllowed(UserAccessLevels.SELF)).toEqual(true);
    });

    it('should return false is accessLevel is null', () => {
      expect(isGuestDetailsPageAllowed(null)).toEqual(false);
    });

    it('should return false is accessLevel is an empty string', () => {
      expect(isGuestDetailsPageAllowed('')).toEqual(false);
    });
  });

  describe('isDataCollectionMsgAndFormVisible method', () => {
    it('should return false if accessLevel is STAYER or SELF', () => {
      expect(isDataCollectionMsgAndFormVisible(UserAccessLevels.STAYER)).toEqual(false);
      expect(isDataCollectionMsgAndFormVisible(UserAccessLevels.SELF)).toEqual(false);
    });

    it('should return true if accessLevel is SUPER or BOOKER', () => {
      expect(isDataCollectionMsgAndFormVisible(UserAccessLevels.SUPER)).toEqual(true);
      expect(isDataCollectionMsgAndFormVisible(UserAccessLevels.BOOKER)).toEqual(true);
    });

    it('should return false is accessLevel is null', () => {
      expect(isDataCollectionMsgAndFormVisible(null)).toEqual(false);
    });

    it('should return false is accessLevel is an empty string', () => {
      expect(isDataCollectionMsgAndFormVisible('')).toEqual(false);
    });
  });
});
