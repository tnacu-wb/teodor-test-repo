import { SrpLabel, SrpInfo, DynamicFilters } from './graphql';

export interface FilterByLabels {
  filters: {
    label?: SrpLabel;
    info?: SrpInfo;
  };
  dynamicFilters?: DynamicFilters[];
}
