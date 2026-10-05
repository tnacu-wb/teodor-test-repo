import { config } from '@WB-playwright/config';
import { basicAuth, SECURE2_404_URL, BB_HOME_URL } from '@WB-playwright/constants';
import { UserAccount } from '@WB-playwright/types';
import { Browser, Page, expect, BrowserContext, test } from '@playwright/test';

import { GraphQlUtils } from '../api/request/graphqlCalls';

export const getContext = async (browser: Browser) => {
  return await test.step('Get context', async () => {
    return await browser.newContext({
      httpCredentials: {
        username: basicAuth.username,
        password: basicAuth.password,
      },
    });
  });
};

export const acceptCookies = async (page: Page, context: BrowserContext) => {
  await context.clearCookies();
  const acceptCookiesButton = page.locator('#accept-all-cookies-button');
  const cookieModalId = '#manageCookieModal';

  await test.step('Accept All Cookies', async () => {
    await page.waitForSelector(cookieModalId, {
      state: 'visible',
      timeout: 5000,
    });

    await acceptCookiesButton.click();

    await page.waitForSelector(cookieModalId, {
      state: 'hidden',
    });
  });
};

export const LoginBB = async (browser: Browser, { email, password }: UserAccount) => {
  const context = await getContext(browser);
  await expect(async () => {
    const page = await context.newPage();

    await page.goto(SECURE2_404_URL);

    await page.goto(BB_HOME_URL);
    if (config.ENVIRONMENT !== 'dit') {
      await acceptCookies(page, context);
    }

    await test.step('Add Email', async () => {
      await page.locator('#email-input').fill(email);
    });
    await test.step('Add Password', async () => {
      await page.locator('#password-input').fill(password);
    });
    await test.step('Submit Login', async () => {
      page.locator('#submit-button').click();
    });

    const loginModal = page.locator('#LOGIN_FORM');
    await expect(loginModal, 'User should be logged in').toBeHidden({
      timeout: 10000,
    });
  }, 'Perform Business Booker Login').toPass({
    intervals: [2000],
    timeout: 40000,
  });
  const cookies = context.cookies();
  GraphQlUtils.setAuthToken(cookies);

  return context;
};
