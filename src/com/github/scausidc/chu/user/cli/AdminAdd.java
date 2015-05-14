package com.github.scausidc.chu.user.cli;

import com.github.scausidc.chu.user.model.*;
import com.github.scausidc.chu.user.dao.*;
import com.github.scausidc.chu.user.core.*;

public class AdminAdd
{
    public static void main(String[] args)
    {
        System.out.println("Admin Toolkit - AdminAdd");
        System.out.println("WARNING: This tool is used to create admin id, WITHOUT any privilege check.");

        AdminDao adminDao = AdminDao.getInstance();
        Authorizer authorizer = Authorizer.getInstance();

        try
        {
            System.out.println("Add Admin...");


            adminDao.begin();

            String username = args[0];
            System.out.println("Using username:"+username);
            String password = args[1];
            System.out.println("Using password:"+password);

            Admin admin = adminDao.create(username);

            authorizer.setPassword(admin.getId(), password.getBytes("UTF-8"));

            adminDao.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("Operation failed.\nUSAGE: AdminAdd username password");
            System.out.println("And check if:\n* username is unique\n* write access to database\n* your OS/JRE supports UTF-8");
        }
        finally
        {
            adminDao.close();
        }
    }
}
