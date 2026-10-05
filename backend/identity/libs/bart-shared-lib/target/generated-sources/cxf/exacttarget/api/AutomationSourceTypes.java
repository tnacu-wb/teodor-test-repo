
package exacttarget.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for AutomationSourceTypes&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="AutomationSourceTypes"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Unknown"/&gt;
 *     &lt;enumeration value="FileTrigger"/&gt;
 *     &lt;enumeration value="UserInterface"/&gt;
 *     &lt;enumeration value="UserAPI"/&gt;
 *     &lt;enumeration value="RESTApi"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "AutomationSourceTypes")
@XmlEnum
public enum AutomationSourceTypes {

    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),
    @XmlEnumValue("FileTrigger")
    FILE_TRIGGER("FileTrigger"),
    @XmlEnumValue("UserInterface")
    USER_INTERFACE("UserInterface"),
    @XmlEnumValue("UserAPI")
    USER_API("UserAPI"),
    @XmlEnumValue("RESTApi")
    REST_API("RESTApi");
    private final String value;

    AutomationSourceTypes(String v) {
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
    public static AutomationSourceTypes fromValue(String v) {
        for (AutomationSourceTypes c: AutomationSourceTypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
