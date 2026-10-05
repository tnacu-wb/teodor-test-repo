import { Box, Collapse as ChakraCollapse, type CollapseProps, Link } from '@chakra-ui/react';
import { formatDataTestId, renderSanitizedHtml } from '@whitbread-eos/utils';
import React, { useEffect, useRef, useState } from 'react';

type CollapseWithChildrenProps = React.PropsWithChildren<CollapseProps>;
const Collapse = ChakraCollapse as unknown as React.ForwardRefExoticComponent<
  CollapseWithChildrenProps & React.RefAttributes<HTMLDivElement>
>;

interface Props extends CollapseProps {
  startingHeight: number;
  noOfLines: number;
  expandButtonText: string;
  collapseButtonText: string;
  baseTestId: string;
  contentText: string;
  isFadeEffect?: boolean;
  isHtml?: boolean;
}

export default function CollapseExpandText(props: Readonly<Props>) {
  const baseTestId = props.baseTestId;

  const [isDescriptionExpanded, setIsDescriptionExpanded] = useState(false);
  const [isExpandLinkClicked, setIsExpandLinkClicked] = useState(false);
  const [collapseHeight, setCollapseHeight] = useState(props.startingHeight);
  const [isTextHeightClamped, setIsTextHeightClamped] = useState(false);

  const collapseRef = useRef<HTMLDivElement>(null);
  const descriptionRef = useRef<HTMLDivElement>(null);

  const handleToggleExpand = () => {
    setIsExpandLinkClicked(true);
    setIsDescriptionExpanded((prev) => !prev);
  };

  useEffect(() => {
    const descriptionEl = descriptionRef.current;
    const collapseEl = collapseRef.current;

    if (descriptionEl?.scrollHeight) {
      const nextHeight =
        descriptionEl.scrollHeight <= collapseHeight ? descriptionEl.scrollHeight : collapseHeight;
      if (nextHeight !== collapseHeight) setCollapseHeight(nextHeight);
    }

    if (collapseEl?.scrollHeight) {
      const shouldClamp =
        (descriptionEl?.scrollHeight as number) > collapseEl.scrollHeight || isExpandLinkClicked;
      if (shouldClamp !== isTextHeightClamped) setIsTextHeightClamped(shouldClamp);
    }
  }, [collapseHeight, isExpandLinkClicked, isTextHeightClamped, props.contentText]);

  return (
    <Box {...descriptionWrapperStyle} data-testid={formatDataTestId(baseTestId, 'Content')}>
      <Box ref={collapseRef}>
        <Collapse startingHeight={collapseHeight} in={isDescriptionExpanded}>
          <Box
            {...descriptionStyle}
            data-testid={formatDataTestId(baseTestId, 'Description')}
            ref={descriptionRef}
            style={{
              WebkitLineClamp: isDescriptionExpanded ? ('unset' as any) : props.noOfLines,
              WebkitBoxOrient: 'vertical' as any,
              ...descriptionInlineStyle,
            }}
            className={props.isHtml ? 'formatLinks' : ''}
          >
            {props.isHtml ? renderSanitizedHtml(props.contentText) : props.contentText}
          </Box>
        </Collapse>
      </Box>

      {props.isFadeEffect && (
        <Box
          mt="-5rem"
          height="5rem"
          position="relative"
          display={isTextHeightClamped && !isDescriptionExpanded ? 'block' : 'none'}
          bg={`${
            isTextHeightClamped &&
            !isDescriptionExpanded &&
            props.contentText.length > 200 &&
            'linear-gradient(to bottom, var(--chakra-colors-fadedBaseWhite) 0%, var(--chakra-colors-baseWhite) 75%)'
          }`}
        />
      )}

      <Link
        as="button"
        data-testid={formatDataTestId(baseTestId, 'Expand-Collapse')}
        display={isTextHeightClamped ? 'block' : 'none'}
        {...linkStyle}
        onClick={handleToggleExpand}
      >
        {isDescriptionExpanded ? props.collapseButtonText : props.expandButtonText}
      </Link>
    </Box>
  );
}

const descriptionStyle = {
  fontSize: 'lg',
  fontWeight: 'normal',
};

const descriptionInlineStyle = {
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  display: '-webkit-box',
};

const linkStyle = {
  textDecoration: 'underline',
  color: 'tertiary',
  _focus: {
    outline: 'none',
  },
  fontSize: 'md',
  fontWeight: 'normal',
  marginTop: 'var(--chakra-space-xs)',
};

const descriptionWrapperStyle = {
  marginBottom: 'var(--chakra-space-lg)',
  marginTop: 'var(--chakra-space-lg)',
};
