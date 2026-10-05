package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.BrandDto;
import uk.co.whitbread.content.entity.service.generated.models.content.OfferDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GlobalDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class GlobalDto {

  private @Nullable String accessible;

  private @Nullable String accessibleOrBarrierFree;

  private @Nullable String addRoom;

  private @Nullable String adult;

  private @Nullable String adults;

  private @Nullable String adultsLabel;

  private @Nullable BrandDto brand;

  private @Nullable String child;

  private @Nullable String children;

  private @Nullable String childrenLabel;

  private @Nullable String done;

  private @Nullable String _double;

  private @Nullable String family;

  private @Nullable String night;

  @Valid
  private List<@Valid OfferDto> offers = new ArrayList<>();

  private @Nullable String room;

  private @Nullable String roomLabel;

  private @Nullable String rooms;

  private @Nullable String single;

  private @Nullable String thirdParties;

  private @Nullable String today;

  private @Nullable String tomorrow;

  private @Nullable String twin;

  public GlobalDto accessible(String accessible) {
    this.accessible = accessible;
    return this;
  }

  /**
   * Get accessible
   * @return accessible
   */
  
  @Schema(name = "accessible", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessible")
  public String getAccessible() {
    return accessible;
  }

  public void setAccessible(String accessible) {
    this.accessible = accessible;
  }

  public GlobalDto accessibleOrBarrierFree(String accessibleOrBarrierFree) {
    this.accessibleOrBarrierFree = accessibleOrBarrierFree;
    return this;
  }

  /**
   * Get accessibleOrBarrierFree
   * @return accessibleOrBarrierFree
   */
  
  @Schema(name = "accessibleOrBarrierFree", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessibleOrBarrierFree")
  public String getAccessibleOrBarrierFree() {
    return accessibleOrBarrierFree;
  }

  public void setAccessibleOrBarrierFree(String accessibleOrBarrierFree) {
    this.accessibleOrBarrierFree = accessibleOrBarrierFree;
  }

  public GlobalDto addRoom(String addRoom) {
    this.addRoom = addRoom;
    return this;
  }

  /**
   * Get addRoom
   * @return addRoom
   */
  
  @Schema(name = "addRoom", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addRoom")
  public String getAddRoom() {
    return addRoom;
  }

  public void setAddRoom(String addRoom) {
    this.addRoom = addRoom;
  }

  public GlobalDto adult(String adult) {
    this.adult = adult;
    return this;
  }

  /**
   * Get adult
   * @return adult
   */
  
  @Schema(name = "adult", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adult")
  public String getAdult() {
    return adult;
  }

  public void setAdult(String adult) {
    this.adult = adult;
  }

  public GlobalDto adults(String adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Get adults
   * @return adults
   */
  
  @Schema(name = "adults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adults")
  public String getAdults() {
    return adults;
  }

  public void setAdults(String adults) {
    this.adults = adults;
  }

  public GlobalDto adultsLabel(String adultsLabel) {
    this.adultsLabel = adultsLabel;
    return this;
  }

  /**
   * Get adultsLabel
   * @return adultsLabel
   */
  
  @Schema(name = "adultsLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adultsLabel")
  public String getAdultsLabel() {
    return adultsLabel;
  }

  public void setAdultsLabel(String adultsLabel) {
    this.adultsLabel = adultsLabel;
  }

  public GlobalDto brand(BrandDto brand) {
    this.brand = brand;
    return this;
  }

  /**
   * Get brand
   * @return brand
   */
  @Valid 
  @Schema(name = "brand", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("brand")
  public BrandDto getBrand() {
    return brand;
  }

  public void setBrand(BrandDto brand) {
    this.brand = brand;
  }

  public GlobalDto child(String child) {
    this.child = child;
    return this;
  }

  /**
   * Get child
   * @return child
   */
  
  @Schema(name = "child", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("child")
  public String getChild() {
    return child;
  }

  public void setChild(String child) {
    this.child = child;
  }

  public GlobalDto children(String children) {
    this.children = children;
    return this;
  }

  /**
   * Get children
   * @return children
   */
  
  @Schema(name = "children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("children")
  public String getChildren() {
    return children;
  }

  public void setChildren(String children) {
    this.children = children;
  }

  public GlobalDto childrenLabel(String childrenLabel) {
    this.childrenLabel = childrenLabel;
    return this;
  }

  /**
   * Get childrenLabel
   * @return childrenLabel
   */
  
  @Schema(name = "childrenLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("childrenLabel")
  public String getChildrenLabel() {
    return childrenLabel;
  }

  public void setChildrenLabel(String childrenLabel) {
    this.childrenLabel = childrenLabel;
  }

  public GlobalDto done(String done) {
    this.done = done;
    return this;
  }

  /**
   * Get done
   * @return done
   */
  
  @Schema(name = "done", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("done")
  public String getDone() {
    return done;
  }

  public void setDone(String done) {
    this.done = done;
  }

  public GlobalDto _double(String _double) {
    this._double = _double;
    return this;
  }

  /**
   * Get _double
   * @return _double
   */
  
  @Schema(name = "double", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("double")
  public String getDouble() {
    return _double;
  }

  public void setDouble(String _double) {
    this._double = _double;
  }

  public GlobalDto family(String family) {
    this.family = family;
    return this;
  }

  /**
   * Get family
   * @return family
   */
  
  @Schema(name = "family", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("family")
  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public GlobalDto night(String night) {
    this.night = night;
    return this;
  }

  /**
   * Get night
   * @return night
   */
  
  @Schema(name = "night", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("night")
  public String getNight() {
    return night;
  }

  public void setNight(String night) {
    this.night = night;
  }

  public GlobalDto offers(List<@Valid OfferDto> offers) {
    this.offers = offers;
    return this;
  }

  public GlobalDto addOffersItem(OfferDto offersItem) {
    if (this.offers == null) {
      this.offers = new ArrayList<>();
    }
    this.offers.add(offersItem);
    return this;
  }

  /**
   * Get offers
   * @return offers
   */
  @Valid 
  @Schema(name = "offers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offers")
  public List<@Valid OfferDto> getOffers() {
    return offers;
  }

  public void setOffers(List<@Valid OfferDto> offers) {
    this.offers = offers;
  }

  public GlobalDto room(String room) {
    this.room = room;
    return this;
  }

  /**
   * Get room
   * @return room
   */
  
  @Schema(name = "room", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("room")
  public String getRoom() {
    return room;
  }

  public void setRoom(String room) {
    this.room = room;
  }

  public GlobalDto roomLabel(String roomLabel) {
    this.roomLabel = roomLabel;
    return this;
  }

  /**
   * Get roomLabel
   * @return roomLabel
   */
  
  @Schema(name = "roomLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomLabel")
  public String getRoomLabel() {
    return roomLabel;
  }

  public void setRoomLabel(String roomLabel) {
    this.roomLabel = roomLabel;
  }

  public GlobalDto rooms(String rooms) {
    this.rooms = rooms;
    return this;
  }

  /**
   * Get rooms
   * @return rooms
   */
  
  @Schema(name = "rooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rooms")
  public String getRooms() {
    return rooms;
  }

  public void setRooms(String rooms) {
    this.rooms = rooms;
  }

  public GlobalDto single(String single) {
    this.single = single;
    return this;
  }

  /**
   * Get single
   * @return single
   */
  
  @Schema(name = "single", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("single")
  public String getSingle() {
    return single;
  }

  public void setSingle(String single) {
    this.single = single;
  }

  public GlobalDto thirdParties(String thirdParties) {
    this.thirdParties = thirdParties;
    return this;
  }

  /**
   * Get thirdParties
   * @return thirdParties
   */
  
  @Schema(name = "thirdParties", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("thirdParties")
  public String getThirdParties() {
    return thirdParties;
  }

  public void setThirdParties(String thirdParties) {
    this.thirdParties = thirdParties;
  }

  public GlobalDto today(String today) {
    this.today = today;
    return this;
  }

  /**
   * Get today
   * @return today
   */
  
  @Schema(name = "today", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("today")
  public String getToday() {
    return today;
  }

  public void setToday(String today) {
    this.today = today;
  }

  public GlobalDto tomorrow(String tomorrow) {
    this.tomorrow = tomorrow;
    return this;
  }

  /**
   * Get tomorrow
   * @return tomorrow
   */
  
  @Schema(name = "tomorrow", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tomorrow")
  public String getTomorrow() {
    return tomorrow;
  }

  public void setTomorrow(String tomorrow) {
    this.tomorrow = tomorrow;
  }

  public GlobalDto twin(String twin) {
    this.twin = twin;
    return this;
  }

  /**
   * Get twin
   * @return twin
   */
  
  @Schema(name = "twin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("twin")
  public String getTwin() {
    return twin;
  }

  public void setTwin(String twin) {
    this.twin = twin;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GlobalDto globalDto = (GlobalDto) o;
    return Objects.equals(this.accessible, globalDto.accessible) &&
        Objects.equals(this.accessibleOrBarrierFree, globalDto.accessibleOrBarrierFree) &&
        Objects.equals(this.addRoom, globalDto.addRoom) &&
        Objects.equals(this.adult, globalDto.adult) &&
        Objects.equals(this.adults, globalDto.adults) &&
        Objects.equals(this.adultsLabel, globalDto.adultsLabel) &&
        Objects.equals(this.brand, globalDto.brand) &&
        Objects.equals(this.child, globalDto.child) &&
        Objects.equals(this.children, globalDto.children) &&
        Objects.equals(this.childrenLabel, globalDto.childrenLabel) &&
        Objects.equals(this.done, globalDto.done) &&
        Objects.equals(this._double, globalDto._double) &&
        Objects.equals(this.family, globalDto.family) &&
        Objects.equals(this.night, globalDto.night) &&
        Objects.equals(this.offers, globalDto.offers) &&
        Objects.equals(this.room, globalDto.room) &&
        Objects.equals(this.roomLabel, globalDto.roomLabel) &&
        Objects.equals(this.rooms, globalDto.rooms) &&
        Objects.equals(this.single, globalDto.single) &&
        Objects.equals(this.thirdParties, globalDto.thirdParties) &&
        Objects.equals(this.today, globalDto.today) &&
        Objects.equals(this.tomorrow, globalDto.tomorrow) &&
        Objects.equals(this.twin, globalDto.twin);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessible, accessibleOrBarrierFree, addRoom, adult, adults, adultsLabel, brand, child, children, childrenLabel, done, _double, family, night, offers, room, roomLabel, rooms, single, thirdParties, today, tomorrow, twin);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GlobalDto {\n");
    sb.append("    accessible: ").append(toIndentedString(accessible)).append("\n");
    sb.append("    accessibleOrBarrierFree: ").append(toIndentedString(accessibleOrBarrierFree)).append("\n");
    sb.append("    addRoom: ").append(toIndentedString(addRoom)).append("\n");
    sb.append("    adult: ").append(toIndentedString(adult)).append("\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    adultsLabel: ").append(toIndentedString(adultsLabel)).append("\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    child: ").append(toIndentedString(child)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    childrenLabel: ").append(toIndentedString(childrenLabel)).append("\n");
    sb.append("    done: ").append(toIndentedString(done)).append("\n");
    sb.append("    _double: ").append(toIndentedString(_double)).append("\n");
    sb.append("    family: ").append(toIndentedString(family)).append("\n");
    sb.append("    night: ").append(toIndentedString(night)).append("\n");
    sb.append("    offers: ").append(toIndentedString(offers)).append("\n");
    sb.append("    room: ").append(toIndentedString(room)).append("\n");
    sb.append("    roomLabel: ").append(toIndentedString(roomLabel)).append("\n");
    sb.append("    rooms: ").append(toIndentedString(rooms)).append("\n");
    sb.append("    single: ").append(toIndentedString(single)).append("\n");
    sb.append("    thirdParties: ").append(toIndentedString(thirdParties)).append("\n");
    sb.append("    today: ").append(toIndentedString(today)).append("\n");
    sb.append("    tomorrow: ").append(toIndentedString(tomorrow)).append("\n");
    sb.append("    twin: ").append(toIndentedString(twin)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

