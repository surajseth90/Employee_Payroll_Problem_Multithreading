package com.bridgelabz.employeepayroll;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
	private PreparedStatement preparedStatement;
	private static int connectionCounter =0;
	private static EmployeePayrollDBService employeePayrollDBService;

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null) {
			employeePayrollDBService = new EmployeePayrollDBService();
		}
		return employeePayrollDBService;
	}

	public static synchronized Connection getConnection() throws SQLException {
		connectionCounter++;
		String jdbcURL = "jdbc:mysql://localhost:3306/employee_payroll_service";
		String userName = "root";
		String password = "Surajmysql@90";
		Connection connectionn;
		connectionn = DriverManager.getConnection(jdbcURL, userName, password);
		return connectionn;
	}

	public List<EmployeePayrollData> readDataFromDB() throws EmployeePayrollServiceException {
		String sql = "SELECT * FROM employee_payroll;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				int emp_id = resultSet.getInt("emp_id");
				String name = resultSet.getString("name");
				String gender = resultSet.getString("Gender");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(emp_id, name, gender, salary, startDate));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new EmployeePayrollServiceException(
					EmployeePayrollServiceException.EmployeePayrollExceptionType.EMPLOYEE_DATA_RETRIEVE_ISSUE,
					"Unable to read data from database!!");
		}
		return employeePayrollList;
	}

	public EmployeePayrollData addEmployee(int emp_id, String name, String gender, double salary, LocalDate startDate) {
		int employeeId = -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format(
				"INSERT INTO employee_payroll (name,Gender,salary,start) VALUES ('%s','%s','%s','%s')", name, gender,
				salary, Date.valueOf(startDate));
		try (Connection connection = this.getConnection();) {
			preparedStatement = connection.prepareStatement(sql);
			int rowAffected = preparedStatement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayrollData(employeeId, name, gender, salary, startDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return employeePayrollData;
	}
}