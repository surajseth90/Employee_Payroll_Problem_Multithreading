package com.bridgelabz.employeepayrolltest;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.bridgelabz.employeepayroll.EmployeePayrollData;
import com.bridgelabz.employeepayroll.EmployeePayrollService;
import com.bridgelabz.employeepayroll.EmployeePayrollService.IOService;
import com.bridgelabz.employeepayroll.EmployeePayrollServiceException;

public class EmployeePayrollServiceTest {

	@Test
	public void given6Employees_WhenAddedToDB_ShouldMatchEmployeeEntries() throws EmployeePayrollServiceException {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(1, "Jeff Bezos", "M", 100000.0, LocalDate.now()),
				new EmployeePayrollData(2, "Bill Gates", "M", 200000.0, LocalDate.now()),
				new EmployeePayrollData(3, "Mark Zuckerberg", "M", 300000.0, LocalDate.now()),
				new EmployeePayrollData(4, "Sunder Pichai", "M", 400000.0, LocalDate.now()),
				new EmployeePayrollData(5, "Mukesh", "M", 600000.0, LocalDate.now()),
				new EmployeePayrollData(6, "Anil", "M", 700000.0, LocalDate.now()) };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeeToPayroll(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		System.out.println("Duration without thread : " + Duration.between(start, end));
		Assert.assertEquals(6, employeePayrollService.countEntries(IOService.DB_IO));
	}
}