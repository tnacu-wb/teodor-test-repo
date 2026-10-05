const businessBookerTailwindConfig = require('../../apps/next-apps/business-booker/tailwind.config.js');

/** @type {import('tailwindcss').Config} */
module.exports = {
  ...businessBookerTailwindConfig,
  content: [
    './src/**/*.{js,ts,jsx,tsx,mdx}',
    './.storybook/**/*.{js,ts,jsx,tsx,mdx}',
    '../../apps/next-apps/business-booker/src/**/*.{js,ts,jsx,tsx,mdx}',
  ],
};
