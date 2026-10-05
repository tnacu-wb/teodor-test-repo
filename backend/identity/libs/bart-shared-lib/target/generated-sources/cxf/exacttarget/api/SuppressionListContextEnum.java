
package exacttarget.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for SuppressionListContextEnum&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="SuppressionListContextEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Enterprise"/&gt;
 *     &lt;enumeration value="BusinessUnit"/&gt;
 *     &lt;enumeration value="SendClassification"/&gt;
 *     &lt;enumeration value="Send"/&gt;
 *     &lt;enumeration value="Global"/&gt;
 *     &lt;enumeration value="SenderProfile"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "SuppressionListContextEnum")
@XmlEnum
public enum SuppressionListContextEnum {

    @XmlEnumValue("Enterprise")
    ENTERPRISE("Enterprise"),
    @XmlEnumValue("BusinessUnit")
    BUSINESS_UNIT("BusinessUnit"),
    @XmlEnumValue("SendClassification")
    SEND_CLASSIFICATION("SendClassification"),
    @XmlEnumValue("Send")
    SEND("Send"),
    @XmlEnumValue("Global")
    GLOBAL("Global"),
    @XmlEnumValue("SenderProfile")
    SENDER_PROFILE("SenderProfile");
    private final String value;

    SuppressionListContextEnum(String v) {
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
    public static SuppressionListContextEnum fromValue(String v) {
        for (SuppressionListContextEnum c: SuppressionListContextEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
