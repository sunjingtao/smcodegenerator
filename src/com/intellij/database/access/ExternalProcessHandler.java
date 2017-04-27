package com.intellij.database.access;

import com.intellij.database.dataSource.DataSourceManagerEx;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.psi.DbDataSourceElement;
import com.intellij.database.psi.DbSchemaElement;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.ide.passwordSafe.PasswordStorage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.io.IOException;

public class ExternalProcessHandler {
    private static String externalProcessPath = null;
    private static File dataFile = null;
    private static Process externalProcess = null;
    private static DbSchemaElement schemaElement;
    private static Project project;

    static {
        String classPath = System.getProperty("java.class.path");
        classPath = classPath.split(";")[0];
        classPath = classPath.substring(0,classPath.lastIndexOf("\\"));
        classPath = classPath.substring(0,classPath.lastIndexOf("\\"));
        externalProcessPath = classPath + "\\plugins\\DatabaseTools\\smcg.exe";
        dataFile = new File(classPath.substring(0,classPath.indexOf("\\"))+"\\smcg\\trans.data");
    }

    public static void start(DbSchemaElement schemaElement,Project project) {
        ExternalProcessHandler.schemaElement = schemaElement;
        ExternalProcessHandler.project = project;
        startExternalProcess();
    }

    private static void startExternalProcess() {
        Runtime runtime = Runtime.getRuntime();
        try {
            if(externalProcess != null){
                externalProcess.destroy();
            }
            FileUtil.writeToFile(dataFile,getTransData());
            externalProcess = runtime.exec(externalProcessPath);
        } catch (IOException e) {
            Messages.showErrorDialog("cannot start external process", "SMCG");
        }
    }

    private static String getTransData() {
        DbDataSourceElement sourceElement = schemaElement.getDataSource();
        StringBuilder builder = new StringBuilder();
        builder.append("databaseType$");
        builder.append(sourceElement.getDatabaseProductName());
        builder.append(",databaseVersion$");
        builder.append(sourceElement.getDatabaseProductVersion());
        builder.append(",url$");
        builder.append(sourceElement.getConnectionInfo().getEffectiveUrl());
        LocalDataSource dataSource = getSelectedDataSource(sourceElement.getUniqueId());
        if(dataSource!=null) {
            builder.append(",userName$");
            builder.append(dataSource.getUsername());
            builder.append(",password$");
            builder.append(getDataSourcePassword(dataSource));
        }
        return builder.toString();
    }

    private static String getDataSourcePassword(LocalDataSource dataSource) {
        String password = null;
        try {
            PasswordStorage storage = DatabaseCredentials.getInstance().getPasswordStorageInner(dataSource, DatabaseCredentials.StorageType.MASTER_KEY);
            password = storage.getPassword(project,DatabaseCredentials.class,"db:pwd@" + dataSource.getUniqueId());
            if(password != null && !"".equals(password))return password;
            storage = DatabaseCredentials.getInstance().getPasswordStorageInner(dataSource, DatabaseCredentials.StorageType.EMBEDDED);
            password = storage.getPassword(project,DatabaseCredentials.class,"db:pwd@" + dataSource.getUniqueId());
            if(password != null && !"".equals(password))return password;
            storage = DatabaseCredentials.getInstance().getPasswordStorageInner(dataSource, DatabaseCredentials.StorageType.MEMORY);
            password = storage.getPassword(project,DatabaseCredentials.class,"db:pwd@" + dataSource.getUniqueId());
            if(password != null && !"".equals(password))return password;
        } catch (PasswordSafeException e) {
            Messages.showErrorDialog("cannot get password", "SMCG");
        }
        return password;
    }

    private static LocalDataSource getSelectedDataSource(String id){
        return (LocalDataSource)DataSourceManagerEx.getInstanceEx(project).getDataSourceByID(id);
    }

}