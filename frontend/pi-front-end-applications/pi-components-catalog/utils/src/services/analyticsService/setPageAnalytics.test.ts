import analytics from './analytics';
import setPageAnalytics from './setPageAnalytics';

const analyticsUpdateSpy = jest.spyOn(analytics, 'update').mockReturnValue(undefined);

describe('setPageAnalytics Method', () => {
  afterEach(() => {
    analyticsUpdateSpy.mockReset();
  });

  it('should not call analytics update if provided pathName is not found in the mapper', () => {
    setPageAnalytics('', 'PI', {});
    expect(analyticsUpdateSpy).not.toHaveBeenCalled();
  });

  it('should call analytics update with correct details if provided pathName matches a mapper entry', () => {
    setPageAnalytics('/ancillaries', 'PI', {}, 'en');
    expect(analyticsUpdateSpy).toHaveBeenCalledWith({
      pageName: 'extras',
      pageType: 'booking flow',
      siteType: 'PI',
      funnel_step: 'Web:PI:UK:Ancillaries',
    });
  });

  it('should call analytics DLP', () => {
    setPageAnalytics('/hotels/[...slug]', 'PI', {}, 'en');
    expect(analyticsUpdateSpy).toHaveBeenCalledWith({
      pageName: 'premier inn hotel details',
      pageType: 'look to book',
      siteType: 'PI',
      funnel_step: 'Web:PI:UK:DLP',
    });
  });

  it('should call analytics HDP', () => {
    setPageAnalytics('/hotels/[...slug]', 'PI', { NIGHTS: '1' }, 'de');
    expect(analyticsUpdateSpy).toHaveBeenCalledWith({
      pageName: 'premier inn hotel details',
      pageType: 'look to book',
      siteType: 'PI',
      funnel_step: 'Web:PI:DE:HDP',
    });
  });

  it('should call analytics HDP PIB', () => {
    setPageAnalytics('/hotels/[...slug]', 'PIB', { NIGHTS: '1' }, 'de');
    expect(analyticsUpdateSpy).toHaveBeenCalledWith({
      pageName: 'premier inn hotel details',
      pageType: 'look to book',
      siteType: 'PIB',
      funnel_step: 'Web:PB:DE:HDP',
    });
  });

  it('should call analytics for PIB Spending page', () => {
    setPageAnalytics('/spending', 'PIB', {}, 'en');
    expect(analyticsUpdateSpy).toHaveBeenCalledWith({
      pageName: 'Spending and Reporting: Company spending',
      pageType: 'look to book',
      siteType: 'PIB',
      funnel_step: 'Web:PB:UK:Spending and reporting',
    });
  });
  it('should call analytics for PIB Manage Employees page', () => {
    setPageAnalytics('/manage/employees', 'PIB', {}, 'en');
    expect(analyticsUpdateSpy).toHaveBeenCalledWith({
      pageName: 'Manage Employees',
      pageType: 'look to book',
      siteType: 'PIB',
      funnel_step: 'Web:PB:UK:Manage employees',
    });
  });
});
