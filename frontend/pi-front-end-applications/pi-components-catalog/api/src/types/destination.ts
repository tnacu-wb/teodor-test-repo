import { Breadcrumb } from './graphql';

export interface HeroSectionData {
  title: string;
  description: string | React.JSX.Element | React.JSX.Element[];
  picture: string;
  breadcrumbs: Breadcrumb[];
}

export interface CommonIconsQuery {
  data: { getPageData: { commonIconsEndpoint: string } };
}

export interface SelectedFilter {
  name: string;
  codes: string[];
  operator: string;
  queryParam: string;
}
