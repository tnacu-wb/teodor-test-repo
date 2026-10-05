'use client';

import { cn } from '@whitbread-eos/utils';
import { Bar, BarChart, CartesianGrid, XAxis, YAxis, Cell } from 'recharts';
import { DataKey } from 'recharts/types/util/types';

import { ChartContainer, ChartConfig, ChartTooltip, ChartTooltipContent } from '../Chart/chart';

interface Props {
  chartData: Record<any, string | number>[];
  chartConfig: ChartConfig;
  className?: string;
  axisDataKey: string;
  barDataKey: DataKey<any>;
  dataTestId?: string;
  tooltipNameKey?: string;
  tooltipPosition?: { x: number; y: number } | null;
  activeIndex?: number | null;
  showTooltip?: boolean;
  disabled?: boolean;
  formatXAxis?: (value: number) => string;
  formatYAxis?: (value: number) => string;
  formatTooltip?: () => string;
  formatLabel?: () => string;
  onMouseEnter?: (data: any, index: number) => void;
  onMouseLeave?: () => void;
}

export function FinancialChart({
  chartData,
  chartConfig,
  axisDataKey,
  dataTestId,
  className,
  tooltipNameKey,
  barDataKey,
  tooltipPosition = { x: 0, y: 0 },
  activeIndex = null,
  showTooltip = false,
  disabled = false,
  formatXAxis,
  formatYAxis,
  formatTooltip,
  formatLabel,
  onMouseEnter,
  onMouseLeave,
}: Props) {
  return (
    <ChartContainer
      config={chartConfig}
      className={cn(
        className,
        'relative h-[14.375rem] w-full',
        disabled ? 'opacity-[30%]' : '',
        disabled ? 'disabled' : ''
      )}
      data-testid={`${dataTestId}-recharts-container`}
    >
      <BarChart
        accessibilityLayer
        data={chartData}
        barSize={38.83}
        {...{
          overflow: 'visible',
        }}
      >
        <CartesianGrid horizontal={true} vertical={false} />
        <XAxis
          dataKey={axisDataKey}
          tick={{ fill: 'var(--dark-grey-1)', opacity: activeIndex !== null ? 0.5 : 1 }}
          tickLine={false}
          tickMargin={10}
          axisLine={false}
          tickFormatter={formatXAxis}
        />
        <YAxis
          type="number"
          axisLine={false}
          tick={{ fill: 'var(--dark-grey-1)', width: 250 }}
          tickFormatter={formatYAxis}
          tickLine={false}
          tickCount={6}
          interval={0}
        />
        <ChartTooltip
          active={showTooltip}
          labelClassName="recharts-tooltip-label"
          position={{ x: tooltipPosition?.x, y: tooltipPosition?.y }}
          content={
            <ChartTooltipContent
              className="text-[.875rem] leading-[1.25rem] min-w-[9.68rem] min-h-[4.87rem] rounded-sm px-[1rem] py-[.5rem] bg-[var(--tooltipInfo)]"
              nameKey={tooltipNameKey}
              formatter={formatTooltip}
              labelFormatter={formatLabel}
            />
          }
        />
        <Bar
          dataKey={barDataKey}
          onMouseEnter={!disabled ? onMouseEnter : undefined}
          onMouseLeave={!disabled ? onMouseLeave : undefined}
          fill={disabled ? 'var(--dark-grey-2)' : 'var(--primaryColor)'}
        >
          {chartData?.map((entry, index) => {
            const activeCellClass =
              activeIndex === null
                ? ''
                : activeIndex === index
                  ? 'active-chart-cell'
                  : 'inactive-chart-cell';
            const lastCellClass = index === chartData?.length - 1 ? ' last-chart-cell' : '';

            return (
              <Cell
                cursor={!disabled ? 'pointer' : 'default'}
                key={`cell-${index}`}
                className={`chart-cell ${activeCellClass}${lastCellClass}`}
                fill={disabled ? 'var(--dark-grey-2)' : 'var(--primaryColor)'}
              />
            );
          })}
        </Bar>
      </BarChart>
    </ChartContainer>
  );
}
