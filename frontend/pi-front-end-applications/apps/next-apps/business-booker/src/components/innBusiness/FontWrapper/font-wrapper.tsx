'use client';

import { Fonts } from '@whitbread-eos/atoms';
import React from 'react';

type FontWrapperProps = {
  baseUrl: string;
};

const FontWrapper = ({ baseUrl }: FontWrapperProps) => {
  return <Fonts baseUrl={baseUrl} />;
};

export default FontWrapper;
