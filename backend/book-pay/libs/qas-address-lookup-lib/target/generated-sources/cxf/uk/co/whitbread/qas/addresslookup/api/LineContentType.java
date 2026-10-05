
package uk.co.whitbread.qas.addresslookup.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for LineContentType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="LineContentType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="None"/&gt;
 *     &lt;enumeration value="Address"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="Ancillary"/&gt;
 *     &lt;enumeration value="DataPlus"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "LineContentType")
@XmlEnum
public enum LineContentType {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("Address")
    ADDRESS("Address"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("Ancillary")
    ANCILLARY("Ancillary"),
    @XmlEnumValue("DataPlus")
    DATA_PLUS("DataPlus");
    private final String value;

    LineContentType(String v) {
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
    public static LineContentType fromValue(String v) {
        for (LineContentType c: LineContentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
