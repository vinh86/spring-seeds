package com.example.demo.mapping5_typeConversion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper5 {

	@Mapping(target="employeeStartDt", source = "entity.startDt", dateFormat = "dd-MM-yyyy HH:mm:ss")
	EmployeeDTO employeeToEmployeeDTO(Employee entity);

	@Mapping(target="startDt", source="dto.employeeStartDt", dateFormat="dd-MM-yyyy HH:mm:ss")
	Employee employeeDTOtoEmployee(EmployeeDTO dto);
}
