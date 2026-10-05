import { handleManageBookingParamsCCUI } from './handleManageBookingParamsCCUI';

describe('handleManageBookingParamsCCUI Method', () => {
  it('should have removed ccuiPrevSearchCriteria from sessionStorage', () => {
    const removeItem = jest.spyOn(Object.getPrototypeOf(sessionStorage), 'removeItem');
    handleManageBookingParamsCCUI();
    expect(removeItem).toHaveBeenCalled();
  });
});
