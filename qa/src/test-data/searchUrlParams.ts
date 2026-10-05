/** Search URL parameter names used when validating URL/query-state search behavior. */
export class SearchUrlParams {
  private constructor() {}

  static readonly PLACEID = 'PLACEID';
  static readonly ADULT = 'ADULT1';
  static readonly CHILD = 'CHILD1';
  static readonly COT = 'COT1';
  static readonly ROOMS = 'ROOMS';
  static readonly DAY = 'ARRdd';
  static readonly MONTH = 'ARRmm';
  static readonly YEAR = 'ARRyyyy';
  static readonly NIGHTS = 'NIGHTS';
  static readonly SEARCH_TERM = 'searchModel.searchTerm';
}
