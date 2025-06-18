/**
 * Represents an employee or system user for assignment in workflows, approvals,
 * purchase creation, and various ERP roles.
 */
package com.Vcidex.StoryboardSystems.CmnMasterPOJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

    // Unique identifiers
    private String user_gid;
    private String employee_gid;
    private String designation_gid;
    private String department_gid;
    private String branch_gid;

    // User metadata
    private String useraccess;
    private String entity_name;
    private String user_name;
    private String employee_joiningdate;
    private String employee_gender;
    private String emp_address;

    // Designation/Role
    private String designation_name;
    private String department_name;
    private String branch_name;

    // User login/status info
    private String user_status;

    // ─── Getters and Setters ───
    public String getUserGid() { return user_gid; }
    public void setUserGid(String user_gid) { this.user_gid = user_gid; }

    public String getUseraccess() { return useraccess; }
    public void setUseraccess(String useraccess) { this.useraccess = useraccess; }

    public String getEntityName() { return entity_name; }
    public void setEntityName(String entity_name) { this.entity_name = entity_name; }

    public String getUserName() { return user_name; }
    public void setUserName(String user_name) { this.user_name = user_name; }

    public String getEmployeeJoiningdate() { return employee_joiningdate; }
    public void setEmployeeJoiningdate(String employee_joiningdate) { this.employee_joiningdate = employee_joiningdate; }

    public String getEmployeeGender() { return employee_gender; }
    public void setEmployeeGender(String employee_gender) { this.employee_gender = employee_gender; }

    public String getEmpAddress() { return emp_address; }
    public void setEmpAddress(String emp_address) { this.emp_address = emp_address; }

    public String getDesignationName() { return designation_name; }
    public void setDesignationName(String designation_name) { this.designation_name = designation_name; }

    public String getDesignationGid() { return designation_gid; }
    public void setDesignationGid(String designation_gid) { this.designation_gid = designation_gid; }

    public String getEmployeeGid() { return employee_gid; }
    public void setEmployeeGid(String employee_gid) { this.employee_gid = employee_gid; }

    public String getBranchName() { return branch_name; }
    public void setBranchName(String branch_name) { this.branch_name = branch_name; }

    public String getUserStatus() { return user_status; }
    public void setUserStatus(String user_status) { this.user_status = user_status; }

    public String getDepartmentGid() { return department_gid; }
    public void setDepartmentGid(String department_gid) { this.department_gid = department_gid; }

    public String getDepartmentName() { return department_name; }
    public void setDepartmentName(String department_name) { this.department_name = department_name; }

    public String getBranchGid() { return branch_gid; }
    public void setBranchGid(String branch_gid) { this.branch_gid = branch_gid; }
}