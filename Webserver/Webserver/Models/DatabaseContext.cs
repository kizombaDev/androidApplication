/* This file contains the DatabaseContext class.
* This database context is the entry point to the Entity Framework and provides access to the users table.
*
* Datei: DatabaseContext.cs Autor: Ramandeep Singh
* Datum: 23.12.2015 Version: 1.0
*/

using System.Data.Entity;

namespace Webserver.Models
{
    /// <summary>
    ///     This class should be used to get acces to the database and users table.
    /// </summary>
    public class DatabaseContext : DbContext
    {
        public DatabaseContext() : base("name=WebserverContext")
        {
            Database.SetInitializer(new DatabaseInitilizer());
        }

        public DbSet<User> Users { get; set; }

        /// <summary>
        ///     This database initializer is used  to drop all tables from the database and recreate them if the model classes have
        ///     changed.
        /// </summary>
        private class DatabaseInitilizer : DropCreateDatabaseIfModelChanges<DatabaseContext>
        {
        }
    }
}