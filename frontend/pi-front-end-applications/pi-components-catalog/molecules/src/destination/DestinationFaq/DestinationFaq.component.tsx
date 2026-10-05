import { Box, Heading, StyleProps } from '@chakra-ui/react';
import { FaqItem, ScreenSizeValues } from '@whitbread-eos/api';
import { Accordion, Tabs } from '@whitbread-eos/atoms';
import { formatDataTestId, renderSanitizedHtml, useScreenSize } from '@whitbread-eos/utils';
import { CSSProperties } from 'react';

interface Props {
  data: any[]; // type to be added after BE integration;
}

const baseDataTestId = 'DestinationFaq';

export default function DestinationFaq({ data }: Readonly<Props>) {
  if (!data || data.length === 0) {
    return null;
  }

  return (
    <div style={sectionStyle} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <Heading as="h2" {...headingStyle} data-testid={formatDataTestId(baseDataTestId, 'Title')}>
        FAQs
      </Heading>
      <Tabs
        prefixDataTestId={baseDataTestId}
        styles={{ tab: tabStyles, tabList: {} }}
        options={data.map((faqTab: any, index: number) => ({
          index,
          label: faqTab.title,
          content: <DestinationFaqTab questions={faqTab.faqItems} />,
        }))}
      />
    </div>
  );
}

interface TabProps {
  questions: FaqItem[]; // type to be added after BE integration;
}

function DestinationFaqTab({ questions }: Readonly<TabProps>) {
  const screenSize: ScreenSizeValues = useScreenSize();
  const hasTwoColumns = questions.length > 3 && !screenSize.isLessThanMd;
  const allFaqs = questions.map((q: FaqItem) => {
    return {
      title: q.question,
      content: <Box>{renderSanitizedHtml(q.answer ?? '')}</Box>,
    };
  });

  return (
    <div
      className="formatLinks"
      style={accordionStyle}
      data-testid={formatDataTestId(baseDataTestId, 'Accordions')}
    >
      <Accordion
        className="formatLinks"
        allowMultiple={false}
        accordionItems={hasTwoColumns ? allFaqs.slice(0, Math.round(allFaqs.length / 2)) : allFaqs}
        bgColor="var(--chakra-colors-lightGrey5)"
        accordionOverwriteStyles={{
          container: {
            flex: hasTwoColumns || screenSize.isLessThanMd ? 1 : 0.66,
            allowToggle: true,
          },
          item: { cursor: 'default' },
        }}
      />
      {hasTwoColumns && (
        <Accordion
          className="formatLinks"
          allowMultiple={false}
          accordionItems={allFaqs.slice(Math.round(allFaqs.length / 2), allFaqs.length)}
          bgColor="var(--chakra-colors-lightGrey5)"
          accordionOverwriteStyles={{
            container: { flex: 1, allowToggle: true },
            item: { cursor: 'default' },
          }}
        />
      )}
    </div>
  );
}

const accordionStyle = {
  display: 'flex',
  flexDirection: 'row',
  gap: 'var(--chakra-space-lg)',
} as CSSProperties;

const tabStyles = {
  flex: 1,
  padding: 'var(--chakra-space-lg) 0',
} as StyleProps;

const sectionStyle = {
  display: 'flex',
  flexDirection: 'column',
  padding: 'var(--chakra-space-xl) 0',
} as CSSProperties;

const headingStyle = {
  fontSize: '1.8rem',
  fontWeight: '900',
  lineHeight: '2rem',
  color: 'tertiary',
  marginBottom: 'var(--chakra-space-md)',
};
