package com.apexgym.shared.mappers;

import com.apexgym.auth.persistence.User;
import com.apexgym.profile.dto.UserDTO;
import com.apexgym.profile.dto.AddressDTO;
import com.apexgym.profile.dto.EmergencyContactDTO;
import com.apexgym.profile.dto.HealthInfoDTO;
import com.apexgym.profile.dto.PersonalInfoDTO;
import com.apexgym.profile.persistence.embeddable.Address;
import com.apexgym.profile.persistence.embeddable.EmergencyContact;
import com.apexgym.profile.persistence.embeddable.HealthInfo;
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
