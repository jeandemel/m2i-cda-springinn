package fr.m2i.cda.springinn.controller.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import fr.m2i.cda.springinn.controller.dto.RegisterCustomerDTO;
import fr.m2i.cda.springinn.controller.dto.SimpleCustomerDTO;
import fr.m2i.cda.springinn.entity.Customer;

@Mapper(componentModel = "spring",
     unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    Customer toEntity(RegisterCustomerDTO dto);

    SimpleCustomerDTO toSimpleCustomer(Customer entity);
}
