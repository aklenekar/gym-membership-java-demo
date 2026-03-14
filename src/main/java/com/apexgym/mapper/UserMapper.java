package com.apexgym.mapper;

import com.apexgym.dto.*;
import com.apexgym.entity.User;
import com.apexgym.entity.embeddable.Address;
import com.apexgym.entity.embeddable.EmergencyContact;
import com.apexgym.entity.embeddable.HealthInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDTO toUserDTO(User user);

    @Mapping(target = "dateOfBirth", source = "dateOfBirth", dateFormat = "yyyy-MM-dd")
    PersonalInfoDTO toPersonalInfoDTO(User user);

    EmergencyContactDTO toEmergencyContactDTO(EmergencyContact contact);

    AddressDTO toAddressDTO(Address address);

    HealthInfoDTO toHealthInfoDTO(HealthInfo healthInfo);
}
