const { fontFamily } = require('tailwindcss/defaultTheme');
/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/innBusiness/**/*.{js,ts,jsx,tsx,mdx}',
    '../../../pi-components-catalog/**/src/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  future: {
    hoverOnlyWhenSupported: true,
  },
  theme: {
    container: {
      center: true,
      padding: '2rem',
      screens: {
        '2xl': '1400px',
      },
    },
    extend: {
      colors: {
        border: 'hsl(var(--border))',
        input: 'hsl(var(--input))',
        ring: 'hsl(var(--ring))',
        background: 'hsl(var(--background))',
        foreground: 'hsl(var(--foreground))',
        primary: {
          DEFAULT: 'hsl(var(--primary))',
          foreground: 'hsl(var(--primary-foreground))',
        },
        secondary: {
          DEFAULT: 'hsl(var(--secondary))',
          foreground: 'hsl(var(--secondary-foreground))',
        },
        destructive: {
          DEFAULT: 'hsl(var(--destructive))',
          foreground: 'hsl(var(--destructive-foreground))',
        },
        muted: {
          DEFAULT: 'hsl(var(--muted))',
          foreground: 'hsl(var(--muted-foreground))',
        },
        accent: {
          DEFAULT: 'hsl(var(--accent))',
          foreground: 'hsl(var(--accent-foreground))',
        },
        popover: {
          DEFAULT: 'hsl(var(--popover))',
          foreground: 'hsl(var(--popover-foreground))',
        },
        card: {
          DEFAULT: 'hsl(var(--card))',
          foreground: 'hsl(var(--card-foreground))',
        },
        primaryColor: 'var(--primaryColor)',
        secondaryColor: 'var(--secondaryColor)',
        secondaryColorHover: 'var(--secondaryColorHover)',
        lightGrey1: 'var(--light-grey-1)',
        lightGrey2: 'var(--light-grey-2)',
        lightGrey3: 'var(--light-grey-3)',
        lightGrey4: 'var(--light-grey-4)',
        lightGrey5: 'var(--light-grey-5)',
        darkGrey1: 'var(--dark-grey-1)',
        darkGrey2: 'var(--dark-grey-2)',
        alertYellow1: 'var(--alert-yellow-1)',
        linearGradient: 'var(--linearGradient)',
        baseWhite: 'var(--baseWhite)',
        toggleButtonPressed: 'var(--toggleButtonPressed)',
        successTint: 'var(--successTint)',
        successTint2: 'var(--successTint2)',
        success: 'var(--success)',
        error: 'var(--error)',
        errorTint: 'var(--errorTint)',
        warning: 'var(--warning)',
        tooltipError: 'var(--tooltipError)',
        tooltipInfo: 'var(--tooltipInfo)',
        tooltipInfoTint: 'var(--tooltipInfoTint)',
        notificationAlertBg: 'var(--notificationAlertBg)',
        notificationAlertBorder: 'var(--notificationAlertBorder)',
        destructive: 'var(--destructive)',
      },
      width: {
        sidebarWidth: `var(--sidebarWidth)`,
        sidebarLevel2Width: `var(--sidebarLevel2Width)`,
        sidebarCollapsedWidth: `var(--sidebarCollapsedWidth)`,
      },
      height: {
        headerHeight: `var(--headerHeight)`,
        mobileHeaderHeight: `var(--mobileHeaderHeight)`,
        mobileSidebarHeight: `var(--mobileSidebarHeight)`,
        mobileSidebarLevel2Height: `var(--mobileSidebarLevel2Height)`,
        contentHeight: `calc(100vh - var(--headerHeight))`,
      },
      borderRadius: {
        lg: `var(--radius)`,
        md: `calc(var(--radius) - 2px)`,
        sm: 'calc(var(--radius) - 4px)',
      },
      fontFamily: {
        sans: ['var(--font-sans)', ...fontFamily.sans],
      },
      keyframes: {
        'accordion-down': {
          from: { height: '0' },
          to: { height: 'var(--radix-accordion-content-height)' },
        },
        'accordion-up': {
          from: { height: 'var(--radix-accordion-content-height)' },
          to: { height: '0' },
        },
      },
      animation: {
        'accordion-down': 'accordion-down 0.2s ease-out',
        'accordion-up': 'accordion-up 0.2s ease-out',
      },
      boxShadow: {
        DEFAULT: '0 2px 12px 0 #CCC',
        variantAccountHolder: '0px 8px 15px rgba(0, 0, 0, 0.15)',
        variantTooltip: '0px 2px 8px rgba(0, 0, 0, 0.20)',
      },
      screens: {
        mobile: { max: '1279px' },
        tablet: { min: '1280px', max: '1439px' },
      },
    },
  },
  plugins: [require('tailwindcss-animate')],
};
