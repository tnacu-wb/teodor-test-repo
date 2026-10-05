
package exacttarget.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for OverrideType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="OverrideType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="DoNotOverride"/&gt;
 *     &lt;enumeration value="Override"/&gt;
 *     &lt;enumeration value="OverrideExceptWhenNull"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "OverrideType")
@XmlEnum
public enum OverrideType {

    @XmlEnumValue("DoNotOverride")
    DO_NOT_OVERRIDE("DoNotOverride"),
    @XmlEnumValue("Override")
    OVERRIDE("Override"),
    @XmlEnumValue("OverrideExceptWhenNull")
    OVERRIDE_EXCEPT_WHEN_NULL("OverrideExceptWhenNull");
    private final String value;

    OverrideType(String v) {
        value = v;
    }

    /**
     * Gets the value associated to the enum constant.
     * 
     * @return
     *     The value linked to the enum.
     */
    public String value() {
        return value;
    }

    /**
     * Gets the enum associated to the value passed as parameter.
     * 
     * @param v
     *     The value to get the enum from.
     * @return
     *     The enum which corresponds to the value, if it exists.
     * @throws IllegalArgumentException
     *     If no value matches in the enum declaration.
     */
    public static OverrideType fromValue(String v) {
        for (OverrideType c: OverrideType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
