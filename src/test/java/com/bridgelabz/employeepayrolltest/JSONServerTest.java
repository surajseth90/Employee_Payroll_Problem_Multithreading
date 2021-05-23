package com.bridgelabz.employeepayrolltest;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bridgelabz.employeepayroll.EmployeePayrollData;
import com.bridgelabz.employeepayroll.EmployeePayrollService;
import com.bridgelabz.employeepayroll.EmployeePayrollService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class JSONServerTest {
	
	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	public EmployeePayrollData[] getEmployeePayroll() {
		Response response = RestAssured.get("/EmployeePayroll");
		System.out.println("Employee Payroll entries in json server :\n" + response.asString());
		EmployeePayrollData[] arrayOfEmployeePayroll = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
		return arrayOfEmployeePayroll;
	}
	
	@Test
	public void givenAddressBookJSONServer_WhenRetrieved_ShouldReturnCount() {
		EmployeePayrollData[] arrayOfEmployeePayroll = getEmployeePayroll();
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployeePayroll));
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(1, entries);
	}
}

