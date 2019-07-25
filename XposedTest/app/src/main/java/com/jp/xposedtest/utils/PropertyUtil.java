
package com.jp.xposedtest.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertyUtil
{

    // 根据 File产生property
    public static Properties getPropObjFromFile(String filePath)
    {
        Properties objProp = new Properties();
        File file = new File(filePath);
        InputStream inStream = null;
        try
        {
            inStream = new FileInputStream(file);
            objProp.load(inStream);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (inStream != null)
        {
            try
            {
                inStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return objProp;
    }

    public static String readValue(String filePath, String key)
    {
        Properties props = new Properties();
        InputStream in = null;
        try
        {
            in = new BufferedInputStream(new FileInputStream(
                    filePath));
            props.load(in);
            String value = props.getProperty(key);
            System.out.println(key + value);
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return value;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            return null;
        }
    }

    public static String readValue(String filePath, String key, String defaultValue)
    {
        Properties props = new Properties();
        InputStream in = null;
        try
        {
            in = new BufferedInputStream(new FileInputStream(
                    filePath));
            props.load(in);
            String value = props.getProperty(key);
            System.out.println(key + value);
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if(value == null){
                return defaultValue;
            }
            return value;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            return defaultValue;
        }
    }

    public static List<Map> readPropertiesToList(String filePath)
    {
        Properties props = new Properties();
        List<Map> propertyList = new ArrayList<Map>();
        InputStream in = null;
        try
        {
            in = new BufferedInputStream(new FileInputStream(
                    filePath));
            props.load(in);
            Enumeration en = props.propertyNames();
            while (en.hasMoreElements())
            {
                String key = (String) en.nextElement();
                String property = props.getProperty(key);
                Map<String, String> m = new HashMap<String, String>();
                m.put(key, property);
                propertyList.add(m);
                // System.out.println(key + property);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (in != null)
        {
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return propertyList;
    }

    public static Map<String, String> readPropertiesToMap(String filePath)
    {
        // 检测文件是否存在
        File file = new File(filePath);
        if (!file.exists())
        {
            return null;
        }

        Properties props = new Properties();
        Map<String, String> propertyMap = new HashMap<String, String>();
        InputStream in = null;
        try
        {
            in = new BufferedInputStream(new FileInputStream(
                    filePath));
            props.load(in);
            Enumeration en = props.propertyNames();
            while (en.hasMoreElements())
            {
                String key = (String) en.nextElement();
                String property = props.getProperty(key);
                propertyMap.put(key, property);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (in != null)
        {
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return propertyMap;
    }

    public static void writeProperties(String filePath, String parameterName,
            String parameterValue)
    {
        File file = new File(filePath);
        if (!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Properties prop = new Properties();
        InputStream fis = null;
        OutputStream fos = null;
        try
        {
            fis = new FileInputStream(filePath);
            prop.load(fis);
            fos = new FileOutputStream(filePath);
            prop.setProperty(parameterName, parameterValue);
            prop.store(fos, "Update '" + parameterName + "' value");
        }
        catch (IOException e)
        {
            System.err.println("Visit " + filePath + " for updating "
                    + parameterName + " value error");
        }
        if (fis != null)
        {
            try
            {
                fis.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if (fos != null)
        {
            try
            {
                fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void writeProperties(String filePath, Properties prop)
    {
        OutputStream fos = null;
        try
        {
            if (prop != null)
            {
                fos = new FileOutputStream(filePath);
                prop.store(fos, "Properties file config");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (fos != null)
        {
            try
            {
                fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, String> init(String filePath)
    {
        Map<String, String> m = PropertyUtil.readPropertiesToMap(filePath);
        System.out.println(m);
        return m;

    }

    public static boolean containsKey(Map<String, String> maps, String key)
    {
        if (maps == null || key == null)
        {
            return false;
        }
        else
        {
            return maps.containsKey(key);
        }
    }

    public static boolean containsValue(Map<String, String> maps, String vaule)
    {
        if (maps == null || vaule == null)
        {
            return false;
        }
        else
        {
            return maps.containsValue(vaule);
        }
    }

}
