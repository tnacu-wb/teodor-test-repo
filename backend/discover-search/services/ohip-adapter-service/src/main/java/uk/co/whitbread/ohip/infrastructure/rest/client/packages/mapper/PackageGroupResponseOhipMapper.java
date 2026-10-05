package uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.HotelPackageGroupsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageCodeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupsInfo;
import uk.co.whitbread.ohip.domain.model.packages.in.PackageCodeRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackageGroupRequest;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageCodes;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageGroups;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageGroupsResponse;


@Component
@RequiredArgsConstructor
public class PackageGroupResponseOhipMapper {

  public PackageGroupsResponse toModel(List<PackageGroupsInfo> packageGroupsInfos,
      PackageGroupRequest packageGroupRequest) {

    List<HotelPackageGroupsType> packageGroupList = extractPackageGroupList(packageGroupsInfos);
    List<PackageGroups> packageGroups;

    if (!ObjectUtils.isEmpty(packageGroupRequest.getPackageGroupList())
        && packageGroupRequest.getPackageGroupList().stream().anyMatch(StringUtils::isNotEmpty)) {
      packageGroups = filterByGroupList(packageGroupList,
          packageGroupRequest.getPackageGroupList());
    } else {
      packageGroups = filterByPackageCodes(packageGroupList,
          packageGroupRequest.getPackageCodeList());
    }

    return PackageGroupsResponse.builder()
        .packagesGroup(packageGroups)
        .build();
  }

  // Extracts all package groups from the input DTO
  private List<HotelPackageGroupsType> extractPackageGroupList(List<PackageGroupsInfo> ohipDtos) {
    return Optional.ofNullable(ohipDtos)
        .orElse(Collections.emptyList())
        .stream()
        .map(PackageGroupsInfo::getPackageGroupList)
        .filter(Objects::nonNull)
        .flatMap(pkgGroupList -> {
          List<HotelPackageGroupsType> groups = pkgGroupList.getPackageGroups();
          return groups == null ? Stream.empty() : groups.stream();
        })
        .toList();
  }

  private List<PackageGroups> filterByGroupList(List<HotelPackageGroupsType> packageGroupList,
      Set<String> requestedGroupCodes) {
    return packageGroupList.stream()
        .filter(Objects::nonNull)
        .flatMap(pkg -> Optional.ofNullable(pkg.getPackageGroup())
            .orElse(Collections.emptyList())
            .stream())
        .filter(pkgGroup -> requestedGroupCodes.contains(pkgGroup.getCode()))
        .map(this::mapToPackageGroups)
        .toList();
  }

  private List<PackageGroups> filterByPackageCodes(List<HotelPackageGroupsType> packageGroupList,
      Set<PackageCodeRequest> requestedPackageCodes) {
    return packageGroupList.stream()
        .filter(Objects::nonNull)
        .flatMap(pkg -> Optional.ofNullable(pkg.getPackageGroup())
            .orElse(Collections.emptyList())
            .stream())
        .filter(pkgGroup -> matchMembers(pkgGroup.getMembersList(), requestedPackageCodes))
        .map(this::mapToPackageGroups)
        .toList();
  }

  // Checks if group members match requested package codes
  private boolean matchMembers(List<PackageCodeType> members,
      Set<PackageCodeRequest> requestedPackageCodes) {
    if (ObjectUtils.isEmpty(members)) {
      return false;
    }
    if (isAllPackagesRequested(requestedPackageCodes)) {
      return true;
    }
    Set<String> memberCodes = members.stream()
        .filter(Objects::nonNull)
        .map(PackageCodeType::getCode)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    return requestedPackageCodes.stream()
        .map(req -> Optional.ofNullable(req.getPackageCodes())
            .orElse(Collections.emptySet()))
        .anyMatch(
            codeSet -> codeSet.size() == memberCodes.size() && codeSet.containsAll(memberCodes));
  }

  // If no specific package codes are requested, include all groups with members
  private boolean isAllPackagesRequested(Set<PackageCodeRequest> requestedPackageCodes) {
    return ObjectUtils.isEmpty(requestedPackageCodes)
        || requestedPackageCodes.stream()
        .allMatch(req -> ObjectUtils.isEmpty(req.getPackageCodes()));
  }

  // Maps PackageGroupType to domain model
  private PackageGroups mapToPackageGroups(PackageGroupType pkgGroup) {
    List<PackageCodes> packageCodes = Optional.ofNullable(pkgGroup.getMembersList())
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .map(member -> PackageCodes.builder()
            .packageCode(member.getCode())
            .packageDescription(member.getDescription())
            .build())
        .toList();

    return PackageGroups.builder()
        .packageGroup(pkgGroup.getCode())
        .packageGroupDescription(pkgGroup.getDescription())
        .packageCodes(packageCodes)
        .build();
  }
}