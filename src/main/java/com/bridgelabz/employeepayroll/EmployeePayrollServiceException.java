package com.bridgelabz.employeepayroll;

@SuppressWarnings("serial")
public class EmployeePayrollServiceException extends Exception {

	public enum EmployeePayrollExceptionType {
		EMPLOYEE_DATA_RETRIEVE_ISSUE, UPDATION_ISSUE, ADDING_NEW_EMPLOYEE_ISSUE;
	}

	public EmployeePayrollExceptionType type;

	public EmployeePayrollServiceException() {

	}

	public EmployeePayrollServiceException(EmployeePayrollExceptionType type, String message) {
		super(message);
		this.type = type;
	}
}
