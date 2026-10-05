package uk.co.whitbread.hotel.info.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class CaseInsensitiveEnumPropertyEditorTest {

    private Class<TestEnum> typeParameterClass = TestEnum.class;
    private CaseInsensitiveEnumPropertyEditor<TestEnum> caseInsensitiveEnumPropertyEditor;

    @BeforeEach
    public void setup() {

        caseInsensitiveEnumPropertyEditor = new CaseInsensitiveEnumPropertyEditor<>(typeParameterClass);
    }

    @Test
    public void testUpperCase() {

        caseInsensitiveEnumPropertyEditor.setAsText("upper");
        Object result = caseInsensitiveEnumPropertyEditor.getValue();

        assertThat("UPPER", result, is(TestEnum.UPPER));
    }

    @Test
    public void testThrowExceptionWhenInvalidValue() {

        assertThrows(IllegalArgumentException.class,
                () -> caseInsensitiveEnumPropertyEditor.setAsText("notanenumvalue"),
                "must be an acceptable value. Acceptable values: [UPPER]");
    }


    private enum TestEnum {
        UPPER
    }
}