package com.bridgelabz.employeepayrolltest;

import java.time.LocalDate;
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
import io.restassured.specification.RequestSpecification;

public class JSONServerTest {

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	public EmployeePayrollData[] getEmployeePayroll() {
		Response response = RestAssured.get("/EmployeePayroll");
		System.out.println("Employee Payroll entries in json server :\n" + response.asString());
		EmployeePayrollData[] arrayOfEmployeePayroll = new Gson().fromJson(response.asString(),
				EmployeePayrollData[].class);
		return arrayOfEmployeePayroll;
	}

	private Response addContactToJsonServer(EmployeePayrollData employeePayroll) {
		String addressBookJson = new Gson().toJson(employeePayroll);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(addressBookJson);
		return request.post("/EmployeePayroll");
	}

	@Test
	public void givenAddressBookJSONServer_WhenRetrieved_ShouldReturnCount() {
		EmployeePayrollData[] arrayOfEmployeePayroll = getEmployeePayroll();
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployeePayroll));
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(1, entries);
	}

	@Test
	public void givenContact_WhenAdded_ShouldMatch201ResponseAndCount() {
		EmployeePayrollData[] arrayOfEmployeePayroll = getEmployeePayroll();
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployeePayroll));
		EmployeePayrollData arrayOfPersonPayroll = new EmployeePayrollData(0, "Purvi", "F", 0.0, LocalDate.now());
		Response response = addContactToJsonServer(arrayOfPersonPayroll);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		arrayOfPersonPayroll = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
		employeePayrollService.addContactToEmployeePayroll(arrayOfPersonPayroll, IOService.REST_IO);
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(2, entries);
	}

	@Test
	public void givenMultipleContact_WhenAdded_ShouldMatch201ResponseAndCount() {
		EmployeePayrollData[] arrayOfEmployeePayroll = getEmployeePayroll();
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmployeePayroll));
		EmployeePayrollData[] arrayOfPersonPayroll = { new EmployeePayrollData(0, "Purvi", "F", 0.0, LocalDate.now()),
				new EmployeePayrollData(0, "Rahul", "M", 0.0, LocalDate.now()) };
		for (EmployeePayrollData employeePayrollData : arrayOfEmployeePayroll) {
			Response response = addContactToJsonServer(employeePayrollData);
			int statusCode = response.getStatusCode();
			Assert.assertEquals(201, statusCode);
			employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
			employeePayrollService.addContactToEmployeePayroll(employeePayrollData, IOService.REST_IO);
		}

		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(3, entries);
	}
	
	@Test
	public void givenSalary_WhenUpdated_ShouldMatch200response() {
		EmployeePayrollData[] employeePayroll = getEmployeePayroll();
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(employeePayroll));
		employeePayrollService.updateSalary("Suraj", 600000.0, IOService.REST_IO);
		EmployeePayrollData employeePayrollData = employeePayrollService.getEmployeePayrollData("Suraj");

		String addJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(addJson);
		Response response = request.put("/EmployeePayroll/" + employeePayrollData.id);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200, statusCode);
	}
	
	@Test
	public void givenEmployeePayroll_WhenDeleted_ShouldMatch200ResponseAndCount() {
		EmployeePayrollData[] arrayOfAddressBook = getEmployeePayroll();
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfAddressBook));
		EmployeePayrollData employeePayrollData = employeePayrollService.getEmployeePayrollData("Purvi");
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		Response response = request.delete("/addressbook/" + employeePayrollData.id);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(200, statusCode);

		employeePayrollService.deleteContactPayroll(employeePayrollData.name, IOService.REST_IO);
		long entries = employeePayrollService.countEntries(IOService.REST_IO);
		Assert.assertEquals(2, entries);
	}
}
