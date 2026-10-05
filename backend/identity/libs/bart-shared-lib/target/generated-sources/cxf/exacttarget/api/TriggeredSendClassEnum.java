
package exacttarget.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for TriggeredSendClassEnum&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="TriggeredSendClassEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Standard"/&gt;
 *     &lt;enumeration value="SMTPRestV1"/&gt;
 *     &lt;enumeration value="SMTPRestV2"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "TriggeredSendClassEnum")
@XmlEnum
public enum TriggeredSendClassEnum {

    @XmlEnumValue("Standard")
    STANDARD("Standard"),
    @XmlEnumValue("SMTPRestV1")
    SMTP_REST_V_1("SMTPRestV1"),
    @XmlEnumValue("SMTPRestV2")
    SMTP_REST_V_2("SMTPRestV2");
    private final String value;

    TriggeredSendClassEnum(String v) {
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
    public static TriggeredSendClassEnum fromValue(String v) {
        for (TriggeredSendClassEnum c: TriggeredSendClassEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
