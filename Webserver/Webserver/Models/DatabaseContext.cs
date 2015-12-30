using System.Data.Entity;

namespace Webserver.Models
{
    public class DatabaseContext : DbContext
    {
        // You can add custom code to this file. Changes will not be overwritten.
        // 
        // If you want Entity Framework to drop and regenerate your database
        // automatically whenever you change your model schema, please use data migrations.
        // For more information refer to the documentation:
        // http://msdn.microsoft.com/en-us/data/jj591621.aspx

        public DatabaseContext() : base("name=WebserverContext")
        {
            Database.SetInitializer(new DatabaseInitilizer());
        }

        public DbSet<User> Users { get; set; }
    }

    public class DatabaseInitilizer : DropCreateDatabaseIfModelChanges<DatabaseContext>
    {
    }
}