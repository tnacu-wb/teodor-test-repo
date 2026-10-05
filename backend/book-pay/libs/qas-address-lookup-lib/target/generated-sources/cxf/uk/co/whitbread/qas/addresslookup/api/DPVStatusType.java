
package uk.co.whitbread.qas.addresslookup.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for DPVStatusType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="DPVStatusType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="DPVNotConfigured"/&gt;
 *     &lt;enumeration value="DPVConfigured"/&gt;
 *     &lt;enumeration value="DPVConfirmed"/&gt;
 *     &lt;enumeration value="DPVConfirmedMissingSec"/&gt;
 *     &lt;enumeration value="DPVNotConfirmed"/&gt;
 *     &lt;enumeration value="DPVLocked"/&gt;
 *     &lt;enumeration value="DPVSeedHit"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "DPVStatusType")
@XmlEnum
public enum DPVStatusType {

    @XmlEnumValue("DPVNotConfigured")
    DPV_NOT_CONFIGURED("DPVNotConfigured"),
    @XmlEnumValue("DPVConfigured")
    DPV_CONFIGURED("DPVConfigured"),
    @XmlEnumValue("DPVConfirmed")
    DPV_CONFIRMED("DPVConfirmed"),
    @XmlEnumValue("DPVConfirmedMissingSec")
    DPV_CONFIRMED_MISSING_SEC("DPVConfirmedMissingSec"),
    @XmlEnumValue("DPVNotConfirmed")
    DPV_NOT_CONFIRMED("DPVNotConfirmed"),
    @XmlEnumValue("DPVLocked")
    DPV_LOCKED("DPVLocked"),
    @XmlEnumValue("DPVSeedHit")
    DPV_SEED_HIT("DPVSeedHit");
    private final String value;

    DPVStatusType(String v) {
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
    public static DPVStatusType fromValue(String v) {
        for (DPVStatusType c: DPVStatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
