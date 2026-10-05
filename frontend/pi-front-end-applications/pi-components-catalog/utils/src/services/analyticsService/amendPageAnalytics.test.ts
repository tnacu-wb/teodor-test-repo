import updateAmendPageAnalytics from './amendPageAnalytics';
import analytics from './analytics';

const analyticsUpdateSpy = jest.spyOn(analytics, 'update').mockReturnValue(undefined);

describe('amendPageAnalytics', () => {
  describe('updateAmendPageAnalytics Method', () => {
    it('should call analytics update with provided data', () => {
      updateAmendPageAnalytics(
        {
          change: 'change',
          revenue: 10,
          extrasRevenueChange: 5,
          foodRevenueChange: 5,
          nightsChange: 2,
          roomTypeChange: true,
          roomsChange: 1,
          totalRevenueChange: 15,
        },
        'GAA8181177'
      );
      expect(analyticsUpdateSpy).toHaveBeenCalledWith({
        change: 'change',
        revenue: 10,
        extrasRevenueChange: 5,
        foodRevenueChange: 5,
        nightsChange: 2,
        roomTypeChange: true,
        roomsChange: 1,
        totalRevenueChange: 15,
      });
    });
    it('should call analytics update with validation data', () => {
      updateAmendPageAnalytics(
        {
          change: 'change',
          revenue: 10,
          extrasRevenueChange: 5,
          foodRevenueChange: 5,
          nightsChange: 2,
          roomTypeChange: true,
          roomsChange: 1,
          totalRevenueChange: 15,
          validation: 'basket error',
        },
        'GAA8181177'
      );
      expect(analyticsUpdateSpy).toHaveBeenCalledWith({
        change: 'change',
        revenue: 10,
        extrasRevenueChange: 5,
        foodRevenueChange: 5,
        nightsChange: 2,
        roomTypeChange: true,
        roomsChange: 1,
        totalRevenueChange: 15,
        validation: 'basket error',
      });
    });
  });
});
