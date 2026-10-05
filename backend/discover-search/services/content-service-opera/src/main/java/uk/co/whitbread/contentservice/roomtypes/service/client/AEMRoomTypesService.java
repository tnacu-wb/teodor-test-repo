package uk.co.whitbread.contentservice.roomtypes.service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.contentservice.roomtypes.client.feign.AEMFeignClient;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignProperties;
import uk.co.whitbread.contentservice.roomtypes.exception.NoDataFoundException;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRoomType;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRoomTypesResponse;
import uk.co.whitbread.contentservice.roomtypes.model.aem.ContentFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Component
public class AEMRoomTypesService {
    private static final String ERROR_MESSAGE = "AEM room types not found.";
    private final AEMFeignClient aemFeignClient;
    private final FeignProperties feignProperties;

    /**
     * Method responsible for getting all the room types details for a given country, language and brand:
     * Calls AEM to get the model.json for the brand page
     *
     * @param country  country could be gb/de
     * @param language language could be en/de
     * @param brand    brand could be pi,hub,zip,pid
     * @return List<AEMRoomType></AEMRoomType>
     */
    public List<AEMRoomType> getRoomTypes(String country, String language, String brand) {
        List<AEMRoomType> aemRoomTypes = new ArrayList<>();
        try {
            AEMRoomTypesResponse roomTypesJson = aemFeignClient.getRoomTypes(country, language, brand, feignProperties.getAem().getResource());

            Map<String, ContentFragment> contentFragments = roomTypesJson.getItems().getRoot().getItems();

            if (!contentFragments.isEmpty()) {
                contentFragments.entrySet().stream().forEach(contentFragment -> {
                    AEMRoomType aemRoomType = contentFragment.getValue().getElements();
                    aemRoomTypes.add(aemRoomType);
                });

            }
        } catch (Exception exception) {
            throw new NoDataFoundException(ERROR_MESSAGE);
        }

        return aemRoomTypes;
    }

}
