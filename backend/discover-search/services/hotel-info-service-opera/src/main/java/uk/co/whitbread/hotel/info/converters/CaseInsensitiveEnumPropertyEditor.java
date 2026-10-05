package uk.co.whitbread.hotel.info.converters;


import java.beans.PropertyEditorSupport;
import java.util.Arrays;

public class CaseInsensitiveEnumPropertyEditor<T extends Enum> extends PropertyEditorSupport {

    private final Class<T> enumType;
    private final String errorMessage;

    public CaseInsensitiveEnumPropertyEditor(Class<T> enumType) {
        super();
        this.enumType = enumType;
        this.errorMessage = String.format("must be an acceptable value. Acceptable values: %s", Arrays.toString(enumType.getEnumConstants()));
    }

    @Override
    public void setAsText(final String text) {
        try {
            setValue(Enum.valueOf(enumType, text.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}