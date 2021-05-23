
package com.bridgelabz.employeepayroll;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData {
	public int id;
	public Integer empId;
	public String name;
	public String gender;
	public double salary;
	public LocalDate startDate;
	public String date;
	

	public EmployeePayrollData(int emp_id, String name, String gender, double salary, LocalDate startDate) {
		super();
		this.id = emp_id;
		this.name = name;
		this.gender = gender;
		this.salary = salary;
		this.startDate = startDate;
	}
	
	public EmployeePayrollData(Integer empId,String name, String gender, double salary, String date) {
		this.empId =empId;
		this.name = name;
		this.gender = gender;
		this.salary = salary;
		this.date = date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(salary);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayrollData other = (EmployeePayrollData) obj;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
	
	

	@Override
	public String toString() {
		return "EmployeePayrollData [emp_id=" + id + ", name=" + name + ", gender=" + gender + ", salary=" + salary
				+ ", startDate=" + startDate + "]";
	}
}