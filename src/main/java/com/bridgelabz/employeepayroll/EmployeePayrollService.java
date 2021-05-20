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
				System.out.println("employee being added"+Thread.currentThread().getName());
				this.addEmployeeToPayroll(employeePayrollData.id, employeePayrollData.name, employeePayrollData.gender,
						employeePayrollData.salary, employeePayrollData.startDate);

				employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
				System.out.println("employee added"+Thread.currentThread().getName());
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
		return 0;
	}
}