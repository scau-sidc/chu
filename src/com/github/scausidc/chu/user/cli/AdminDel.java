package com.github.scausidc.chu.user.cli;

import com.github.scausidc.chu.user.model.*;
import com.github.scausidc.chu.user.dao.*;
import com.github.scausidc.chu.user.core.*;

public class AdminDel
{
    public static void main(String[] args)
    {
        System.out.println("Admin Toolkit - AdminDel");
        System.out.println("WARNING: This tool is used to drop admin id, WITHOUT any privilege check.");

        AdminDao adminDao = AdminDao.getInstance();

        try
        {
            System.out.println("Add Admin...");


            adminDao.begin();

            String username = args[0];
            System.out.println("Using username:"+username);
            //String password = args[1];
            //System.out.println("Using password:"+password);

            adminDao.delete(adminDao.forUname(username));

            adminDao.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("Operation failed.\nUSAGE: AdminDel username");
            System.out.println("And check if:\n* username is unique\n* write access to database");
        }
        finally
        {
            adminDao.close();
        }
    }
}
