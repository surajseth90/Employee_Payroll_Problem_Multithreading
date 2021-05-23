package com.bridgelabz.employeepayroll;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

	public void addEmployeeToPayrollWithThreads(List<EmployeePayrollData> employeePayrollList) {
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<>();
		employeePayrollList.forEach(employeePayrollData -> {
			Runnable task = () -> {
				employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
				System.out.println("employee being added" + Thread.currentThread().getName());
				this.addEmployeeToPayroll(employeePayrollData.id, employeePayrollData.name, employeePayrollData.gender,
						employeePayrollData.salary, employeePayrollData.startDate);

				employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
				System.out.println("employee added" + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, employeePayrollData.name);
			thread.start();
		});
		while (employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void addEmployeeToPayroll(int id, String name, String gender, double salary, LocalDate startDate) {
		employeePayrollList.add(employeePayrollDBService.addEmployee(id, name, gender, salary, startDate));
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollList.size();
		return employeePayrollList.size();
	}

	public void updateSalaryOfMultipleEmployees(Map<String, Double> employeeSalaryMap) {
		Map<Integer, Boolean> salaryUpdateStatus = new HashMap<>();
		employeeSalaryMap.forEach((employee, salary) -> {
			Runnable salaryUpdate = () -> {
				salaryUpdateStatus.put(employee.hashCode(), false);

				this.updateEmployeeSalary(employee, salary);
				salaryUpdateStatus.put(employee.hashCode(), true);

			};
			Thread thread = new Thread(salaryUpdate, employee);
			thread.start();
		});
		while (salaryUpdateStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public void updateEmployeeSalary(String name, double salary) {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		EmployeePayrollData employeePayrollData = this.getEmployeeData(name);
		if (employeePayrollData != null)
			employeePayrollData.salary = salary;
	}

	private EmployeePayrollData getEmployeeData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollData -> employeePayrollData.name.equalsIgnoreCase(name)).findFirst()
				.orElse(null);
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) {
		List<EmployeePayrollData> employeeDataList = employeePayrollDBService.getEmployeeData(name);
		return employeeDataList.get(0).equals(this.getEmployeeData(name));
	}

	public EmployeePayrollData getEmployeePayrollData(String name) {
		EmployeePayrollData employeePayrollData;
		employeePayrollData = this.employeePayrollList.stream().filter(dataItem -> dataItem.name.equals(name))
				.findFirst().orElse(null);
		return employeePayrollData;
	}

	public void updateSalary(String name, double salary, IOService restIo) {
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.salary =salary;
	}
	
	public void addContactToEmployeePayroll(EmployeePayrollData arrayOfPersonPayroll, IOService restIo) {
		employeePayrollList.add(arrayOfPersonPayroll);
	}

	public void deleteContactPayroll(String name, IOService restIo) {
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		employeePayrollList.remove(employeePayrollData);
	}
}