import type { Meta, StoryObj } from '@storybook/react';

import { CardIcon } from './card-icon';

// Simple SVG card brand icons as data URIs for Storybook preview
const visaSvg =
  'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 40 24%22%3E%3Crect width=%2240%22 height=%2224%22 rx=%222%22 fill=%22%231A1F71%22/%3E%3Ctext x=%2250%25%22 y=%2256%25%22 dominant-baseline=%22middle%22 text-anchor=%22middle%22 fill=%22%23F7B600%22 font-size=%228%22 font-family=%22Arial%22 font-weight=%22bold%22%3EVISA%3C/text%3E%3C/svg%3E';

const mastercardSvg =
  'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 40 24%22%3E%3Crect width=%2240%22 height=%2224%22 rx=%222%22 fill=%22%23fff%22/%3E%3Ccircle cx=%2215%22 cy=%2212%22 r=%227%22 fill=%22%23EB001B%22/%3E%3Ccircle cx=%2225%22 cy=%2212%22 r=%227%22 fill=%22%23F79E1B%22 opacity=%220.85%22/%3E%3C/svg%3E';

const amexSvg =
  'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 40 24%22%3E%3Crect width=%2240%22 height=%2224%22 rx=%222%22 fill=%22%232E77BC%22/%3E%3Ctext x=%2250%25%22 y=%2256%25%22 dominant-baseline=%22middle%22 text-anchor=%22middle%22 fill=%22white%22 font-size=%226%22 font-family=%22Arial%22 font-weight=%22bold%22%3EAMERICAN%20EXPRESS%3C/text%3E%3C/svg%3E';

const dinnersSvg =
  'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 40 24%22%3E%3Crect width=%2240%22 height=%2224%22 rx=%222%22 fill=%22%23fff%22/%3E%3Ccircle cx=%2220%22 cy=%2212%22 r=%229%22 fill=%22none%22 stroke=%22%23004A97%22 stroke-width=%221.5%22/%3E%3Ccircle cx=%2215.5%22 cy=%2212%22 r=%225.5%22 fill=%22none%22 stroke=%22%23004A97%22 stroke-width=%221%22/%3E%3Ccircle cx=%2224.5%22 cy=%2212%22 r=%225.5%22 fill=%22none%22 stroke=%22%23004A97%22 stroke-width=%221%22/%3E%3C/svg%3E';

const pibaSvg =
  'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 40 24%22%3E%3Crect width=%2240%22 height=%2224%22 rx=%222%22 fill=%22%2300798E%22/%3E%3Ctext x=%2250%25%22 y=%2256%25%22 dominant-baseline=%22middle%22 text-anchor=%22middle%22 fill=%22white%22 font-size=%228%22 font-family=%22Arial%22 font-weight=%22bold%22%3EPIBA%3C/text%3E%3C/svg%3E';

const meta: Meta<typeof CardIcon> = {
  title: 'Shadcn/CardIcon',
  component: CardIcon,
  argTypes: {
    type: {
      description: 'Card type code used to resolve icon mapping.',
      options: ['VI', 'MA', 'AX', 'DN', 'UNKNOWN'],
      control: { type: 'radio' },
    },
    icons: {
      description: 'Map of payment icon keys to source URLs.',
      control: 'object',
    },
    className: {
      description: 'Optional utility classes for icon sizing.',
      control: 'text',
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Payment card brand icon renderer for supported payment types.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const icons = {
  'icon.payment.visa': visaSvg,
  'icon.payment.mastercard': mastercardSvg,
  'icon.payment.amex': amexSvg,
  'icon.payment.dinners': dinnersSvg,
  'icon.payment.piba': pibaSvg,
};

export const Default: Story = {
  args: {
    type: 'VI',
    icons,
  },
};

export const AllCardTypes: Story = {
  render: () => {
    const cardTypes = ['VI', 'MA', 'AX', 'DN', 'PI'] as const;

    return (
      <div className="flex flex-wrap items-center gap-3">
        {cardTypes.map((cardType) => (
          <div
            key={cardType}
            className="flex items-center gap-2 rounded border border-lightGrey3 p-2"
          >
            <span className="w-7 text-xs font-semibold text-darkGrey2">{cardType}</span>
            <CardIcon type={cardType} icons={icons} />
          </div>
        ))}
      </div>
    );
  },
};
