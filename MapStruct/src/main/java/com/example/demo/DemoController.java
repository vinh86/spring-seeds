package com.example.demo;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.mapping2_dependencyInjection.SimpleDestination;
import com.example.demo.mapping2_dependencyInjection.SimpleMapper2;
import com.example.demo.mapping2_dependencyInjection.SimpleSource;
import com.example.demo.mapping3_differentFieldNames.Employee;
import com.example.demo.mapping3_differentFieldNames.EmployeeDTO;
import com.example.demo.mapping3_differentFieldNames.EmployeeMapper3;
import com.example.demo.mapping4_childBeans.Division;
import com.example.demo.mapping4_childBeans.EmployeeMapper4;
import com.example.demo.mapping5_typeConversion.EmployeeMapper5;

@Controller()
@RequestMapping("demo")
public class DemoController {

	@Autowired private SimpleMapper2 mapper2;
	@Autowired private EmployeeMapper3 mapper3;
	@Autowired private EmployeeMapper4 mapper4;
	@Autowired private EmployeeMapper5 mapper5;

	@GetMapping("dependencyInjection")
	public ResponseEntity<List<String>> dependencyInjection() {
		SimpleSource source = new SimpleSource();
		source.setName("source name");
		source.setDescription("source description");
		
		SimpleDestination destination = mapper2.sourceToDestination(source);
		
		return ResponseEntity.ok(Arrays.asList(
						String.format("%s - %s", source.getName(), destination.getName()), 
						String.format("%s - %s", source.getDescription(), destination.getDescription())
						));
	}

	@GetMapping("differentFieldNames")
	public ResponseEntity<List<String>> differentFieldNames() {
		Employee source = new Employee();
		source.setId(100);
		source.setName("source name");
		
		EmployeeDTO destination = mapper3.employeeToEmployeeDTO(source);
		
		return ResponseEntity.ok(Arrays.asList(
						String.format("%s - %s", source.getId(), destination.getEmployeeId()),
						String.format("%s - %s", source.getName(), destination.getEmployeeName())
						));
	}

	@GetMapping("childBeans")
	public ResponseEntity<List<String>> childBeans() {
		Division division = new Division();
		division.setId(101);
		division.setName("division name");
		com.example.demo.mapping4_childBeans.Employee source = new com.example.demo.mapping4_childBeans.Employee();
		source.setId(100);
		source.setName("source name");
		source.setDivision(division);
		
		com.example.demo.mapping4_childBeans.EmployeeDTO destination = mapper4.employeeToEmployeeDTO(source);
		
		return ResponseEntity.ok(Arrays.asList(
				String.format("%s - %s", source.getId(), destination.getEmployeeId()),
				String.format("%s - %s", source.getName(), destination.getEmployeeName()),
				String.format("%s - %s", source.getDivision().getId(), destination.getDivision().getId()),
				String.format("%s - %s", source.getDivision().getName(), destination.getDivision().getName())
						));
	}

	@GetMapping("typeConversion")
	public ResponseEntity<List<String>> typeConversion() {
		com.example.demo.mapping5_typeConversion.Employee source = new com.example.demo.mapping5_typeConversion.Employee();
		source.setStartDt(new Date());
		
		com.example.demo.mapping5_typeConversion.EmployeeDTO destination = mapper5.employeeToEmployeeDTO(source);
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return ResponseEntity.ok(Arrays.asList(
						String.format("%s - %s", format.format(source.getStartDt()), destination.getEmployeeStartDt())
						));
	}
}
