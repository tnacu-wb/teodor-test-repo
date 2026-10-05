import type { Config } from 'tailwindcss';

const config: Config = {
  content: [
    './app/**/*.{ts,tsx}',
    './components/**/*.{ts,tsx}',
    './features/**/*.{ts,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        brand: '#5c2d82',
        'brand-2': '#7b3faf',
        bg: '#f4f6f9',
        panel: '#ffffff',
        ink: '#1c2530',
        'ink-2': '#5a6675',
        'ink-3': '#8a94a3',
        good: '#1f9d6b',
        warn: '#c8811a',
        bad: '#cf3b45',
        info: '#2f6fed',
      },
      boxShadow: {
        card: '0 1px 3px 0 rgb(0 0 0 / 0.06), 0 1px 2px -1px rgb(0 0 0 / 0.06)',
      },
    },
  },
  plugins: [],
};

export default config;
