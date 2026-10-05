package uk.co.whitbread.domain.ports.secondary;


import uk.co.whitbread.domain.model.packages.in.CutOffMinute;
import uk.co.whitbread.domain.model.packages.in.HotelInformationExtended;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.domain.model.packages.out.CutOffExtras;

public interface CutOffOutPort {

  CutOffExtras isCutOffByHotel(PackagesRequest packagesRequest, CutOffMinute cutOffMinutes);


  boolean isOutsideCutOffTime(String code, PackagesRequest packagesRequest,
      CutOffExtras cutOff);

  int availableRooms(PackagesRequest packagesRequest, CutOffExtras cutOff, int available);

  CutOffMinute getCutOffMinute(HotelInformationExtended contentInfo);
}
