package flex.samples.crm.employee;

import flex.samples.crm.company.Company;

public class Employee
{
	private int employeeId;

	private String firstName;

	private String lastName;

	private String title;

	private String email;

	private String phone;

    private Company company;
	
	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public int getEmployeeId()
	{
		return employeeId;
	}

	public void setEmployeeId(int employeeId)
	{
		this.employeeId = employeeId;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

    public Company getCompany()
    {
        return company;
    }

    public void setCompany(Company company)
    {
        this.company = company;
    }
}
