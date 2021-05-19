package com.bridgelabz.employeepayroll;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class EmployeePayrollService {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO;
	}

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService)
			throws EmployeePayrollServiceException {
		if (ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readDataFromDB();
		return this.employeePayrollList;
	}

	public void addEmployeeToPayroll(List<EmployeePayrollData> employeePayrollList) {
		employeePayrollList.forEach(employeePayrollData -> {
			this.addEmployeeToPayroll(employeePayrollData.id, employeePayrollData.name, employeePayrollData.gender,
					employeePayrollData.salary, employeePayrollData.startDate);
		});
	}

	private void addEmployeeToPayroll(int id, String name, String gender, double salary, LocalDate startDate) {
		employeePayrollList.add(employeePayrollDBService.addEmployee(id, name, gender, salary, startDate));
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollList.size();
		return 0;
	}
}