'use client';

import { useRef, useCallback, RefObject, useReducer, useEffect } from 'react';

import { SCROLL_AMOUNTS } from '../global-constants';

interface UseHorizontalScrollResult<T extends HTMLElement> {
  scrollRef: RefObject<T | null>;
  scrollToLeft: () => void;
  scrollToRight: () => void;
  canScrollLeft: boolean;
  canScrollRight: boolean;
  hasOverflow: boolean;
  checkScrollButtons: () => void;
}

const SET_SCROLL_STATE = 'SET_SCROLL_STATE';

interface ScrollState {
  canScrollLeft: boolean;
  canScrollRight: boolean;
  hasOverflow: boolean;
}

type ScrollAction = {
  type: typeof SET_SCROLL_STATE;
  payload: Partial<ScrollState>;
};

const initialState: ScrollState = {
  canScrollLeft: false,
  canScrollRight: false,
  hasOverflow: false,
};

function scrollReducer(state: ScrollState, action: ScrollAction): ScrollState {
  switch (action.type) {
    case SET_SCROLL_STATE:
      return { ...state, ...action.payload };
    default:
      return state;
  }
}

export const useHorizontalScroll = <T extends HTMLElement>(
  scrollAmount = SCROLL_AMOUNTS.monthTab
): UseHorizontalScrollResult<T> => {
  const scrollRef = useRef<T>(null);

  const [state, dispatch] = useReducer(scrollReducer, initialState);
  const { canScrollLeft, canScrollRight, hasOverflow } = state;

  const checkScrollButtons = useCallback(() => {
    const el = scrollRef.current;
    if (!el) return;

    const { scrollLeft, scrollWidth, clientWidth } = el;
    dispatch({
      type: SET_SCROLL_STATE,
      payload: {
        canScrollLeft: scrollLeft > 0,
        canScrollRight: scrollLeft < scrollWidth - clientWidth,
        hasOverflow: scrollWidth > clientWidth,
      },
    });
  }, []);

  useEffect(() => {
    const el = scrollRef.current;
    if (!el) return;

    checkScrollButtons();

    const handleResize = () => checkScrollButtons();
    const handleScroll = () => checkScrollButtons();

    el.addEventListener('scroll', handleScroll);
    window.addEventListener('resize', handleResize);

    return () => {
      el.removeEventListener('scroll', handleScroll);
      window.removeEventListener('resize', handleResize);
    };
  }, [checkScrollButtons]);

  const scrollToLeft = useCallback(() => {
    scrollRef.current?.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
  }, [scrollAmount]);

  const scrollToRight = useCallback(() => {
    scrollRef.current?.scrollBy({ left: scrollAmount, behavior: 'smooth' });
  }, [scrollAmount]);

  return {
    scrollRef,
    scrollToLeft,
    scrollToRight,
    canScrollLeft,
    canScrollRight,
    hasOverflow,
    checkScrollButtons,
  };
};
