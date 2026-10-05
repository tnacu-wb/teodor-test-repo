package uk.co.whitbread.contentservice.roomtypes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.whitbread.contentservice.roomtypes.model.RoomTypesResponse;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRoomType;
import uk.co.whitbread.contentservice.roomtypes.service.client.AEMRoomTypesService;
import uk.co.whitbread.contentservice.roomtypes.util.AEMResponseConverter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomTypesService {

    private final AEMRoomTypesService aemRoomTypesService;
    private final AEMResponseConverter aemResponseConverter;

    /**
     * Method responsible for getting all the room types details for a given country, language and brand:
     * Calls AEM to get the model.json for the brand page
     * Converts the AEM response (json with Objects) to MS response (json with Strings)
     *
     * @param country  country could be gb/de
     * @param language language could be en/de
     * @param brand    brand could be pi,hub,zip,pid
     * @return RoomTypesResponse
     */
    @Cacheable(cacheNames = "contentService-allRoomTypes", unless = "#result == null")
    public RoomTypesResponse getAllRoomTypes(String country, String language, String brand) {
        RoomTypesResponse roomTypesResponse = new RoomTypesResponse();
        // Call AEM to get room types for the given country,language and brand
        List<AEMRoomType> aemRoomTypes = aemRoomTypesService.getRoomTypes(country, language, brand);
        roomTypesResponse.setRoomTypes(aemResponseConverter.convertAEMResponse(aemRoomTypes));
        return roomTypesResponse;
    }

    /**
     * Method responsible for getting the specific room type details for a given country, language and brand:
     * Calls AEM to get the model.json for the brand page
     * Converts the AEM response (json with Objects) to MS response (json with Strings)
     *
     * @param country      country could be gb/de
     * @param language     language could be en/de
     * @param brand        brand could be pi,hub,zip,pid
     * @param roomTypeCode roomTypeCode could be sb/db/twin etc.
     * @return RoomTypesResponse
     */
    @Cacheable(cacheNames = "contentService-roomTypesForCode", unless = "#result == null")
    public RoomTypesResponse getRoomTypesForCode(String country, String language, String brand, String roomTypeCode) {
        RoomTypesResponse roomTypesResponse = new RoomTypesResponse();
        // Call AEM to get specific room type for the given room type code
        List<AEMRoomType> aemRoomTypes = aemRoomTypesService.getRoomTypes(country, language, brand);
        roomTypesResponse.setRoomTypes(aemResponseConverter.getRoomTypesForCode(aemRoomTypes, roomTypeCode));
        return roomTypesResponse;
    }
}