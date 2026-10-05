package uk.co.whitbread.hotel.register.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.register.properties.RegisterProperties;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EncryptionServiceTest {
    private final String KEY = "@SnFUFU-TNJQZiV9BxL4@oNjnUFji3Onfgsnb5@k8Q0N+iqtCgjrJh@2n6WNRX-vkfv6QCNnz4vpGrBHKxgawlBEbfGmV7E9_zEJvKwp-oy6Jc6pFC7-ekXNCprWCRf4R3+H_q0pHgMD7NC9GM@O1L0=E5psbD5sqygdWoZd0@0QFGJa8EFkjJtwpgZPDo6@Zd-=Su8A@WRrMHzonRgz3Lb=kRcnXpqWTV0DZoa_vDObQuH_+RhZwJ3+m@+5kE=1yIWvM4Aiz_DsbWA9rtXL@oJTfzgv5*300i3XBP1I*R06FTipcfIO2OUOyNAIq038BOiNH1h4gY8P*8MjqsxRl+GprC7Qu8zrRk1NP=YAOLKFcd82vJ2L0=SwpmTmm-*zExLgtSUCStF1IflrN@g*0*0pUX0084By5jaGl_CVa+B5QDMBZVMkgFLpKG-ysRJF@mHHRyazFaG5JIiHCeTQIufFY=VNF4a38kH+IYN5ye3HG*-5Gjw*nsdrY979-dozLYkweTM8_vh=_=J=*18wXF4qKF4ZsMJxlEm2lPlAo+7rFjX5lTjU5hL4wC=TNn503mcxggld_XMxSgwGTaLWHJ5nuBtBQrX*e7KbrHCQdyM0IDZOP_Nn6iHV6ZD9Q8H+gWeWfP@94D+udaq58FLx7-EX1PoTPo4ycJpI2pN_R-1MAu9X@jxC+t3AbK7gZ*cybmq=DY@CZeD0+la6k8fE2nWtACAp1MSz=RnvZqX6snAg4OwJab4x+cQ99=1GRL@XZ7U31zWhU4xGBs5x-8QFz@LwucMylGMO+ASp@IZR6a5EF1u7PJ2P@E6tfRSyL4M*LUiTyObkqS1xHPjTCjrYqB2hXzZnmL9@=IJwjchU_1WoubZ@NGHQxvWL*j0pLB-ZdP07fyeyC1bR3knCO7PNnqK5S5ndjWEnzS9qlOEFF_jelq@g_haqg7=j_uRwUTaD3GFgcX@eKKG9j8aPY3S-EodnzHgfA*fQA6tcG43MRYyc08Zh*CYxWKfNrN6fP58xjAP2Frc9DW26Y436UU+cgk8xyfI37adW6FxI5DN6*=Q31WngSZ4akwoH86cbFNpg=Gd5t@G0j3Yq8zX=pcvmW7byY*GVGd0DP-c@NxhP1KtSM6qbLf=I-j1wwqzmkknOqu69_wUouRvh7IgbPYlkZj1ARbxQWWGAVCuDGMVPY6wOh+qWG=QkbDKf5TfNWLMQ0R7xo_=_Vw5zFLXjeykxKmY-jxoEB+Z2gt1gq7Oey3WZ@OqnwS6I8un09QSLia0UN1toJZF9nVW6zGfjHzZLrNjRHiy6ydLyyyr8abTpn5SU73To5ZYgsh47biD4zd8gf4-3siHHikJLTdLCPc2TTvuMgr4ody6MTvV5ojEOx+0*U1_ViYXo5IVdiO8XRIBhhjQ3gNnvP3j=O7959FI=EiE*=_Yvi8nsbZM5==iAw3U=wgZLhtneYSNwLhZpq=zDP7lkWF_T3v@285Jtf@BWwo*nTbIFSNS0EkHQrJLtNd3oTuR-E*78o5MSDVBu1kJb9Pp0h8hhX82dwWw0vWQ8crN7bfGOPnNCmYD*Nl_hh1ZysP2absIDlfrKXobrXmxh9xego=buMb9PKKPxAlLG_GG06XtrEUma8CLz_rUToVlkv6AAB@4tog_WBUox=DXy+m0tjQ+FBG99bRDHD7J6gfwz0KIA+@NgBunlRup9TydL951leU=u8+8L9u*5XJncRNLFQgKPT97eza-JAq4V6zoeSxvIsubCgm0*_FAA*MnAJ*+k4OvUaWcErI105Phf7-FbR9=*t3sL0wnLi53=--CaoTARwHN9z8+fkOv211tPMi4pJ4mSi1o*88mJog3WW@GKaEfbePesIGu3OtRqpJRm_08JabLWRk2FldaEyD0@ueHgrMQPZaYWkmd0JBCSUHDEs7hCFAS@*dFRFcxFbzQSD2-QiFCG_SZ@S_XWVWl3PtVI==lNbE+KhWC9Y_fvS2UqvqZzgMU2cuV0yP8vdyFrK2TirJ8x+PfH=uG3o407=Bt8J_+yYNi@@*YkJ3hU";
    private final String SALT = "ilc*47Xu_P44slUZ3rbc1vHHpeDW2kKFmLhNBu@B8S3oMxTWZABim5dH_8jvseYqzdtKCr41c=y@na1-8aY=pV*Fx9vxiXmA6Swumi=OfwWvs1itUwSt9U8=nq29Ey7yaZm5ZTMep7CEpaxq4x@+l+R+cQk9I0PcNHlahkp_jCD1SwwGMDmgHSPnrdrSciho9vH6w0nDUlEcmsl5tGDJLHcKR9L9eJGy0J0J34Tc_Tp4rJSWyPLrDHu1i6FYjKJBTQ0dJtaEaWcL_dOJPv*dV*k9d0xYcZXQIKequkMOoG8gKH-AwHc@+JVUe6nY+*fwfkl0cbI_YpfC0niogY+eTufqX6lhDYuqgOkc+0rDxt1YR3rKSiN+pi514hAHGoX8Ac3oT1whSHjkaJO3lpMYwJys0lHYMATs2vqhuBKwPmBUTt_GDTH6oHD-@6I1WZngRnuHJ40@7WJXDyu*Ypm7w3vRE+7dGtAfmgzy6swQp*gT-fJ6Lj7-I+_mNL-xx9BkKT@1JKXD16S6rvfVcbqnEqsw5WqAHOXrJpHFnEMsjcFQNrpMeZbXTBHi9Mp90vI2MMSyQ+3JDb-iaJk8JXbypSuCHNtEWOr0VIitJn2IgowhjILBShRld_Nv8KJ_6@m09HZKcyOd260A+tuF=BzwuRnW36KaRz-IS9IDPJ8zrMShDyZd-ljI6mwX28uGqdc3w+7hu@r*13w-dxQCfxgcXwkM6Hvi42G_Oxn58MJYu3nvaJGG6Xq7mhik3motwzcPjvz5mpic1lPUDKfJM=VsGUKc*hkyhjwc8TRxBGb-GmmGbAOWvfVlzd3=1cIH9r8=aDeRbtia@FNY_jU7xNLSQ@kZB07EO8MHXTBcuDTz8zClrZ0dZb7+nI4aZXWKmlhxychv+dM-kUbJbHT7JysRF5p_T3Y@AW*qmRW3zv6v+*BE3AygqcVpkFaDk=oMMFksEb*fxFOTlpVqsx9VSf@TQKYnlU2L9bpVMkzJmncAh8Nkpn8Tx3ZH5PabOY7T3akOGhrfDJLvScEFyuWahcJBBWiHWCTD40p7qW+N@zW4brK3mZMmC-_=PPnrBDpO1Vyxjr8CmEjP2MY0mMdot1v@uvGhsMLLFcYoAb5sfbAJsp0eVOaiDzOTZSgk5s9Yvx=iD_n15nBTwCIojlaxO6BH9Y9bwaOWI=9Ffxp2EBD*8wn4OzGz_GvW0r-VPIOoM_AhxAETR8q*vA3dYrwbHYK6Vq*LCOKckjGQzLSam0DS1UAdhCZc2Jm+@XQ3glzRWuAxBvrg=@HgWGnciagDLcQ8JPAbhUKu=Y1KisT22R31OL9iNttpg5qG0Ud@XPct@Iwbex=qNwli270p90va*@=cY+L8-3B4p40=GK0QgHiuVBHO_GNA-HLTEEwm*XzjDWBF1Phy_Bv@SG87kNRKJ0eQ4eJcynlF9kEZ@vI_ucJexAP8pVR4cC9py18sD83xo6fKSRsnSGRzobL2Apsf=_pTIm35Yx5xl_LADU13ihT3cJqi6Mne9tJaOod+BXSpd0i1EMr*@knz93RW7pbj0cuRcbxkdYYe3RsXzmwAyPHM2kkjvfO1x*hQdYPo8mxyzRpiezA33FBwPWIv1fQ3mYdQUB1KAw+71fj8FD14ckMAFPtZ6Vc6b4Pkb4*xoFEFRkGuM7Z0tZG-h0MhaVmYrdqbAKBmwKq0qvaD2MMa25ex3Gk+CU6nO5mrH=xFfy3y6igAlIUc6o54-R4LekcQPKG8ZlfddIMHcm7JC@Pq9zfZlbt3V=ulTB6EAWMkst5FXQoZ1Db4zx8**Y59@IbeTxs2lj8UTvsRCEHZtOQHh+Dk=rY9G-Z5dcl5DFsjzqJ_Fi4h4TMvh6=h5pR*5gbVfsnGh1LtBibZmQJydvG@@dxgPwrGZJ6-fi9uonCunW7OFZWJ8k9T@h@VIdD5V*DE4BLGjI-uELwiV*pVZsAIsIXsW*_lDmUOt@FMIio66M@q6@yW3=G@biQmoj8qFJAMqDKrWjp*ujVqlb2fm88GwEQWIMVKZwD4dFjlSEc9_F5yIXMZ";
    private final String STATIC_IV = "G-4f*3-HGFs-8d6b";

    private RegisterProperties properties;

    @BeforeEach
    public void setup() {
        properties = new RegisterProperties();
        properties.setSecretKey(KEY);
        properties.setSalt(SALT);
        properties.setStaticIV(STATIC_IV);
    }

    @Test
    public void encryptTextSuccessfully() throws Exception {

        String plainText = "email.lastname@gmail.com";
        final String encodeToString = Base64.getEncoder().encodeToString(plainText.getBytes());
        EncryptionService enc = new EncryptionService(properties);
        final String result = enc.encrypt(encodeToString);
        final String deResult = enc.decrypt(result);

        Assertions.assertThat(deResult).isEqualTo(plainText);
    }

    @Test
    public void encryptTextShouldNotBeCaseSensitive() throws Exception {

        String plainText = "email.lastname@gmail.com";
        final String encodeToString = Base64.getEncoder().encodeToString(plainText.getBytes());
        EncryptionService enc = new EncryptionService(properties);
        final String result = enc.encrypt(encodeToString);
        final String deResult = enc.decrypt(result);

        Assertions.assertThat(deResult).isEqualTo(plainText);

        String plainTextUpperCase = "Email.Lastname@Gmail.com";
        final String encodeTextUpperCase = Base64.getEncoder().encodeToString(plainTextUpperCase.getBytes());
        final String upperCaseResult = enc.encrypt(encodeTextUpperCase);

        Assertions.assertThat(upperCaseResult).isEqualTo(result);
    }

    @Test
    public void encryptTextSuccessfullyWithBigText() throws Exception {

        StringBuilder plainText = new StringBuilder("email.lastname@gmail.com");

        for (int i = 0; 10000 > i; i++) {
            plainText.append("email.lastname@gmail.com");
        }
        final String encodeToString = Base64.getEncoder().encodeToString(plainText.toString().getBytes());

        EncryptionService enc = new EncryptionService(properties);
        final String result = enc.encrypt(encodeToString);
        final String deResult = enc.decrypt(result);

        Assertions.assertThat(deResult).isEqualTo(plainText.toString());
    }

    @Test
    public void shouldNotFailIfEncryptTextIsEmpty() throws Exception {

        String plainText = "";
        final String encodeToString = Base64.getEncoder().encodeToString(plainText.toString().getBytes());
        EncryptionService enc = new EncryptionService(properties);
        final String result = enc.encrypt(encodeToString);
        final String deResult = enc.decrypt(result);

        Assertions.assertThat(plainText).isEqualTo(deResult);
    }

    @Test
    public void decryptWithDifferentInitialVector() throws Exception {

        StringBuilder plainText = new StringBuilder("email.lastname@gmail.com");
        final String encodeToString = Base64.getEncoder().encodeToString(plainText.toString().getBytes());
        EncryptionService enc = new EncryptionService(properties);
        final String result = enc.encrypt(encodeToString);
        properties.setStaticIV("abcdefgihjklmnop");

        assertThrows(AEADBadTagException.class, () -> enc.decrypt(result), "Tag mismatch!");
    }

    @Test
    public void decryptWithDifferentSalt() throws Exception {

        StringBuilder plainText = new StringBuilder("email.lastname@gmail.com");
        final String encodeToString = Base64.getEncoder().encodeToString(plainText.toString().getBytes());

        EncryptionService enc = new EncryptionService(properties);
        final String result = enc.encrypt(encodeToString);

        properties.setSalt("e0f0d27a-3f23-4571-8fa1-be50ada3b049e0f0d27a-3f23-4571-8fa1-be50ada3b049e0f0d27a-3f23-4571");

        assertThrows(BadPaddingException.class, () -> enc.decrypt(result),
                "Given final block not properly padded. Such issues can arise if a bad key is used during decryption.");
    }

    @Test
    public void decryptWithDifferentSecretKey() throws Exception {

        StringBuilder plainText = new StringBuilder("email.lastname@gmail.com");
        final String encodeToString = Base64.getEncoder().encodeToString(plainText.toString().getBytes());

        EncryptionService enc = new EncryptionService(properties);
        final String result = enc.encrypt(encodeToString);
        properties.setSecretKey("e0f0d27a-3f23-4571-8fa1-be50ada3b049e0f027a-3f23-4571-8fa1-be50ada3b049e0f0d27a-3f23-4571");

        assertThrows(BadPaddingException.class, () -> enc.decrypt(result),
                "Given final block not properly padded. Such issues can arise if a bad key is used during decryption.");

    }

}