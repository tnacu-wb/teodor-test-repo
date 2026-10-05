export interface FooterTab {
  name: string;
  intro: Intro;
  columns: FooterColumn[];
}

export interface Intro {
  name: string;
  description: string;
}

export interface FooterColumn {
  name: string;
  linkItems: FooterLinkItem[];
}

export interface FooterLinkItem {
  name: string;
  openInNewTab: boolean;
  linkSrc: string;
}

export interface FooterIcon {
  linkSrc: string;
  label: string;
  iconSrc: string;
}
