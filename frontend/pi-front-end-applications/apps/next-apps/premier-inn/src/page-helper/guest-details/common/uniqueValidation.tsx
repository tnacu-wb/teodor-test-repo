import { FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import * as yup from 'yup';

export default function checkIsUnique() {
  yup.addMethod(yup.array, 'unique', function (field, message) {
    return this.test('unique', message, function (array): boolean | yup.ValidationError {
      if (array) {
        const errors: yup.ValidationError[] = [];
        array
          .filter((field) => {
            return (
              field.firstName?.length > FORM_VALIDATIONS.FIRST_NAME.MIN &&
              field.lastName?.length > FORM_VALIDATIONS.LAST_NAME.MIN
            );
          })
          .map((field) => {
            return `${field.title}${field.firstName}${field.lastName}`;
          })
          .forEach((value, index, array) => {
            const isDuplicated = array.indexOf(value) !== array.lastIndexOf(value);

            if (isDuplicated) {
              errors.push(
                this.createError({
                  path: `${this.path}.${index}.${field}`,
                  message,
                })
              );
            }
          });

        if (errors.length > 0) {
          return new yup.ValidationError(errors);
        }

        return true;
      }

      return true;
    });
  });
}
