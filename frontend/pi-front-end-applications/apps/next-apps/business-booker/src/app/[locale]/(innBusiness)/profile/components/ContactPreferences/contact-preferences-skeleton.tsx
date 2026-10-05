'use client';

import { Skeleton } from '@whitbread-eos/atoms/ui';

const BrandListSkeleton = () => (
  <div className={brandListContainerStyle}>
    {[1, 2, 3].map((index) => (
      <div key={index} className={brandItemStyle} data-testid="brand-item">
        <div className={brandIconContainerStyle}>
          <Skeleton className={brandIconStyle} />
        </div>
        <Skeleton className={brandNameSkeletonStyle} />
      </div>
    ))}
  </div>
);

const CheckboxSectionSkeleton = () => (
  <div className={checkboxSectionStyle}>
    <div className={checkboxHeaderStyle}>
      <Skeleton className={checkboxTitleSkeletonStyle} />
      <Skeleton className={checkboxDescriptionSkeletonStyle} />
    </div>
    <div className={checkboxWrapperStyle}>
      <Skeleton className={checkboxSkeletonStyle} />
      <Skeleton className={checkboxLabelSkeletonStyle} />
    </div>
  </div>
);

export function ContactPreferencesSkeleton() {
  return (
    <div className={containerStyle} data-testid="ContactPreferencesSkeleton">
      <div className={backButtonContainerStyle}>
        <Skeleton className={backIconSkeletonStyle} />
        <Skeleton className={backTextSkeletonStyle} />
      </div>

      <div className={headerContainerStyle}>
        <Skeleton className={titleSkeletonStyle} />
        <Skeleton className={descriptionSkeletonStyle} />
      </div>

      <div className={infoContainerStyle}>
        <Skeleton className={descriptionSkeletonStyle} />
        <div className={listContainerStyle}>
          <Skeleton className={listItemSkeletonStyle} />
          <Skeleton className={listItemSkeletonStyle} />
        </div>
      </div>

      <div className={emailContainerStyle}>
        <Skeleton className={emailTitleSkeletonStyle} />
        <Skeleton className={descriptionSkeletonStyle} />
        <Skeleton className={emailValueSkeletonStyle} />
      </div>

      <div className={sectionStyle}>
        <CheckboxSectionSkeleton />
        <BrandListSkeleton />
      </div>

      <div className={middleSectionStyle}>
        <CheckboxSectionSkeleton />
        <BrandListSkeleton />
      </div>

      <div className={bottomSectionStyle}>
        <CheckboxSectionSkeleton />
      </div>

      <div className={buttonContainerStyle}>
        <Skeleton className={submitButtonSkeletonStyle} />
        <Skeleton className={cancelButtonSkeletonStyle} />
      </div>
    </div>
  );
}

// Container styles
const containerStyle = 'px-12 pt-12 mobile:min-w-full mobile:px-4';
const brandListContainerStyle = 'w-full md:w-[310px] grid grid-cols-1 gap-4';
const checkboxSectionStyle = 'flex-1 flex flex-col gap-6';
const headerContainerStyle = 'w-full md:w-[620px]';
const infoContainerStyle = 'w-full md:w-[620px] mb-6 md:mb-8';
const emailContainerStyle = 'w-full md:w-[620px] mb-6 md:mb-8';
const buttonContainerStyle = 'space-y-4 w-full';

// Section styles
const sectionStyle = 'p-6 border border-lightGrey4 flex gap-10 flex-col md:flex-row';
const middleSectionStyle =
  'p-6 border border-lightGrey4 bg-lightGrey5 border-b-0 border-t-0 rounded-b-none mb-0 flex-col md:flex-row flex gap-10';
const bottomSectionStyle = 'p-6 border border-lightGrey4 bg-lightGrey5 rounded-t-none mb-8';

// Brand list styles
const brandItemStyle = 'flex items-center gap-3';
const brandIconContainerStyle = 'relative w-10 h-10';
const brandIconStyle = 'w-10 h-10 rounded-full';
const brandNameSkeletonStyle = 'h-4 w-32';

// Checkbox section styles
const checkboxHeaderStyle = 'flex flex-col gap-2';
const checkboxWrapperStyle = 'flex items-center gap-2 mb-4';
const checkboxTitleSkeletonStyle = 'h-6 w-48';
const checkboxDescriptionSkeletonStyle = 'h-4 w-full';
const checkboxSkeletonStyle = 'w-5 h-5';
const checkboxLabelSkeletonStyle = 'h-4 w-64';

// Back button styles
const backButtonContainerStyle = 'flex items-center text-secondaryColor group mb-6 md:mb-8';
const backIconSkeletonStyle = 'w-4 h-4';
const backTextSkeletonStyle = 'ml-2 h-4 w-16';

// Title and description styles
const titleSkeletonStyle = 'h-10 w-72 mb-4';
const descriptionSkeletonStyle = 'h-4 w-full';
const listContainerStyle = 'pl-5 md:pl-8 mt-4 space-y-2';
const listItemSkeletonStyle = 'h-4 w-full';
const emailTitleSkeletonStyle = 'h-6 w-48 mb-3';
const emailValueSkeletonStyle = 'h-4 w-48 mt-4';

// Button styles
const submitButtonSkeletonStyle = 'w-full md:w-[298px] h-14';
const cancelButtonSkeletonStyle = 'h-4 w-16';
