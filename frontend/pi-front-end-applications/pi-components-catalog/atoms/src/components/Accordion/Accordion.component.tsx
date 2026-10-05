import type {
  AccordionItemProps,
  AccordionPanelProps,
  AccordionProps as AcdProps,
  BoxProps,
  ButtonProps,
  FlexProps,
  StyleProps,
  TextProps,
} from '@chakra-ui/react';
import {
  Accordion as AccordionChakra,
  AccordionButton,
  AccordionIcon,
  AccordionItem,
  AccordionPanel,
  Flex,
} from '@chakra-ui/react';
import { useSemanticTypography } from '@whitbread-eos/utils';
import React, { ReactNode } from 'react';

export interface AccordionItemProp {
  title: string | ReactNode;
  content: string | ReactNode;
  onToggleSection?: () => void;
}

type accordionOverwriteProps = {
  container?: AcdProps;
  button?: BoxProps & ButtonProps;
  text?: TextProps;
  panel?: AccordionPanelProps;
  item?: AccordionItemProps;
  icon?: StyleProps;
};

interface Props extends AcdProps {
  accordionItems: AccordionItemProp[];
  allowMultiple?: boolean;
  bgColor?: string;
  accordionOverwriteStyles?: accordionOverwriteProps;
}

export default function Accordion(props: Readonly<Props>) {
  const {
    accordionItems,
    allowMultiple = true,
    bgColor = 'lightGrey5',
    accordionOverwriteStyles,
    allowToggle,
  } = props;

  const getTypographyProps = useSemanticTypography();

  const titleTextOverwriteStyles = (accordionOverwriteStyles?.text ?? {}) as TextProps;
  const textItemLegacyTypography = {
    fontSize: titleTextOverwriteStyles.fontSize,
    fontWeight: titleTextOverwriteStyles.fontWeight ?? 600,
    lineHeight: titleTextOverwriteStyles.lineHeight,
    letterSpacing: titleTextOverwriteStyles.letterSpacing,
    fontFamily: titleTextOverwriteStyles.fontFamily,
    textTransform: titleTextOverwriteStyles.textTransform,
  };

  const titleTextLayoutOverwriteStyles: FlexProps = {
    ...titleTextOverwriteStyles,
    fontSize: undefined,
    fontWeight: undefined,
    lineHeight: undefined,
    letterSpacing: undefined,
    fontFamily: undefined,
    textTransform: undefined,
    textStyle: undefined,
  };

  const textItemLayoutStyle = {
    textAlign: 'left',
    color: 'darkGrey1',
    flex: 1,
    ...titleTextLayoutOverwriteStyles,
  } as FlexProps;

  const buttonStyle = { ...getAccordionButtonStyle(bgColor), ...accordionOverwriteStyles?.button };
  const panelLayoutStyle = {
    color: 'darkGrey1',
    p: '0 var(--chakra-space-3xl) var(--chakra-space-md) var(--chakra-space-sm)',
    pb: 'var(--chakra-space-md)',
    ...accordionOverwriteStyles?.panel,
  };

  const panelLegacyTypography = {
    lineHeight: accordionOverwriteStyles?.panel?.lineHeight ?? '3',
  };

  const itemStyle = { ...accordionItemStyle, ...accordionOverwriteStyles?.item };
  const iconStyle = { ...accordionIconStyle, ...accordionOverwriteStyles?.icon };

  return (
    <AccordionChakra
      allowMultiple={allowMultiple}
      reduceMotion
      allowToggle={allowToggle}
      {...accordionOverwriteStyles?.container}
    >
      {accordionItems.map((item: AccordionItemProp, index) => {
        const itemTitle = item?.title?.toString().split(' ').join('_');
        return (
          <AccordionItem {...itemStyle} key={itemTitle} id={`heading-${index}`}>
            <AccordionButton
              {...buttonStyle}
              data-testid={`Button-${itemTitle}`}
              onClick={item.onToggleSection}
            >
              <Flex
                {...textItemLayoutStyle}
                {...getTypographyProps(textItemLegacyTypography, accordionTitleSemanticTypography)}
              >
                {item.title}
              </Flex>
              <AccordionIcon {...iconStyle} />
            </AccordionButton>
            <AccordionPanel
              {...panelLayoutStyle}
              {...getTypographyProps(panelLegacyTypography, accordionPanelSemanticTypography)}
              bgColor={bgColor}
            >
              {addListPadding(item.content)}
            </AccordionPanel>
          </AccordionItem>
        );
      })}
    </AccordionChakra>
  );
}

function addListPadding(node: React.ReactNode): React.ReactNode {
  if (!node) return node;
  if (Array.isArray(node)) {
    return node.map(addListPadding);
  }
  if (typeof node === 'object' && node !== null) {
    if (['ol', 'ul'].includes((node as any).type)) {
      return React.cloneElement(
        node as React.ReactElement,
        {
          style: { paddingLeft: 'var(--chakra-space-4)' } as React.CSSProperties,
        } as any,
        addListPadding((node as any).props?.children)
      );
    }

    return React.cloneElement(
      node as React.ReactElement,
      {},
      addListPadding((node as any).props?.children)
    );
  }
  return node;
}

const accordionItemStyle = {
  cursor: 'pointer',
  fontSize: 'md',
  lineHeight: '3',
  borderBottom: '1px solid var(--chakra-colors-lightGrey4)',
  _hover: {
    bgColor: 'baseWhite',
  },
};

const accordionTitleSemanticTypography = {
  textStyle: 'body-l-emphasis',
};

const getAccordionButtonStyle = (bgColor: string) => ({
  p: 'var(--chakra-space-md) 0 var(--chakra-space-md) 0',
  bgColor: 'baseWhite',
  _focus: {
    boxShadow: 'none',
  },
  _expanded: {
    bgColor: bgColor,
  },
  _hover: {
    bgColor: 'baseWhite',
  },
});

const accordionPanelSemanticTypography = {
  textStyle: 'body-m-regular',
};

const accordionIconStyle = {
  color: 'darkGrey2',
  fontSize: '1.875rem',
};
