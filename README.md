# Sample Java application connecting to Azure SQL Database using Azure Active Directory Access Token

Follow these steps to get application working

1. Register application in Azure Active Directory
2. Copy the client ID, client secret, token endpoint of the application
3. Define system environment variables for AD Token Endpoint, ClientID and ClientSecret. This is not to store those values in code. You cam explore other option such as MSI as explained in this repo
3. Set the Active Directory Admin for Azure SQL Server
4. Login to Azure SQL Server using AD admin credentials
3. Add "Name" of the application as registered in Azure AD as an external user by running following command. Run it in the context of   database you wish to connect to
      CREATE USER [your-app-name-as-registered-in-AD] FROM EXTERNAL PROVIDER
4. Give newly added user permissions (SELECT) on the database
5. In Java application, define system environment variables for Database Server Name and Database Name. This is not to store those values in code. You cam explore other option such as MSI as explained in this repo.
