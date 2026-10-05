import { config } from '@WB-playwright/config';
import { Page } from '@playwright/test';

export const Search_Container_IB = async (page: Page) => {
  return config.DEVICE === 'desktop'
    ? page.getByTestId('IB-Search-Container-Desktop')
    : page.getByTestId('IB-Search-Container-Mobile');
};

export const Search_Button_IB = async (page: Page) => {
  return config.DEVICE === 'desktop'
    ? page.getByTestId('IB-Desktop-Search-Button')
    : page.getByTestId('IB-Mobile-Search-Button');
};
export const Side_Bar_IB = async (page: Page) => {
  return config.DEVICE === 'desktop'
    ? page.getByTestId('SidebarDesktop-container')
    : page.getByTestId('SidebarMobile-container');
};
