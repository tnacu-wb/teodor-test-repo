import '@testing-library/jest-dom';
import React from 'react';
import * as yup from 'yup';

import { render, userEvent, waitFor } from '../../utils/test-utils';
import { FormWithAccordian } from './FormWithAccordian.component';
import { FORM_BUTTON_TYPES, FORM_FIELD_TYPES } from './formConstants';
import { FormWithAccordianProps } from './formTypes';

const handleAccordionToggle = jest.fn();

const props: FormWithAccordianProps = {
  defaultValues: {},
  validationSchema: yup.object().shape({
    firstName: yup
      .string()
      .required('Please enter a first name')
      .min(10, 'groupBooking.contactDetails.yourContact.firstName.min'),
  }),
  accordionIndex: 0,
  handleAccordionToggle,
  accordians: [
    {
      title: 'groupBooking.contactDetails.title',
      onToggleSection: () => handleAccordionToggle(0),
      elements: {
        fields: [
          {
            type: FORM_FIELD_TYPES.DROPDOWN,
            name: 'title',
            dropdownOptions: [{ id: 'Mr', label: 'Mr' }],
            testid: 'title',
            styles: { maxW: '8.5rem', mt: '2xl' },
            label: 'groupBooking.contactDetails.yourContact.nameTitles.placeholder',
          },
          {
            type: FORM_FIELD_TYPES.INPUT_TEXT,
            name: 'firstName',
            label: 'groupBooking.contactDetails.yourContact.firstName',
            testid: 'firstName',
            props: {
              className: 'sessioncamhidetext',
            },
          },
          {
            label: '',
            type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
            name: 'yourContactDetails',
            content: 'groupBooking.contactDetails.yourContact.title',
          },
          {
            name: 'ContinueButton',
            label: 'groupBooking.contactDetails.buttonLabel',
            type: FORM_BUTTON_TYPES.BUTTON,
            action: jest.fn(),
            props: {
              variant: 'primary',
              size: 'full',
            },
            testid: 'ContinueButton',
          },
        ],
        buttons: [],
      },
    },
    {
      title: 'groupBooking.bookingDetails.title',
      onToggleSection: () => handleAccordionToggle(1),
      elements: {
        fields: [
          {
            type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
            name: 'bookerTypeTitle',
            content: 'groupBooking.bookingDetails.bookingType.title',
            label: '',
          },
          {
            type: FORM_FIELD_TYPES.RADIO_GROUP,
            name: 'purposeTitle',
            options: [
              {
                value: 'personalAddress',
                label: 'Personal Address',
              },
              {
                value: 'companyAddress',
                label: 'Company Address',
              },
            ],
            label: '',
          },
          {
            type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
            name: 'purposeTitle',
            content: 'groupBooking.bookingDetails.purposeOfStay.title',
            label: '',
          },
          {
            name: 'SubmitButton',
            type: FORM_BUTTON_TYPES.SUBMIT,
            label: 'groupBooking.submit.button.label',
            props: {
              variant: 'primary',
              size: 'full',
            },
            testid: 'SubmitButton',
          },
        ],
        buttons: [],
      },
    },
  ],
};

describe('FormWithAccordian', () => {
  const Component = () => {
    return <FormWithAccordian {...props} />;
  };

  it('should render the component', () => {
    const { getByText } = render(<Component />);
    const accordionItem1 = getByText('groupBooking.contactDetails.title');
    const accordionItem2 = getByText('groupBooking.bookingDetails.title');
    expect(accordionItem1).toBeInTheDocument();
    expect(accordionItem2).toBeInTheDocument();
  });

  it('should call standard button action if form valid', async () => {
    const { getByTestId, getByText, queryByText } = render(<Component />);
    const fnameFld = getByTestId('firstName');
    const button = getByText('groupBooking.contactDetails.buttonLabel');

    await userEvent.type(fnameFld, 'xxxxxxxxxxxxxx');
    await waitFor(() => {
      userEvent.click(button);
    });

    await waitFor(() => {
      const logSpy = jest.spyOn(global.console, 'error');
      expect(logSpy).not.toHaveBeenCalled();
    });
    await waitFor(() => {
      expect(
        queryByText('groupBooking.contactDetails.yourContact.firstName.min')
      ).not.toBeInTheDocument();
    });
  });

  it('should not call standard button action if form invalid ', async () => {
    const { getByTestId, getByText } = render(<Component />);
    const fnameFld = getByTestId('input-firstName');
    const button = getByText('groupBooking.contactDetails.buttonLabel');

    waitFor(() => {
      userEvent.type(fnameFld, 'ZZ');
    });

    await waitFor(() => {
      userEvent.click(button);
    });

    await waitFor(() => {
      expect(
        getByText('groupBooking.contactDetails.yourContact.firstName.min')
      ).toBeInTheDocument();
    });
  });
});

describe('FormWithAccordian without validationSchema set', () => {
  const Component = () => {
    props.validationSchema = undefined;
    return <FormWithAccordian {...props} />;
  };

  it('should not call standard button action  ', async () => {
    const { getByTestId, getByText, queryByText } = render(<Component />);
    const fnameFld = getByTestId('firstName');
    const button = getByText('groupBooking.contactDetails.buttonLabel');

    await userEvent.type(fnameFld, 'xxx');
    await waitFor(() => {
      userEvent.click(button);
    });

    await waitFor(() => {
      expect(
        queryByText('groupBooking.contactDetails.yourContact.firstName.min')
      ).not.toBeInTheDocument();
    });
  });
});
